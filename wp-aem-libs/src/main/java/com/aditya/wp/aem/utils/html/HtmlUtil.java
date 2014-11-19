/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.html;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class HtmlUtil {

    /**
     * This method applies replacement rules based on a regex at a given <code>String</code>.
     * 
     * @param input
     *            the input string to apply replacement on
     * @param htmlReplacementRules
     *            the list containing <code>HtmlReplacementRules</code>
     * @return the replaced text or untouched text if no <code>HtmlReplacementRules</code> have been specified or
     *         <code>null</code> if input is null
     */
    public static String applyReplacementRules(final String input,
                                               final List<HtmlReplacementRules> htmlReplacementRules) {
        if (null == input) {
            return null;
        }
        String text = input;
        if (null != htmlReplacementRules) {
            for (HtmlReplacementRules rule : htmlReplacementRules) {
                text = text.replaceAll(rule.getRegex(), rule.getReplacement());
            }
        }

        return text;
    }

    /**
     * This method applies replacement rules based on a regex at a given <code>String</code>.
     * 
     * @param input
     *            the input string to apply replacement on
     * @param htmlReplacementRules
     *            one or more <code>HtmlReplacementRules</code>
     * @return the replaced text or untouched text if no <code>HtmlReplacementRules</code> have been specified or
     *         <code>null</code> if input is null
     */
    public static String applyReplacementRules(final String input,
                                               final HtmlReplacementRules... htmlReplacementRules) {
        return applyReplacementRules(input, null != htmlReplacementRules ? Arrays.asList(htmlReplacementRules) : null);
    }

    /**
     * This method gets an <code>HtmlFilter</code> by <code>HtmlFilterType</code>.
     * 
     * @param input
     *            the input string needed for the <code>HtmlFilter.Builder</code>
     * @param htmlFilterType
     *            the <code>HtmlFilterType</code>, if <code>null</code> a default <code>HtmlFilter</code> is built
     * @return the <code>HtmlFilter</code>
     */
    public static HtmlFilter getHtmlFilter(final String input,
                                           final HtmlFilterType htmlFilterType) {

        HtmlFilter filter;
        if (null != htmlFilterType && null != input) {
            filter = new HtmlFilter.Builder(input).tagWhitelist(htmlFilterType.getTagWhitelist())
                    .tagReplacementTable(htmlFilterType.getReplacementTable())
                    .attributeWhitelist(htmlFilterType.getAttributeWhitelist())
                    .htmlReplacementRules(htmlFilterType.getReplacementRules()).build();
        } else if (null == htmlFilterType && null != input) {
            filter = new HtmlFilter.Builder(input).build();
        } else {
            filter = new HtmlFilter.Builder(StringUtils.EMPTY).build();
        }

        return filter;
    }

    /**
     * Helper for execution of filtering.
     * 
     * @param input
     *            the string input
     * @param filterType
     *            filter type
     * @param defaultEscape
     *            html escape if filter does not define
     * @param request
     *            current request can be null
     * @return filtered text
     */
    public static String executeFiltering(final String input,
                                          final HtmlFilterType filterType,
                                          final boolean defaultEscape,
                                          final SlingHttpServletRequest request) {
        String value = input;
        boolean escape = defaultEscape;

        if (StringUtils.isNotBlank(value)) {
            if (null != request) {
                value = RichTextUtil.replaceLinkTags(value, request);
            }

            value = value.trim();
            final HtmlFilter htmlFilter = getHtmlFilter(value, filterType);
            value = htmlFilter.getFilteredOutput().trim();

            if (filterType != null) {
                escape = filterType.getEscape();
            }

            if (escape) {
                value = StringEscapeUtils.escapeHtml(value);
            }
        }

        return value;
    }

    /**
     * Helper for execution of filtering.
     * 
     * @param input
     *            the string input
     * @param filter
     *            name of the filter
     * @param defaultEscape
     *            html escape if filter does not define
     * @param request
     *            the current request
     * @return filtered text
     */
    public static String executeFiltering(final String input,
                                          final String filter,
                                          final boolean defaultEscape,
                                          final SlingHttpServletRequest request) {
        return executeFiltering(input, HtmlFilterType.fromString(filter), defaultEscape, request);
    }

    /**
     * Helper for execution of filtering without replacing link tags.
     * 
     * @param input
     *            the string input
     * @param filterType
     *            the filter type
     * @param defaultEscape
     *            html escape if filter does not define
     * @return filtered text
     */
    public static String executeFiltering(final String input,
                                          final HtmlFilterType filterType,
                                          final boolean defaultEscape) {
        return executeFiltering(input, filterType, defaultEscape, null);
    }

    /**
     * Helper for execution of filtering without replacing link tags.
     * 
     * @param input
     *            the string input
     * @param filter
     *            name of the filter
     * @param defaultEscape
     *            html escape if filter does not define
     * @return filtered text
     */
    public static String executeFiltering(final String input,
                                          final String filter,
                                          final boolean defaultEscape) {
        return executeFiltering(input, HtmlFilterType.fromString(filter), defaultEscape, null);
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private HtmlUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}
