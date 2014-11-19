/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.wp.aem.global.AEMTemplateInfo;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class VanityPathUtil {

    private static final String GMDS_VANITY_PATH = "gmwpVanityPath";

    /** The Constant EXTENSION_LENGTH. */
    private static final int EXTENSION_LENGTH = 4;

    /** The Constant EXTENSION_POSITION. */
    private static final int EXTENSION_POSITION = 5;

    private static final Logger LOG = LoggerFactory.getLogger(VanityPathUtil.class);

    /**
     * This method builds an absolute vanity path url if it's existing, if not absolute page url is returned.
     * 
     * @param page
     *            the target page
     * @param request
     *            the current request
     * @return the vanity url or url build from current page
     */
    public static String buildAbsoluteVanityUrl(final Page page,
                                                final ServletRequest request) {
        String url = "";
        if (null == page) {
            throw new IllegalArgumentException("Missing parameter: page");
        }
        if (null == request) {
            throw new IllegalArgumentException("Missing parameter: request");
        }

        if (request instanceof SlingHttpServletRequest) {
            url = ProtocolUtil.maybeApplyProtocolChange(page.getPath(), (SlingHttpServletRequest) request);
        } else {
            LOG.error("Unexpected request! Request is not a SlingHttpServletRequest!");
        }

        // quick fix for wrong vanity path concatenation, do nothing, only for relative url's
        if (!url.startsWith("https") || !url.startsWith("http")) {
            final boolean isHttps = ProtocolUtil.isHttpsRequest((HttpServletRequest) request);
            final boolean targetPageRequiresHttps = ProtocolUtil.doesPageRequireHttps(page.adaptTo(Resource.class));

            String host = null;
            if (!isHttps && targetPageRequiresHttps) {
                host = ProtocolUtil.getHttpsHostFromConfiguration((HttpServletRequest) request);
            } else {
                host = ProtocolUtil.getHttpHostFromConfiguration((HttpServletRequest) request);
            }
            url = host + ProtocolUtil.buildVanityUrl(url, page, (SlingHttpServletRequest) request);
        }
        return url;
    }

    /**
     * Gets the selector from vanity path.
     * 
     * @param path
     *            the path
     * @return the selector from vanity path
     */
    public static String getSelectorFromVanityPath(final String path) {
        String selector = "";
        final int dotIndex = path.indexOf('.');
        if (-1 != dotIndex && (dotIndex + EXTENSION_POSITION) < path.length()) {
            selector = path.substring(dotIndex, path.length() - EXTENSION_LENGTH);
        }
        return selector;
    }

    /**
     * Gets the vanityPath from the given page. If the page is a folder the vanityPath of the redirection target is
     * returned.
     * 
     * @param page
     *            the page
     * @return vanityPath the vanityPath for the page. Can be null.
     */
    public static String getVanityPathByPage(final Page page) {
        if (page == null) {
            LOG.error("Vanity path is requested for a non existing page");
            return null;
        }
        if (AEMTemplateInfo.TEMPLATE_FOLDER.matchesTemplate(page)) {
            final String internalLink = page.getProperties().get("internalLink", String.class);
            if (StringUtils.isNotEmpty(internalLink)) {
                final Page linkedPage = page.getPageManager().getPage(internalLink);
                if (linkedPage == null) {
                    LOG.error("This page " + page.getPath() + " redirects to " + internalLink
                            + ", which does not exist.");
                    return null;
                } else {
                    // no call to "return getVanityPathByPage(linkedPage);" because this could cause a stack overflow if
                    // the content is maintained badly (e.g. circular redirects)
                    return linkedPage.getProperties().get(GMDS_VANITY_PATH, String.class);
                }
            } else {
                return page.getProperties().get(GMDS_VANITY_PATH, String.class);
            }
        } else {
            return page.getProperties().get(GMDS_VANITY_PATH, String.class);
        }
    }

    /**
     * Gets the vanity path without selector.
     * 
     * @param path
     *            the path
     * @param selector
     *            the selector
     * @return the vanity path without selector
     */
    public static String getVanityPathWithoutSelector(final String path,
                                                      final String selector) {
        return path.replace(selector, ".");
    }

    /**
     * Gets the vanity path with selector.
     * 
     * @param path
     *            the path
     * @param selector
     *            the selector
     * @param vanityPath
     *            the vanity path
     * @return the vanity path with selector
     */
    public static String getVanityPathWithSelector(final String path,
                                                   final String selector,
                                                   final String vanityPath) {
        String result = vanityPath;
        if (result != null && (path.indexOf('.') + EXTENSION_POSITION) < path.length()) {
            if (result.endsWith(".html")) {
                result = result.substring(0, result.length() - ".html".length());
            }
            result = result + selector + "html";
        }
        return result;
    }

    /**
     * This method determines whether a page uses a vanity path.
     * 
     * @param page
     *            the current page
     * @return page uses vanity path
     */
    public static boolean isVanityPath(final Page page) {
        if (null == page) {
            return false;
        }
        final String vanityPath = page.getProperties().get(GMDS_VANITY_PATH, String.class);
        return StringUtil.startsWith(vanityPath, '/');
    }

    /**
     * default constructor.
     */
    private VanityPathUtil() {
    }
}
