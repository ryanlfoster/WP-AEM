/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.properties.ConfigProperties;
import com.aditya.gmwp.aem.services.config.ConfigService;
import com.aditya.gmwp.aem.services.core.LinkWriterService;
import com.aditya.gmwp.aem.services.core.ServiceProvider;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.utils.uri.UriBuilder;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class ProtocolUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ProtocolUtil.class);
    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_HTTPS = "https";
    private static final String REQ_ATTR_CONFIG_SERVICE = "services.configService";
    private static final String REQ_ATTR_HTTP_HOST = "config.httpHost";
    private static final String REQ_ATTR_HTTPS_HOST = "config.httpsHost";
    private static final String HTML_SUFFIX = ".html";

    /**
     * This request header is expected to be found in secure / HTTPS requests. Since requests are
     * never secure when they reach CQ (because behind the dispatcher only plain HTTP is used) this
     * header has to be added by the web-server if he receives an HTTPS request. E.g. in Apache this
     * can easily be done by adding a mod_headers directive to all HTTPS virtual hosts.
     */
    private static final String REQ_HEADER_SECURE_REQUEST = "x-is-secure-request";
    private static final String REQ_HEADER_SECURE_REQUEST_EXPECTED_VALUE = "true";
    public static final String UNKNOWN_HOST = "UNKOWN";

    /**
     * Builds an absolute URL with HTTPS protocol for the given internal page. Neither
     * file-extension nor query-string will be added to the URL by this method.
     * 
     * @param page
     *            the page
     * @param request
     *            the servlet request
     * @param queryString
     *            request parameters (query-string) that have to be added to the new URL.
     * @param selectors
     *            selectors, that have to be added to the new URL.
     * @param anchor
     *            an anchor string that might be passed in the URL
     * @return absolute HTTPS url.
     */
    @Deprecated
    public static String buildHttpsUrl(final Page page,
                                       final HttpServletRequest request,
                                       final String queryString,
                                       final String selectors,
                                       final String anchor) {
        return buildHttpsUrl(page.adaptTo(Resource.class), request, queryString, selectors, anchor);
    }

    /**
     * Builds an absolute URL with HTTPS protocol for the given internal page. Neither
     * file-extension nor query-string will be added to the URL by this method.
     * 
     * @param res
     *            the page-resource
     * @param request
     *            the servlet request
     * @param queryString
     *            request parameters (query-string) that have to be added to the new URL.
     * @param selectors
     *            selectors, that have to be added to the new URL.
     * @param anchor
     *            an anchor string that might be passed in the URL
     * @return absolute HTTPS url.
     */
    @Deprecated
    public static String buildHttpsUrl(final Resource res,
                                       final HttpServletRequest request,
                                       final String queryString,
                                       final String selectors,
                                       final String anchor) {
        final String httpsHost = getHttpsHostFromConfiguration(request);
        return buildProtocolUrl(res, request, httpsHost, queryString, selectors, anchor);

    }

    /**
     * Builds an absolute URL with HTTP protocol for the given internal page. Neither file-extension
     * nor query-string will be added to the URL by this method.
     * 
     * @param page
     *            the page
     * @param request
     *            the servlet request
     * @param queryString
     *            request parameters (query-string) that have to be added to the new URL.
     * @param selectors
     *            selectors, that have to be added to the new URL.
     * @param anchor
     *            an anchor string that might be passed in the URL
     * @return absolute HTTP url.
     */
    @Deprecated
    public static String buildHttpUrl(final Page page,
                                      final HttpServletRequest request,
                                      final String queryString,
                                      final String selectors,
                                      final String anchor) {
        return buildHttpUrl(page.adaptTo(Resource.class), request, queryString, selectors, anchor);
    }

    /**
     * Builds an absolute URL with HTTP protocol for the given internal page. Neither file-extension
     * nor query-string will be added to the URL by this method.
     * 
     * @param res
     *            the page-resource
     * @param request
     *            the servlet request
     * @param queryString
     *            request parameters (query-string) that have to be added to the new URL.
     * @param selectors
     *            selectors, that have to be added to the new URL.
     * @param anchor
     *            an anchor string that might be passed in the URL
     * @return absolute HTTP url.
     */
    @Deprecated
    public static String buildHttpUrl(final Resource res,
                                      final HttpServletRequest request,
                                      final String queryString,
                                      final String selectors,
                                      final String anchor) {
        final String httpHost = getHttpHostFromConfiguration(request);
        return buildProtocolUrl(res, request, httpHost, queryString, selectors, anchor);
    }

    /**
     * Builds an absolute URL to the given resource, based on the given host. The returned URL will
     * always contain the file-extension <code>.html</code> and eventually selector, query-string
     * and #-anchor. This is deprecated as GMDS 3.8. Functions the same as {@link UriBuilder}, but
     * should use {@link LinkWriterService}.
     * 
     * @param res
     *            the resource where the URL should point to.
     * @param request
     *            the servlet request.
     * @param protocolAndHost
     *            the host (with protocol) of the URL.
     * @param queryString
     *            request parameters (query-string) that have to be added to the new URL.
     * @param selectors
     *            selectors, that have to be added to the new URL.
     * @param anchor
     *            an anchor string
     * @return an URL
     */
    @Deprecated
    private static String buildProtocolUrl(final Resource res,
                                           final HttpServletRequest request,
                                           final String protocolAndHost,
                                           final String queryString,
                                           final String selectors,
                                           final String anchor) {
        final StringBuilder path = new StringBuilder();
        String resourcePath = res.getPath();
        if (resourcePath.endsWith("/" + NameConstants.NN_CONTENT)) {
            // this is a page resource -> remove the /jcr:content path to avoid an ugly URL.
            resourcePath = resourcePath.substring(0, resourcePath.length() - ("/" + NameConstants.NN_CONTENT).length());
        }
        path.append(resourcePath);
        path.append(HTML_SUFFIX); // a workaround; mapping the index-level to / would fail
                                  // otherwise.
        String uri = res.getResourceResolver().map(request, path.toString());

        uri = appendProtocolHostAndPathToUri(protocolAndHost, uri);
        uri = appendSelectorsToUri(selectors, uri);
        uri = appendHtmlSuffixToUri(uri);
        uri = appendQueryStringToUri(queryString, uri);
        uri = appendAnchorToUri(anchor, uri);
        return uri;
    }

    /**
     * Append anchor to uri. Deprecated as of GMDS 3.8. Should use {@link UriBuilder} or
     * {@link LinkModel} instead.
     * 
     * @param anchor
     *            the anchor
     * @param uri
     *            the uri
     * @return the string
     */
    @Deprecated
    private static String appendAnchorToUri(final String anchor,
                                            final String uri) {
        String result = uri;
        if (StringUtils.isNotBlank(anchor)) {
            if (StringUtil.startsWith(anchor, '#')) {
                result = result + anchor;
            } else {
                result = result + "#" + anchor;
            }
        }
        return result;
    }

    /**
     * Append query string to uri. Deprecated as of GMDS 3.8. Should use {@link UriBuilder} or
     * {@link LinkModel} instead.
     * 
     * @param queryString
     *            the query string
     * @param uri
     *            the uri
     * @return the string
     */
    @Deprecated
    private static String appendQueryStringToUri(final String queryString,
                                                 final String uri) {
        String result = uri;
        if (StringUtils.isNotBlank(queryString)) {
            if (StringUtil.startsWith(queryString, '?')) {
                result = result + queryString;
            } else {
                result = result + "?" + queryString;
            }
        }
        return result;
    }

    /**
     * Append protocol host and path to uri. Deprecated as of GMDS 3.8. Should use
     * {@link UriBuilder} instead.
     * 
     * @param protocolAndHost
     *            the protocol and host
     * @param uri
     *            the uri
     * @return the string
     */
    @Deprecated
    private static String appendProtocolHostAndPathToUri(final String protocolAndHost,
                                                         final String uri) {
        String result = uri;
        if (!StringUtil.startsWith(result, '/') && !StringUtil.endsWith(protocolAndHost, '/')) {
            result = protocolAndHost + "/" + result;
        } else if (StringUtil.startsWith(result, '/') && StringUtil.endsWith(protocolAndHost, '/')) {
            result = protocolAndHost + result.substring(1);
        } else {
            result = protocolAndHost + result;
        }
        return result;
    }

    /**
     * Append html suffix to uri. Deprecated as of GMDS 3.8. Should use {@link UriBuilder} or
     * {@link LinkModel} instead.
     * 
     * @param uri
     *            the uri
     * @return the string
     */
    @Deprecated
    private static String appendHtmlSuffixToUri(final String uri) {
        String result = uri;
        if (!result.endsWith(HTML_SUFFIX) && !StringUtil.endsWith(result, '/')) {
            if (StringUtil.endsWith(result, '.')) {
                result = result + "html";
            } else {
                result = result + HTML_SUFFIX;
            }
        }
        return result;
    }

    /**
     * Append selectors to uri. Deprecated as of GMDS 3.8. Should use {@link UriBuilder} or
     * {@link LinkModel} instead.
     * 
     * @param selectors
     *            the selectors
     * @param uri
     *            the uri
     * @return the string
     */
    @Deprecated
    private static String appendSelectorsToUri(final String selectors,
                                               final String uri) {
        String result = uri;
        if (StringUtils.isNotBlank(selectors)) {
            if (result.endsWith(HTML_SUFFIX)) {
                result = result.substring(0, result.length() - HTML_SUFFIX.length());
            }
            if (!StringUtil.startsWith(selectors, '.') && !StringUtil.endsWith(result, '.')) {
                result = result + "." + selectors;
            } else if (StringUtil.startsWith(selectors, '.') && StringUtil.endsWith(result, '.')) {
                result = result + selectors.substring(1);
            } else {
                result = result + selectors;
            }
        }
        return result;
    }

    /**
     * Builds the relative url.
     * 
     * @param res
     *            the res
     * @param request
     *            the request
     * @param queryString
     *            the query string
     * @param selectors
     *            the selectors
     * @param anchor
     *            the anchor
     * @return the string
     */
    @Deprecated
    public static String buildRelativeUrl(final Resource res,
                                          final HttpServletRequest request,
                                          final String queryString,
                                          final String selectors,
                                          final String anchor) {
        String resourcePath = res.getPath();
        if (resourcePath.endsWith("/" + NameConstants.NN_CONTENT)) {
            // this is a page resource -> remove the /jcr:content path to avoid an ugly URL.
            resourcePath = resourcePath.substring(0, resourcePath.length() - ("/" + NameConstants.NN_CONTENT).length());
        }
        String uri = res.getResourceResolver().map(request, resourcePath);
        if (StringUtils.isNotEmpty(selectors)) {
            uri += "." + selectors;
        }

        uri = smartAddDotHtml(uri);

        if (queryString != null) {
            uri += "?" + queryString;
        }

        if (anchor != null) {
            uri += "#" + anchor;
        }
        // >>>

        return uri;
    }

    /**
     * This method render a vanity url to the given path, if a vanity url exist.
     * 
     * @param path
     *            the link path
     * @param page
     *            the target page
     * @param request
     *            the HttpServletRequest
     * @return the corresponding vanity url, if exist.
     */
    @Deprecated
    public static String buildVanityUrl(final String path,
                                        final Page page,
                                        final SlingHttpServletRequest request) {
        if (null != page && null != page.getProperties()) {

            final WCMMode mode = WCMMode.fromRequest(request);
            if (!mode.equals(WCMMode.DISABLED)) {
                return path;
            }

            String vanityPath = VanityPathUtil.getVanityPathByPage(page);
            if (StringUtils.isEmpty(vanityPath)) {
                return path;
            }

            final String selector = VanityPathUtil.getSelectorFromVanityPath(path);
            if (StringUtils.isNotEmpty(selector)) {
                vanityPath = VanityPathUtil.getVanityPathWithSelector(path, selector, vanityPath);
            }
            return vanityPath;
        }
        return path;
    }

    /**
     * Tests whether a link that points to the given resource has to use HTTPS. As of 3.8 release,
     * all pages can be HTTPS.
     * 
     * @param targetPage
     *            the resource where the link should point to.
     * @return whether HTTPS is required.
     */
    public static boolean doesPageRequireHttps(final Page targetPage) {
        final ValueMap props = targetPage.getProperties();
        final String requiredProtocol = props.get("protocol", "");
        return PROTOCOL_HTTPS.equals(requiredProtocol);
    }

    /**
     * Tests whether a link that points to the given resource has to use HTTPS. As of 3.8 release,
     * all pages can be HTTPS.
     * 
     * @param targetResource
     *            the resource where the link should point to.
     * @return whether HTTPS is required.
     */
    public static boolean doesPageRequireHttps(final Resource targetResource) {
        if (targetResource != null && NameConstants.NT_PAGE.equals(targetResource.getResourceType())) {
            final Page page = targetResource.adaptTo(Page.class);
            return doesPageRequireHttps(page);
        }
        return false;
    }

    /**
     * Retrieves the {@link ConfigService} via {@link ServiceProvider}.
     * 
     * @return the {@code ConfigService}.
     */
    private static ConfigService fetchConfigService() {
        return ServiceProvider.INSTANCE.getService(ConfigService.class);
    }

    /**
     * Fetches the HTTP host from the configuration page.
     * 
     * @param request
     *            the servlet request
     * @return configured HTTP host
     */
    public static String getHttpHostFromConfiguration(final HttpServletRequest request) {

        String httpHost = (String) request.getAttribute(REQ_ATTR_HTTP_HOST);
        if (null == httpHost) {

            ConfigService configService = (ConfigService) request.getAttribute(REQ_ATTR_CONFIG_SERVICE);
            if (null == configService) {
                configService = fetchConfigService();
                request.setAttribute(REQ_ATTR_CONFIG_SERVICE, configService);
            }

            final ComponentContext compContext = (ComponentContext) request.getAttribute(ComponentContext.CONTEXT_ATTR_NAME);

            httpHost = configService.getConfigValue(compContext.getPage(), ConfigProperties.HTTP_HOST_PUBLISH);
            if (httpHost == null) {
                LOG.warn("No HTTP-host has been defined on configuration-page!");
                httpHost = UNKNOWN_HOST;
            } else if (StringUtil.endsWith(httpHost, '/')) {
                httpHost = httpHost.substring(0, httpHost.length() - 1);
            }
            request.setAttribute(REQ_ATTR_HTTP_HOST, httpHost);
        }
        return httpHost;
    }

    /**
     * Fetches the HTTPS host from the configuration page.
     * 
     * @param request
     *            the servlet request
     * @return configured HTTPS host
     */
    public static String getHttpsHostFromConfiguration(final HttpServletRequest request) {
        String httpsHost = (String) request.getAttribute(REQ_ATTR_HTTPS_HOST);
        if (null == httpsHost) {

            ConfigService configService = (ConfigService) request.getAttribute(REQ_ATTR_CONFIG_SERVICE);
            if (null == configService) {
                configService = fetchConfigService();
                request.setAttribute(REQ_ATTR_CONFIG_SERVICE, configService);
            }

            final ComponentContext compContext = (ComponentContext) request.getAttribute(ComponentContext.CONTEXT_ATTR_NAME);

            httpsHost = configService.getConfigValue(compContext.getPage(), ConfigProperties.HTTPS_HOST_PUBLISH);
            if (httpsHost == null) {
                LOG.warn("No HTTPS-host has been defined on configuration-page!");
                httpsHost = UNKNOWN_HOST;
            } else if (StringUtil.endsWith(httpsHost, '/')) {
                httpsHost = httpsHost.substring(0, httpsHost.length() - 1);
            }
            request.setAttribute(REQ_ATTR_HTTPS_HOST, httpsHost);
        }
        return httpsHost;
    }

    /**
     * Returns the host with protocol (either http or https) from the configuration page. Decision
     * what to return is made by looking at current request and what protocol is required at the
     * target resource.
     * 
     * @param request
     *            the sling http servlet request
     * @return host with protocol or UNKNOWN_HOST if no host defined on configuration page or
     *         doesn't start with http/https
     */
    public static String getHostWithProtocol(final SlingHttpServletRequest request) {
        return getHostWithProtocol(request, request.getResource());
    }

    /**
     * Returns the host with protocol (either http or https) from the configuration page. Decision
     * what to return is made by looking at current request and what protocol is required at the
     * target resource.
     * 
     * @param request
     *            the sling http servlet request
     * @param target
     *            the target resource
     * @return host with protocol or UNKNOWN_HOST if no host defined on configuration page or
     *         doesn't start with http/https
     */
    public static String getHostWithProtocol(final SlingHttpServletRequest request,
                                             final Resource target) {
        final boolean isHttps = isHttpsRequest(request);
        final boolean requiresHttps = doesPageRequireHttps(target);

        String h = null;
        if ((!isHttps && requiresHttps) || (isHttps && requiresHttps)) {
            h = getHttpsHostFromConfiguration(request);
        } else {
            h = getHttpHostFromConfiguration(request);
        }

        return StringUtils.isNotEmpty(h) && (h.startsWith(PROTOCOL_HTTP) || h.startsWith(PROTOCOL_HTTPS)) ? h : UNKNOWN_HOST;
    }

    /**
     * Determines, whether the current request was made via a HTTPS connections. This is done by
     * checking a request header that has to be set by the web-server it a request is secure.
     * 
     * @param request
     *            the http request
     * @return whether the request was made on a secure (HTTPS) connection.
     * @see #REQ_HEADER_SECURE_REQUEST
     */
    public static boolean isHttpsRequest(final HttpServletRequest request) {
        final String header = request.getHeader(REQ_HEADER_SECURE_REQUEST);
        return header != null && header.equals(REQ_HEADER_SECURE_REQUEST_EXPECTED_VALUE);
    }

    /**
     * Checks, whether a protocol-change is required for the link, and if so, converts the href to
     * an absolute URL with protocol and host. If the given handle contains request-parameters
     * and/or an #-anchor, these will be preserved and added to the newly created URL. The returned
     * URL will always contain a file-extension, even when no protocol-change has been applied.
     * 
     * @param internalLinkPath
     *            the internal link to be eventually converted (without file-extension!)
     * @param request
     *            the servlet request
     * @return see method description
     */
    @Deprecated
    public static String maybeApplyProtocolChange(final String internalLinkPath,
                                                  final SlingHttpServletRequest request) {
        if (internalLinkPath == null) {
            return null;
        }
        Boolean isAuthorInstance = (Boolean) request.getAttribute("isAuthorInstance");
        if (null == isAuthorInstance) {
            // try to determine from WCMMode, but this may lead to wrong results because WCMMode
            // gets
            // overridden in some components.
            isAuthorInstance = Boolean.valueOf(WCMMode.fromRequest(request) != WCMMode.DISABLED);
        }

        if (isAuthorInstance) {
            return smartAddDotHtml(internalLinkPath);
        }

        try {
            final ResourceResolver resolver = request.getResourceResolver();
            final Resource targetResource = resolver.resolve(internalLinkPath);
            if (null == targetResource || targetResource instanceof NonExistingResource) {
                // that is an invalid link...
                return smartAddDotHtml(internalLinkPath);
            }

            String helpUrl = internalLinkPath;

            String anchor = null;
            int index = helpUrl.indexOf('#');
            if (index != -1) {
                anchor = helpUrl.substring(index);
                helpUrl = helpUrl.substring(0, index);
            }
            String queryString = null;
            index = helpUrl.indexOf('?');
            if (index != -1) {
                queryString = helpUrl.substring(index);
            }
            final String selectors = StringUtils.substringAfter(internalLinkPath.replace(HTML_SUFFIX, ""), ".");
            // index = helpHandle.indexOf('.');
            // final int index2 = helpHandle.lastIndexOf('.');
            // if (index < index2) {
            // selectors = helpHandle.substring(index, index2);
            // }

            final boolean isHttps = isHttpsRequest(request);
            final boolean targetPageRequiresHttps = doesPageRequireHttps(targetResource);
            // LOG.warn("\nPROTOCOLUTIL:\nrequest = " + request.getRequestURI() +
            // "\ntargetResource = "
            // + targetResource.getPath() + "\nisHttps = " + isHttps +
            // "\ntargetPageRequiresHttps = "
            // + targetPageRequiresHttps);
            if (isHttps && !targetPageRequiresHttps) {
                return buildHttpUrl(targetResource, request, queryString, selectors, anchor);
            } else if (!isHttps && targetPageRequiresHttps) {
                return buildHttpsUrl(targetResource, request, queryString, selectors, anchor);
            } else {
                return buildRelativeUrl(targetResource, request, queryString, selectors, anchor);
                // return smartAddDotHtml(handle);
            }
        } catch (Exception e) {
            LOG.error("Error: ", e);
            return smartAddDotHtml(internalLinkPath);
        }
    }

    /**
     * Sends a server-side redirect to the current page, but creates an absolute URL that contains
     * protocol and host for HTTP. Please note that this only works if the page is not (yet) cached
     * by the dispatcher! This is deprecated as of GMDS 3.8, please use redirectToHttpOrHttpsPage()
     * instead.
     * 
     * @param currentPage
     *            the current page.
     * @param request
     *            the servlet request.
     * @param response
     *            the servlet response.
     * @throws ServletException
     *             when sending the redirect fails.
     */
    @Deprecated
    // Only used in Fleet Flex
    public static void redirectToHttp(final Page currentPage,
                                      final SlingHttpServletRequest request,
                                      final SlingHttpServletResponse response) throws ServletException {
        final String queryString = request.getQueryString();
        final String selectors = request.getRequestPathInfo().getSelectorString();
        String uri = buildHttpUrl(currentPage.adaptTo(Resource.class), request, queryString, selectors, null);
        uri = smartAddDotHtml(uri);
        try {
            response.sendRedirect(uri);
        } catch (IOException e) {
            throw new ServletException("Unable to redirect user to plain-HTTP page '" + uri + "'.", e);
        }
    }

    /**
     * Sends a server-side redirect to the current page, but creates an absolute URL that contains
     * protocol and host for HTTPS and HTTP. Please note that this only works if the page is not
     * (yet) cached by the dispatcher!
     * 
     * @param currentPage
     *            the current page.
     * @param request
     *            the servlet request.
     * @param response
     *            the servlet response.
     * @throws ServletException
     *             when sending the redirect fails.
     */
    public static void redirectToHttpOrHttpsPage(final Page currentPage,
                                                 final SlingHttpServletRequest request,
                                                 final SlingHttpServletResponse response) throws ServletException {
        // get parameters and selectors.
        final UriBuilder paramUb = StringUtils.isNotBlank(request.getQueryString()) ? new UriBuilder(request.getQueryString()) : null;
        final String selectors = request.getRequestPathInfo().getSelectorString();

        // set up the link model with current page, parameters, and selectors.
        final LinkModel linkModel = new LinkModel(currentPage.getPageTitle(), currentPage.getPath());
        if (paramUb != null) {
            linkModel.setParameters(paramUb.getParameters());
        }
        if (StringUtils.isNotBlank(selectors)) {
            linkModel.setSelectorListAsString(selectors);
        }

        // rewrite the link and get the uri to redirect.
        final SlingBindings slingBindings = (SlingBindings) request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
        final SlingScriptHelper scriptHelper = slingBindings.getSling();
        final LinkWriterService linkwriter = scriptHelper.getService(LinkWriterService.class);
        final HTMLLink link = linkwriter.rewriteLink(request, linkModel);
        final String uri = link.getHref();
        try {
            response.sendRedirect(uri);
        } catch (IOException e) {
            throw new ServletException("Unable to redirect user to HTTP or HTTPs page '" + uri + "'", e);
        }
    }

    /**
     * Sends a server-side redirect to the current page, but creates an absolute URL that contains
     * protocol and host for HTTPS. Please note that this only works if the page is not (yet) cached
     * by the dispatcher! This is deprecated as of GMDS 3.8, please use redirectToHttpOrHttpsPage()
     * instead.
     * 
     * @param currentPage
     *            the current page.
     * @param request
     *            the servlet request.
     * @param response
     *            the servlet response.
     * @throws ServletException
     *             when sending the redirect fails.
     */
    @Deprecated
    // Only used in Fleet Flex.
    public static void redirectToHttps(final Page currentPage,
                                       final SlingHttpServletRequest request,
                                       final SlingHttpServletResponse response) throws ServletException {
        final String queryString = request.getQueryString();
        final String selectors = request.getRequestPathInfo().getSelectorString();
        String uri = buildHttpsUrl(currentPage.adaptTo(Resource.class), request, queryString, selectors, null);
        uri = smartAddDotHtml(uri);
        try {
            response.sendRedirect(uri);
        } catch (IOException e) {
            throw new ServletException("Unable to redirect user to HTTPS page '" + uri + "'.", e);
        }
    }

    /**
     * Adds .html to the given URL/link if not yet present and if if the url does not end with /.
     * 
     * @param url
     *            the url
     * @return url with .html
     */
    @Deprecated
    public static String smartAddDotHtml(final String url) {
        if (url == null) {
            return null;
        }
        if (url.endsWith(HTML_SUFFIX) || url.contains(".html?") || url.contains(".html#") || url.matches(".+/(([#][^#/?]*)?|([?][^#/?]*))$")) {
            return url;
        } else {
            return url + HTML_SUFFIX;
        }
    }

    /**
     * Private constructor, this class provides only static methods.
     */
    private ProtocolUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}
