/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.core.JcrService;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class PathUtil {

    /**
     * This enum defines the different modes that can be used when request parameters from different sources are
     * combined.
     * 
     * @see PathUtil#combineRequestParams
     */
    public static enum RequestParameterCombineMode {
        IGNORE_CURRENT_REQUEST, MERGE, PREFER_FROM_CURRENT_REQUEST, PREFER_FROM_MAP
    }

    private static final Logger LOG = LoggerFactory.getLogger(PathUtil.class);;

    /**
     * Creates a map with parameter values by searching for a query string in the given URL any by splitting the values.
     * 
     * @param url
     *            a url
     * @return a map with parameters as key/value pairs.
     */
    public static Map<String, String[]> buildParameterMapFromUrl(final String url) {
        Map<String, String[]> map = null;
        final int qmIndex = url.indexOf('?');
        if (qmIndex != -1) {
            String queryString = url.substring(qmIndex + 1);
            final int hashIndex = queryString.indexOf('#');
            if (hashIndex != -1) {
                queryString = queryString.substring(0, hashIndex);
            }
            final String[] parts = queryString.split("&");
            if (parts.length > 0) {
                map = new HashMap<String, String[]>();
                for (final String part : parts) {
                    final String[] keyVal = part.split("=");
                    if (keyVal.length == 2) {
                        smartAddToRequestParameterMap(map, keyVal[0], keyVal[1]);
                    } else if (keyVal.length == 1) {
                        smartAddToRequestParameterMap(map, keyVal[0], null);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Builds the query string.
     * 
     * @param requestParameterMap
     *            a map containing all request parameters.
     * @return a query string that contains parameters as URL-encoded key/value pairs separated by &amp;-signs but
     *         without leading ?-sign.
     */
    public static String buildQueryString(final Map<String, String[]> requestParameterMap) {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
            for (final String value : entry.getValue()) {
                builder.append(EncodeDecodeUtil.urlEncode(entry.getKey())).append("=")
                        .append(EncodeDecodeUtil.urlEncode(value)).append("&");
            }
        }
        if (builder.length() > 0) {
            // trim last &:
            return builder.substring(0, builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * Merges two request parameter maps into one.
     * 
     * @param currentRequest
     *            current request
     * @param reqParamMap
     *            request parameter map (may be null, in this case this methods just places the parameters from the
     *            current request in to map)
     * @param combineMode
     *            how the values should be merged/combined
     * @return a map that contains parameters from the current request and parameters from the map.
     */
    public static Map<String, String[]> combineRequestParams(final HttpServletRequest currentRequest,
                                                             final Map<String, String[]> reqParamMap,
                                                             final RequestParameterCombineMode combineMode) {

        final Map<String, String[]> mergedMap = new HashMap<String, String[]>();
        if (RequestParameterCombineMode.IGNORE_CURRENT_REQUEST != combineMode) {
            final Enumeration<?> enumr = currentRequest.getParameterNames();
            while (enumr.hasMoreElements()) {
                final String name = (String) enumr.nextElement();
                mergedMap.put(name, currentRequest.getParameterValues(name));
            }
        }

        if (null != reqParamMap) {
            for (final Map.Entry<String, String[]> entry : reqParamMap.entrySet()) {
                if (mergedMap.containsKey(entry.getKey())) {
                    if (RequestParameterCombineMode.PREFER_FROM_MAP == combineMode) {
                        mergedMap.put(entry.getKey(), entry.getValue());
                    } else if (RequestParameterCombineMode.MERGE == combineMode) {
                        final String[] oldValues = mergedMap.get(entry.getKey());
                        final String[] newValues = entry.getValue();
                        final String[] allValues = new String[oldValues.length + newValues.length];
                        System.arraycopy(oldValues, 0, allValues, 0, oldValues.length);
                        System.arraycopy(newValues, 0, allValues, oldValues.length, newValues.length);
                        mergedMap.put(entry.getKey(), allValues);
                    } // nothing to do for case RequestParameterCombineMode.PREFER_FROM_CURRENT_REQUEST
                } else {
                    mergedMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return mergedMap;
    }

    /**
     * Gets the relative publisher url from a given content path. The function takes care of url shortening and vanity
     * path (for publish mode), but there will be no selectors, anchors, parameter or javascript (this should be handled
     * in other methods e.g. linkWriter). This function returns 'only' the pure transformed content path.
     * 
     * @param contentPath
     *            the content path
     * @param httpRequest
     *            the request
     * @return the relative publisher url, maybe null
     */
    public static String getRelativePublisherUrl(final String contentPath,
                                                 final HttpServletRequest httpRequest) {
        if (StringUtils.isEmpty(contentPath)) {
            LOG.debug("getRelativePublisherUrl(contentPath,httpRequest) : contentPath is empty!");
            return null;
        }

        final SlingHttpServletRequest request = (SlingHttpServletRequest) httpRequest;
        final ResourceResolver resolver = request.getResourceResolver();
        final PageManager pageManager = resolver.adaptTo(PageManager.class);
        final Boolean isAuthorInstance = WCMModeUtil.isAuthorInstance(request);

        final Page page = pageManager.getContainingPage(contentPath);
        if (page == null) {
            LOG.error("page for contentPath '" + contentPath + "' is null");
            return null;
        }

        // use the vanityPath only in publisher and if not manually disabled by request parameter
        final boolean useVanityPath = !isAuthorInstance && !"true".equals(request.getParameter("disableVanityPath"));
        if (useVanityPath) {
            final String vanityPath = VanityPathUtil.getVanityPathByPage(page);
            // only return a vaity path if it is maintained
            if (StringUtils.isNotEmpty(vanityPath)) {
                return vanityPath;
            }
        }

        String relativePublisherUrl = page.getPath();

        if (!relativePublisherUrl.endsWith(".html") && !relativePublisherUrl.endsWith("/")) {
            // mapping the index-level to / would fail otherwise.
            relativePublisherUrl = relativePublisherUrl + ".html";
        }
        // on publisher use the mapping to crop everyting before '\index' instead of using the complete content
        // path '\content\...\index'
        if (!isAuthorInstance) {
            relativePublisherUrl = resolver.map(request, relativePublisherUrl);
        }
        return relativePublisherUrl;
    }

    /**
     * Counts the slashes in the given path and returns the count.
     * 
     * @param path
     *            The path, the level is to be determined for.
     * @return The level of the given path.
     */
    public static int levelOfPath(final String path) {
        // Count the slashes to figure out the level of truncation.
        int slashCount = 0;
        int positionOfSlash = 0;
        while (positionOfSlash >= 0) {
            positionOfSlash = path.indexOf('/', positionOfSlash + 1);
            slashCount++;
        }
        return slashCount;
    }

    /**
     * Helper method that adds a new request parameter to request-parameter-map. Supports mulit-value parameters. This
     * function also URL-decodes the value prior to adding them to the map.
     * 
     * @param map
     *            the map to be added to.
     * @param paramName
     *            the parameter name
     * @param paramValue
     *            the parameter value
     */
    private static void smartAddToRequestParameterMap(final Map<String, String[]> map,
                                                      final String paramName,
                                                      final String paramValue) {
        final String paramNameDecoded = EncodeDecodeUtil.urlDecode(paramName);
        String paramValueDecoded = null;
        if (null != paramValue) {
            paramValueDecoded = EncodeDecodeUtil.urlDecode(paramValue);
        }

        if (map.containsKey(paramNameDecoded)) {
            if (null != paramValueDecoded) {
                final String[] oldValues = map.get(paramNameDecoded);
                if (null != oldValues) {
                    final String[] allValues = new String[oldValues.length + 1];
                    System.arraycopy(oldValues, 0, allValues, 0, oldValues.length);
                    allValues[allValues.length - 1] = paramValueDecoded;
                    map.put(paramNameDecoded, allValues);
                } else {
                    map.put(paramNameDecoded, new String[] { paramValueDecoded });
                }
            }
        } else {
            if (null != paramValueDecoded) {
                map.put(paramNameDecoded, new String[] { paramValueDecoded });
            } else {
                map.put(paramNameDecoded, null);
            }
        }

    }

    /**
     * This method combines two Paths by switching the Page path to a given branch. E.G. calling
     * switchToTreeBranch("/content/en/", "/content/de/index/vehicles.html"); will result in the new path
     * "/content/en/index/vehicles.html". If the Branch level is higher then the level of the currentPagePath, the
     * branch is returned.
     * 
     * @param otherBranch
     *            The branch the link should be rewritten to.
     * @param currentPagePath
     *            Path of the current Page.
     * @return Path of the sister page, in the other tree branch.
     */
    public static String switchToTreeBranch(final String otherBranch,
                                            final String currentPagePath) {
        String target = otherBranch;

        if (!StringUtil.endsWith(target, '/')) {
            target += "/";
        }

        final int slashCount = levelOfPath(target);

        int positionOfSlash = 0;
        for (int slash = 0; slash < slashCount - 1; slash++) {
            positionOfSlash = currentPagePath.indexOf("/", positionOfSlash + 1);
        }
        if (positionOfSlash < 0) {
            return target;
        }
        return target + currentPagePath.substring(positionOfSlash + 1);
    }

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PathUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }

    /**
     * Helper class to resolve resource from absolut URL.
     * 
     * @author smaurer, namics ag
     * @since GMWP Release 2.6
     */
    private static final class WrappedHttpServletRequest implements HttpServletRequest {

        private final URL url;

        /**
         * Instantiates a new wrapped http servlet request.
         * 
         * @param url
         *            the url
         * @throws MalformedURLException
         *             the malformed url exception
         */
        public WrappedHttpServletRequest(final String url) throws MalformedURLException {
            this.url = new URL(url);
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getRequestURI()
         */
        @Override
        public String getRequestURI() {
            return this.url.getPath();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getServerPort()
         */
        @Override
        public int getServerPort() {

            return this.url.getPort();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getServerName()
         */
        @Override
        public String getServerName() {
            return this.url.getHost();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getScheme()
         */
        @Override
        public String getScheme() {
            return this.url.getProtocol();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
         */
        @Override
        public Object getAttribute(final String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getAttributeNames()
         */
        @Override
        public Enumeration<?> getAttributeNames() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getCharacterEncoding()
         */
        @Override
        public String getCharacterEncoding() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
         */
        @Override
        public void setCharacterEncoding(final String env) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getContentLength()
         */
        @Override
        public int getContentLength() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getContentType()
         */
        @Override
        public String getContentType() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getInputStream()
         */
        @Override
        public ServletInputStream getInputStream() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
         */
        @Override
        public String getParameter(final String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getParameterNames()
         */
        @Override
        public Enumeration<?> getParameterNames() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
         */
        @Override
        public String[] getParameterValues(final String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getParameterMap()
         */
        @Override
        public Map<?, ?> getParameterMap() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getProtocol()
         */
        @Override
        public String getProtocol() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getReader()
         */
        @Override
        public BufferedReader getReader() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getRemoteAddr()
         */
        @Override
        public String getRemoteAddr() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getRemoteHost()
         */
        @Override
        public String getRemoteHost() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
         */
        @Override
        public void setAttribute(final String name,
                                 final Object o) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
         */
        @Override
        public void removeAttribute(final String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getLocale()
         */
        @Override
        public Locale getLocale() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getLocales()
         */
        @Override
        public Enumeration<?> getLocales() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#isSecure()
         */
        @Override
        public boolean isSecure() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
         */
        @Override
        public RequestDispatcher getRequestDispatcher(final String path) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
         */
        @Override
        public String getRealPath(final String path) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getRemotePort()
         */
        @Override
        public int getRemotePort() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getLocalName()
         */
        @Override
        public String getLocalName() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getLocalAddr()
         */
        @Override
        public String getLocalAddr() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.ServletRequest#getLocalPort()
         */
        @Override
        public int getLocalPort() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getAuthType()
         */
        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getCookies()
         */
        @Override
        public Cookie[] getCookies() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
         */
        @Override
        public long getDateHeader(final String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
         */
        @Override
        public String getHeader(final String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
         */
        @Override
        public Enumeration<?> getHeaders(final String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
         */
        @Override
        public Enumeration<?> getHeaderNames() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
         */
        @Override
        public int getIntHeader(final String name) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getMethod()
         */
        @Override
        public String getMethod() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getPathInfo()
         */
        @Override
        public String getPathInfo() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
         */
        @Override
        public String getPathTranslated() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getContextPath()
         */
        @Override
        public String getContextPath() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getQueryString()
         */
        @Override
        public String getQueryString() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
         */
        @Override
        public String getRemoteUser() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
         */
        @Override
        public boolean isUserInRole(final String role) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
         */
        @Override
        public Principal getUserPrincipal() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
         */
        @Override
        public String getRequestedSessionId() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getRequestURL()
         */
        @Override
        public StringBuffer getRequestURL() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getServletPath()
         */
        @Override
        public String getServletPath() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
         */
        @Override
        public HttpSession getSession(final boolean create) {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#getSession()
         */
        @Override
        public HttpSession getSession() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
         */
        @Override
        public boolean isRequestedSessionIdValid() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
         */
        @Override
        public boolean isRequestedSessionIdFromCookie() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
         */
        @Override
        public boolean isRequestedSessionIdFromURL() {
            throw new UnsupportedOperationException();
        }

        /*
         * (non-Javadoc)
         * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
         */
        @Override
        public boolean isRequestedSessionIdFromUrl() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Tries to resolve the given URL. Expected format is like http://www.chevrolet.com/all.html
     * 
     * @param jcrService
     *            JCRService
     * @param url
     *            URL to resolve
     * @return GMResource
     */
    public static GMResource resolveExternalizedUrl(final JcrService jcrService,
                                                    final String url) {
        if (StringUtils.isNotBlank(url)) {
            if (StringUtil.startsWith(url, '/')) {
                return new GMResource(jcrService.getResourceResolver().resolve(url));
            } else {
                try {
                    final WrappedHttpServletRequest request = new WrappedHttpServletRequest(url);
                    return new GMResource(jcrService.getResourceResolver().resolve(request, request.getRequestURI()));
                } catch (final MalformedURLException e) {
                    LOG.debug("got malformed url " + url, e);
                }
            }
        }
        return new GMResource(null);
    }

    /**
     * Joins path framents to a full path.
     * 
     * @param pathFragmens
     *            The pathFragemens.
     * @return A full path.
     */
    public static String join(final String... pathFragmens) {
        return StringUtils.join(pathFragmens, "/");
    }
}
