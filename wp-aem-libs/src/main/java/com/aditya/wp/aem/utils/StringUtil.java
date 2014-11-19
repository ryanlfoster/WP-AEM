/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils;

import java.util.Locale;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class StringUtil {

    /**
     * Check if a String starts with a specified prefix character. <code>nulls</code> are handled
     * without exceptions. The comparison is case sensitive.
     * 
     * <pre>
     * StringUtil.startsWith(null, 'a')     = false
     * StringUtil.startsWith("", 0)         = false
     * StringUtil.startsWith("", 'a')       = false
     * StringUtil.startsWith("abcdef", 'a') = true
     * StringUtil.startsWith("ABCDEF", 'a') = false
     * StringUtil.startsWith("ABCDEF", 'B') = false
     * </pre>
     * 
     * @param str
     *            the String to check, may be null
     * @param prefix
     *            the first char to find
     * @return true, if the String starts with the prefix
     */
    public static boolean startsWith(final String str,
                                     final char prefix) {
        return str != null && str.length() > 0 && str.charAt(0) == prefix;
    }

    /**
     * Check if a String ends with a specified suffix character. <code>nulls</code> are handled
     * without exceptions. The comparison is case sensitive.
     * 
     * <pre>
     * StringUtil.endsWith(null, 'a')     = false
     * StringUtil.endsWith("", 0)         = false
     * StringUtil.endsWith("", 'a')       = false
     * StringUtil.endsWith("abcdef", 'f') = true
     * StringUtil.endsWith("ABCDEF", 'f') = false
     * StringUtil.endsWith("ABCDEF", 'G') = false
     * </pre>
     * 
     * @param str
     *            the String to check, may be null
     * @param suffix
     *            the last char to find
     * @return true, if the String ends with the suffix
     */
    public static boolean endsWith(final String str,
                                   final char suffix) {
        return str != null && str.length() > 0 && str.charAt(str.length() - 1) == suffix;
    }

    /**
     * Check if string has any special character and remove them.
     * 
     * @param str
     * @return
     */
    public static String removeSpecialChars(final String str) {
        if (str != null) {
            return str.replaceAll("[^a-zA-Z0-9\\s\\_\\-]", "");
        }
        return str;
    }

    /**
     * Check if string has any special character and removes them and convert the input string to
     * lower case.
     * 
     * @param str
     * @return
     */
    public static String getCleanStringToLowerCase(final String input) {
        return input.toLowerCase(Locale.ENGLISH).replaceAll("[^a-zA-Z0-9_\\-]+", "_");
    }

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private StringUtil() {
        throw new AssertionError("This utilitiy class is not ment to be instantiated.");
    }

    /**
     * Removes the carriage returns.
     * 
     * @param string
     *            the string
     * @return the string
     */
    public static String removeCarriageReturns(final String string) {
        return ((string.replace("\n", "")).replace("<br>", "")).replace("\r", "");
    }

    /**
     * Removes the rich text.
     * 
     * @param string
     *            the string
     * @return the string
     */
    public static String removeRichText(final String string) {
        return ((string.replace("\n", "")).replace("<br>", "")).replace("\r", "").replace("<p>", "")
                .replace("</p>", "");
    }
}
