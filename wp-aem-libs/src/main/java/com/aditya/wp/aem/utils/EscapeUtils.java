/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.utils.html.RichTextUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class EscapeUtils {

    /**
     * hidden constructor since class contains only static methods.
     */
    private EscapeUtils() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }

    /**
     * Performs JavaScript escaping on a string.
     * 
     * @param str
     *            input string
     * @return escaped string
     */
    public static String escapeJS(final String str) {
        if (StringUtils.isNotBlank(str)) {
            return StringEscapeUtils.escapeJavaScript(str);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Performs HTML escaping on a string.
     * 
     * @param str
     *            input string
     * @return escaped string
     */
    public static String escapeHTML(final String str) {
        if (StringUtils.isNotBlank(str)) {
            return StringEscapeUtils.escapeHtml(str);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Performs url encoding on a string.
     * 
     * @param str
     *            input string
     * @return encoded string
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     */
    public static String urlencode(final String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF-8");
    }

    /**
     * Performs JavaScript and HTML escaping on a string. This is useful for javascript parts in HTML attributes.
     * 
     * @param str
     *            input string
     * @return escaped string
     */
    public static String escapeJSinHTML(final String str) {
        if (StringUtils.isNotBlank(str)) {
            return StringEscapeUtils.escapeJavaScript(escapeHTML(str));
        }
        return StringUtils.EMPTY;
    }

    /**
     * Performs HTML escaping on a string.
     * 
     * @param str
     *            input string
     * @param request
     *            the request
     * @return escaped string
     */
    public static String escapeHTMLForDisclaimer(final String str, final SlingHttpServletRequest request) {
        if (StringUtils.isNotBlank(str)) {
            return StringEscapeUtils.escapeHtml(RichTextUtil.replaceLinkTags(str, request));
        }
        return StringUtils.EMPTY;
    }
}