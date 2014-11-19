/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.utils.uri.UriBuilder;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class SelectorUtil {

    /** This symbol separates the key from the value within a selector. */
    public static final String ASSIGNMENT_SYMBOL = "=";

    /**
     * Translate all selectors to an map.
     * 
     * @param request
     *            current request object
     * @return a map of all selector parameter
     * @see SelectorUtil
     * @deprecated please use {@link #getSelectors(SlingHttpServletRequest)}
     */
    @Deprecated
    public static Map<String, String> getSelectorParameter(final SlingHttpServletRequest request) {
        final Map<String, String> params = new HashMap<String, String>();
        final String[] selectors = request.getRequestPathInfo().getSelectors();
        for (String selector : selectors) {
            params.putAll(getSelectorParameter(selector));
        }
        return params;
    }

    /**
     * Translate one selector to an map.
     * 
     * @param selector
     *            one selector string
     * @return a map of selector parameter
     * @see SelectorUtil
     * @deprecated please use getSelectors(SlingHttpServletRequest|String|List<String>).get(String)
     */
    @Deprecated
    public static Map<String, String> getSelectorParameter(final String selector) {
        final Map<String, String> params = new HashMap<String, String>();
        final String[] arr = selector.split("&");
        for (final String s : arr) {
            final String[] z = s.split("=");
            final String key = z[0];
            String value = null;
            if (z.length > 1) {
                value = z[1];
            }
            params.put(key, value);
        }
        return params;
    }

    /**
     * Gets the selectors from the request and translates them to a key-value-map. The key is the part of the selector
     * before the ASSIGNMENT_SYMBOL and the value is the part after the symbol.
     * 
     * @param request
     *            the request
     * @return the selectors as key-value-map
     */
    public static Map<String, String> getSelectors(final SlingHttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();
        if (request != null) {
            final String[] s = request.getRequestPathInfo().getSelectors();
            if (s != null) {
                final List<String> selectors = Arrays.asList(s);
                result = getSelectors(selectors);
            }
        }
        return result;
    }

    /**
     * The method returns the value for the given key from a selector.<br/>
     * <br/>
     * <p>
     * The key is the part of the selector before the {@link #ASSIGNMENT_SYMBOL} and the value is the part after the
     * symbol.
     * </p>
     * <p style="color:red">
     * Don't use this method if you need more than one value. It would be inefficient.
     * </p>
     * 
     * @param request
     *            the {@link SlingHttpServletRequest}
     * @param key
     *            the key
     * @return the value or <code>null</code>
     */
    public static String getSelectorValue(final SlingHttpServletRequest request,
                                          final String key) {

        final Map<String, String> map = getSelectors(request);
        return map.get(key);
    }

    /**
     * The method checks whether a key is present or not. The key <b>can be a complete selector</b>.
     * <p>
     * The key is the part of the selector before the {@link #ASSIGNMENT_SYMBOL} and the value is the part after the
     * symbol.
     * <p>
     * <p style="color:red">
     * Don't use this method if you want to check more than one key. It would be inefficient.
     * <p>
     * 
     * @param request
     *            the {@link SlingHttpServletRequest}
     * @param key
     *            the key
     * @return true if the key is present, false otherwise
     */
    public static boolean hasSelectorKey(final SlingHttpServletRequest request,
                                         final String key) {
        final Map<String, String> map = getSelectors(request);
        return map.containsKey(key);
    }

    /**
     * Gets the selectors from the url and translates them to a key-value-map. The key is the part of the selector
     * before the ASSIGNMENT_SYMBOL and the value is the part after the symbol.
     * 
     * @param url
     *            the url
     * @return the selectors as key-value-map
     */
    public static Map<String, String> getSelectors(final String url) {
        Map<String, String> result = new HashMap<String, String>();
        if (url != null) {
            final UriBuilder ub = new UriBuilder(url);
            result = getSelectors(ub.getSelectors());
        }
        return result;
    }

    /**
     * Translate the selectors to a key-value-map. The key is the part of the selector before the ASSIGNMENT_SYMBOL and
     * the value is the part after the symbol.
     * 
     * @param selectors
     *            the selectors
     * @return the selectors as key-value-map
     */
    public static Map<String, String> getSelectors(final List<String> selectors) {
        final Map<String, String> result = new HashMap<String, String>();
        if (selectors != null) {
            for (String selector : selectors) {
                final String[] keyValueArray = selector.split(ASSIGNMENT_SYMBOL, 2);
                final String selectorKey = keyValueArray[0];
                String selectorValue = "";
                if (keyValueArray.length > 1) {
                    selectorValue = keyValueArray[1];
                }
                result.put(selectorKey, selectorValue);
            }
        }
        return result;
    }

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private SelectorUtil() {
        throw new AssertionError("This utilitiy class is not ment to be instantiated.");
    }
}
