/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.exception.DBCException;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class DBC {

    // hide constructor since class contains only static methods
    private DBC() {
    }

    /**
     * Assert equals.
     * 
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     */
    public static void assertEquals(final String expected,
                                    final String actual) {
        assertEquals(expected, actual, "");
    }

    /**
     * Assert equals.
     * 
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     * @param message
     *            the message
     */
    public static void assertEquals(final String expected,
                                    final String actual,
                                    final String message) {

        if (!StringUtils.equals(expected, actual)) {
            throw new DBCException(message + " expected <" + expected + "> actual <" + actual + ">");

        }
    }

    /**
     * Assert equals.
     * 
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     * @param message
     *            the message
     */
    public static void assertEquals(final int expected,
                                    final int actual,
                                    final String message) {

        if (expected != actual) {
            throw new DBCException(message + " expected <" + expected + "> actual <" + actual + ">");

        }
    }

    /**
     * Assert equals.
     * 
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     */
    public static void assertEquals(final Object expected,
                                    final Object actual) {
        if (expected == null || actual == null) {
            throw new DBCException("Assertion failed, Objects not equal or null");
        }
        if (!expected.equals(actual)) {
            throw new DBCException("Assertion broken");
        }
    }

    /**
     * Assert not equal.
     * 
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     * @param msg
     *            the msg
     */
    public static void assertNotEqual(final Object expected,
                                      final Object actual,
                                      final String msg) {
        if (expected == null && actual != null) {
            return;
        }
        if (actual == null && expected != null) {
            return;
        }
        if (expected == actual) {
            throw new DBCException(msg);
        }
        if (expected.equals(actual)) {
            throw new DBCException(msg);
        }
    }

    /**
     * Assert true.
     * 
     * @param value
     *            the value
     */
    public static void assertTrue(final boolean value) {
        if (!value) {
            throw new DBCException("value is false instead of  true");
        }
    }

    /**
     * Assert false.
     * 
     * @param value
     *            the value
     */
    public static void assertFalse(final boolean value) {
        if (value) {
            throw new DBCException("value is true instead of  false");
        }
    }

    /**
     * Assert not null.
     * 
     * @param param
     *            the param
     * @param paramName
     *            the param name
     */
    public static void assertNotNull(final Object param,
                                     final String paramName) {
        if (param == null) {
            final String msg = "value of param '" + paramName + "' can not be null";
            throw new DBCException(msg);
        }
    }

    /**
     * Assert not null.
     * 
     * @param param
     *            the param
     * @param paramName
     *            the param name
     */
    public static void assertNotNull(final long param,
                                     final String paramName) {
        if (param == 0) {
            final String msg = "The value of the param'" + paramName + "' must not be null.";
            throw new DBCException(msg);
        }
    }

    /**
     * Assert not null.
     * 
     * @param param
     *            the param
     * @param paramName
     *            the param name
     */
    public static void assertNotNull(final int param,
                                     final String paramName) {
        if (param == 0) {
            final String msg = "The int value of the param'" + paramName + "' must not be null.";
            throw new DBCException(msg);
        }
    }

    /**
     * Assert not null.
     * 
     * @param param
     *            the param
     * @param paramName
     *            the param name
     */
    public static void assertNotNull(final byte param,
                                     final String paramName) {
        if (param == 0) {
            final String msg = "The byte value of the param'" + paramName + "' must not be null.";
            throw new DBCException(msg);
        }
    }

    /**
     * Assert not null or empty.
     * 
     * @param param
     *            the param
     * @param paramName
     *            the param name
     */
    public static void assertNotNullOrEmpty(final String param,
                                            final String paramName) {

        assertNotNull(param, paramName);

        if (StringUtils.isBlank(param)) {
            throw new DBCException("value is null or empty.");
        }
    }

    /**
     * Assert not blank.
     * 
     * @param value
     *            the value
     * @param paramName
     *            the param name
     */
    public static void assertNotBlank(final String value,
                                      final String paramName) {
        if (StringUtils.isBlank(value)) {
            throw new DBCException(paramName + " is blank!");
        }
    }

    /**
     * Assert not empty collection.
     * 
     * @param param
     *            the param
     * @param paramName
     *            the param name
     */
    public static void assertNotEmptyCollection(final Collection<?> param,
                                                final String paramName) {

        assertNotNull(param, paramName);

        if (param.isEmpty()) {
            throw new DBCException("Collection " + paramName + " is empty.");
        }
    }

    public static void assertNotEmptyCollection(final Collection<?> param,
                                                final String paramName,
                                                final String message) {

        assertNotNull(param, paramName);

        if (param.isEmpty()) {
            throw new DBCException("Collection empty.");
        }
    }

    /**
     * Precondition.
     * 
     * @param value
     *            the value
     */
    public static void precondition(final boolean value) {
        if (!value) {
            throw new DBCException("Precondition broken");
        }
    }

    /**
     * Precondition.
     * 
     * @param value
     *            the value
     * @param msg
     *            the msg
     */
    public static void precondition(final boolean value,
                                    final String msg) {
        if (!value) {
            throw new DBCException("Precondition broken: " + msg);
        }
    }

    /**
     * Postcondition.
     * 
     * @param value
     *            the value
     */
    public static void postcondition(final boolean value) {
        if (!value) {
            throw new DBCException("Postcondition broken");
        }
    }

    /**
     * Postcondition.
     * 
     * @param value
     *            the value
     * @param msg
     *            the msg
     */
    public static void postcondition(final boolean value,
                                     final String msg) {
        if (!value) {
            throw new DBCException("Postcondition broken: " + msg);
        }
    }

    /**
     * Invariant.
     * 
     * @param value
     *            the value
     */
    public static void invariant(final boolean value) {
        if (!value) {
            throw new DBCException("Invariant broken");
        }
    }

    /**
     * Invariant.
     * 
     * @param value
     *            the value
     * @param msg
     *            the msg
     */
    public static void invariant(final boolean value,
                                 final String msg) {
        if (!value) {
            throw new DBCException("Invariant broken: " + msg);
        }
    }

    /**
     * Assert type.
     * 
     * @param value
     *            the value
     * @param validParameterType
     *            the valid parameter type
     */
    public static void assertType(final Object value,
                                  final Class<?> validParameterType) {

        if (value == null) {
            return;
        }

        if (!validParameterType.isInstance(value)) {
            final String msg = "Value is not of type + " + validParameterType.getName() + " but of type: "
                    + value.getClass().getName();
            throw new DBCException(msg);
        }
    }

    /**
     * Map entry with key must not be blank.
     * 
     * @param key
     *            the key
     * @param attr
     *            the attr
     * @return the string
     */
    public static String mapEntryWithKeyMustNotBeBlank(final String key,
                                                       final Map<String, String> attr) {
        final String str = attr.get(key);
        if (StringUtils.isBlank(str)) {
            throw new DBCException("Map entry with key " + key + " is blank!");
        }
        return str;
    }

    /**
     * Map entry with key must point to existing file.
     * 
     * @param key
     *            the key
     * @param attr
     *            the attr
     * @return the file
     */
    public static File mapEntryWithKeyMustPointToExistingFile(final String key,
                                                              final Map<String, String> attr) {
        final String filePath = attr.get(key);
        if (StringUtils.isBlank(filePath)) {
            throw new DBCException("Map entry with key " + key + " not found!");
        }
        final File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("The file " + filePath + " does not exist!");
        }

        return file;
    }

    /**
     * Check for null and append to error message.
     * 
     * @param attribute
     *            the attribute
     * @param errorMessage
     *            the error message
     * @param params
     *            the params
     */
    public static void checkForNullAndAppendToErrorMessage(final String attribute,
                                                           final StringBuilder errorMessage,
                                                           final Map<?, ?> params) {
        checkForNullAndAppendToErrorMessage(attribute, errorMessage, params.get(attribute));
    }

    /**
     * Check for null and append to error message.
     * 
     * @param attribute
     *            the attribute
     * @param errorMessage
     *            the error message
     * @param value
     *            the value
     */
    public static void checkForNullAndAppendToErrorMessage(final String attribute,
                                                           final StringBuilder errorMessage,
                                                           final Object value) {
        if (value == null) {
            // TODO localize me
            errorMessage.append(attribute + " is required\n");
        }
    }

}