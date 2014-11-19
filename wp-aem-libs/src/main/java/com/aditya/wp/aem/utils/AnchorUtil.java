/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.exception.InvalidIdException;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class AnchorUtil {

    /** the valid chars according to w3c, a html id attribute can have. */
    public static final String VALID_ID_REGEX = "^[A-Za-z][A-Za-z0-9-_]*$";
    /** matches all invalid characters, a html id attribute can't have. */
    public static final String INVALID_CHARACTERS_ID_REGEX = "[^\\w-]";
    /** matches common invalid id characters. */
    private static final String COMMON_INVALID_CHARACTERS_ID_REGEX = "[\\s:.]";
    /** the compiled VALID_ID_PATTERN. */
    private static final Pattern VALID_ID_PATTERN = Pattern.compile(VALID_ID_REGEX);
    /** the anchor label property. */
    public static final String ANCHOR_LABEL_PROPERTY = "anchor";
    /** the anchor id property. */
    public static final String ANCHOR_ID_PROPERTY = "anchorId";

    private static final byte END_OF_RANGE = 127;

    private static final byte INCREMENT = 26;

    private static final String TEXT = "text";

    private static final String VALUE = "value";

    private static final String ANCHOR_PREFIX = "Anchor";

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private AnchorUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }

    /**
     * Helps creating an anchor id. The id is build by first looking at the 'anchorId' property. If it's not maintained
     * 'anchor' property is considered which is actually the label. Invalid characters are stripped out of the label to
     * conform to w3c standard, thus it can happen that anchor id is just an empty string.
     * 
     * @param resource
     *            the current resource to read anchor label / id property from
     * @return anchor id or <code>null</code> if no anchor maintained
     * @throws InvalidIdException
     *             thrown if 'anchorId' doesn't conform to w3c standard (only iso-8859-1 chars)
     */
    public static String getAnchorId(final Resource resource) throws InvalidIdException {
        return getAnchorId(ResourceUtil.getValueMap(resource));
    }

    /**
     * Helps creating an anchor id. The id is build by first looking at the 'anchorId' property. If it's not maintained
     * 'anchor' property is considered which is actually the label. Invalid characters are stripped out of the label to
     * conform to w3c standard, , thus it can happen that anchor id is just an empty string.
     * 
     * @param valueMap
     *            the properties of a resource
     * @return anchor id or <code>null</code> if no anchor maintained
     * @throws InvalidIdException
     *             thrown if 'anchorId' doesn't conform to w3c standard (only iso-8859-1 chars)
     */
    public static String getAnchorId(final ValueMap valueMap) throws InvalidIdException {
        return getAnchorId(valueMap.get(ANCHOR_LABEL_PROPERTY, String.class),
                valueMap.get(ANCHOR_ID_PROPERTY, String.class));
    }

    /**
     * Helps creating an anchor id. The id is build by first looking at the 'anchorId' property. If it's not maintained
     * 'anchor' property is considered which is actually the label. Invalid characters are stripped out of the label to
     * conform to w3c standard, thus it can happen that anchor id is just an empty string.
     * 
     * @param anchorLabel
     *            the anchor label
     * @param anchorId
     *            the anchor id
     * @return anchor id or <code>null</code> if no anchor maintained
     * @throws InvalidIdException
     *             thrown if 'anchorId' doesn't conform to w3c standard (only iso-8859-1 chars)
     */
    public static String getAnchorId(final String anchorLabel,
                                     final String anchorId) throws InvalidIdException {

        String id = getAnchorId(anchorId);
        if (null == id && null != anchorLabel) {
            // keep live behaviour, where former 'anchor' now 'anchor label' is used as both label and technical id
            final CharsetEncoder e = Charset.forName("ISO-8859-1").newEncoder();
            if (e.canEncode(anchorLabel)) {
                id = anchorLabel.replaceAll(INVALID_CHARACTERS_ID_REGEX, "").trim();
            } else {
                throw new InvalidIdException("The anchor label '" + anchorLabel
                        + "' used as fallback id is not valid. Only ISO-8859-1 characters allowed. "
                        + "Maintain anchor id additionally.");
            }
        }

        return id;
    }

    /**
     * Retrieves an anchor id from a property string value by also checking (if not <code>null</code>) if it's conform
     * to the w3c standard and eventually returns it or throws an exception.
     * 
     * @param anchorId
     *            the anchor id property value
     * @return anchor id or <code>null</code> if no anchor id passed
     * @throws InvalidIdException
     *             thrown if anchor id doesn't conform to w3c standard (only iso-8859-1 chars)
     */
    public static String getAnchorId(final String anchorId) throws InvalidIdException {
        if (anchorId != null && !isValidId(anchorId)) {
            throw new InvalidIdException("The id '" + anchorId
                    + "' is not valid. Only ISO-8859-1 characters are allowed. "
                    + "The id must begin with a letter ([A-Za-z]) and may be followed by any number of letters, "
                    + "digits ([0-9]), hyphens (-) and underscores (_)");
        }
        return anchorId;
    }

    /**
     * Returns whether an id is valid.
     * 
     * @param id
     *            the id to check
     * @return is valid
     */
    public static boolean isValidId(final String id) {
        return id != null && VALID_ID_PATTERN.matcher(id).matches();
    }

    /**
     * Creates a valid id by also checking if passed string is conform to w3c standard if not, all illegal chars are
     * stripped.
     * 
     * @param id
     *            the id to check
     * @return passed id if it's valid otherwise passed id with all illegal characters stripped
     */
    public static String createValidId(final String id) {
        if (null == id) {
            throw new IllegalArgumentException("Parameter 'id' can't be null.");
        }
        return isValidId(id) ? id : id.replaceAll(INVALID_CHARACTERS_ID_REGEX, "").trim();
    }

    /**
     * The method converts an invalid HTML ID to a valid HTML ID.<br/>
     * <br/>
     * This was necessary because in some cases the title is used as part of the ID but the title can contain invalid
     * characters.
     * 
     * @param invalidId
     *            the invalid ID
     * @return the valid ID
     */
    public static String convertToValidId(final String invalidId) {
        if (StringUtils.isBlank(invalidId)) {
            throw new IllegalArgumentException("The parameter 'invalidId' can be neither null nor empty.");
        }

        String validId = invalidId;
        if (!isValidId(validId)) {
            validId = trySimpleIDConversion(invalidId);
            if (!isValidId(validId)) {
                validId = tryComplexIDConvertion(invalidId);
                if (!isValidId(validId)) {
                    validId = stripOutAllInvalidCharacters(invalidId);
                }
            }
        }

        return validId;
    }

    /**
     * The method tries a simple conversion of an invalid ID.<br/>
     * <br/>
     * The method replaces all spaces (" "), colons (:) and the periods (.) by hyphens (-).<br/>
     * <br/>
     * The method doesn't check whether the converted ID is valid or not.
     * 
     * @param invalidId
     *            the invalid ID
     * @return the converted ID.
     */
    private static String trySimpleIDConversion(final String invalidId) {
        return invalidId.replaceAll(COMMON_INVALID_CHARACTERS_ID_REGEX, "-");
    }

    /**
     * The method tries a complex conversion of an invalid ID<br/>
     * <br/>
     * The method replaces invalid characters according to a schema by one or more valid characters. This leads to a
     * cryptic ID.<br/>
     * <br/>
     * The method doesn't check whether the converted ID is valid or not.
     * 
     * @param invalidId
     *            the invalid id
     * @return the converted ID
     */
    private static String tryComplexIDConvertion(final String invalidId) {
        String validId;
        try {
            final byte[] bytesOfId = invalidId.getBytes("UTF-8");
            final StringBuilder newID = new StringBuilder();

            for (int i = 0; i < bytesOfId.length; i++) {

                byte nextByte = bytesOfId[i];
                if (nextByte < 0) {
                    nextByte *= -1;
                }

                if (i == 0) {
                    newID.append(getValidFirstCharacter((char) nextByte));
                } else {
                    newID.append(getValidCharacter((char) nextByte));
                }
            }
            validId = newID.toString();

        } catch (UnsupportedEncodingException e) {
            validId = "";
        }

        return validId;
    }

    /**
     * The method strips out all invalid characters of an invalid ID.
     * 
     * @param invalidId
     *            the invalid ID
     * @return a cleaned ID
     */
    private static String stripOutAllInvalidCharacters(final String invalidId) {
        return invalidId.replaceAll(INVALID_CHARACTERS_ID_REGEX, "");
    }

    /**
     * The method returns a valid first character of an id.
     * 
     * @param character
     *            the character
     * @return the valid character
     */
    private static char getValidFirstCharacter(final char character) {
        char outputCharacter = character;
        while (!isValidLetter(outputCharacter)) {
            if (outputCharacter >= END_OF_RANGE) {
                outputCharacter = (char) (outputCharacter - END_OF_RANGE + 1);
            }
            outputCharacter += INCREMENT;
        }
        return outputCharacter;
    }

    /**
     * The method returns a valid ID character.
     * 
     * @param character
     *            the character
     * @return a valid HTML ID character
     */
    private static char getValidCharacter(final char character) {
        char outputCharacter = character;
        while (!isValidLetter(outputCharacter) && !isValidNumeral(outputCharacter)
                && !isValidCharacterOtherThanLetterAndNumeral(outputCharacter)) {
            if (outputCharacter >= END_OF_RANGE) {
                outputCharacter = (char) (outputCharacter - END_OF_RANGE + 1);
            }
            outputCharacter += INCREMENT;
        }
        return outputCharacter;
    }

    /**
     * The method checks whether a character is a valid letter (A-Z, a-z) or not.
     * 
     * @param character
     *            the character
     * @return true if the character is a valid letter (A-Z, a-z), false otherwise
     */
    private static boolean isValidLetter(final char character) {
        return (character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z');
    }

    /**
     * The method checks whether a character is a valid numeral (0-9) or not.
     * 
     * @param character
     *            the character
     * @return true if the character is a valid numeral (0-9), false otherwise
     */
    private static boolean isValidNumeral(final char character) {
        return character >= '0' && character <= '9';
    }

    /**
     * The method checks whether a character is a valid character other then a letter or a numeral or not.<br/>
     * <br/>
     * According to the specification the colon (:) and the period (.) are also valid characters for an ID but jQuery
     * seems to have some problems with this characters so they weren't considered.
     * 
     * @param character
     *            the character
     * @return true if the character is a hyphen (-) or an underscore (_), false otherwise
     */
    private static boolean isValidCharacterOtherThanLetterAndNumeral(final char character) {
        return character == '-' || character == '_';
    }

    /**
     * The method returns the deeplink entry for a given resource.
     * <p>
     * The method searches at first for the anchor (property "<b>anchorId</b>") and after that for the anchor label
     * (property "<b>anchor</b>").
     * </p>
     * <b>Importent Hint</b>
     * <p>
     * The method uses {@link #convertToValidId(String)} so the component should use the same method to check
     * respectively to convert the anchor otherwise it could be that the deeplink doesn't work.
     * </p>
     * 
     * @param resource
     *            the resource
     * @return the deeplink entry
     */
    public static Map<String, String> getDeeplinkEntry(final Resource resource) {

        final Map<String, String> map;
        final ValueMap properties = ResourceUtil.getValueMap(resource);
        final String anchor = getAnchor(properties);
        final String anchorLabel = getAnchorLabel(properties);
        if (StringUtils.isNotBlank(anchor)) {
            map = buildEntyMap(anchor, getHref(anchor));

        } else if (StringUtils.isNotBlank(anchorLabel)) {
            map = buildEntyMap(anchorLabel, getHref(anchorLabel));

        } else {
            map = null;
        }
        return map;
    }

    /**
     * The method builds a new deeplink entry.
     * 
     * @param label
     *            the label
     * @param href
     *            the href
     * @return the map
     */
    private static Map<String, String> buildEntyMap(final String label,
                                                    final String href) {

        final Map<String, String> map = new HashMap<String, String>();
        map.put(VALUE, href);
        map.put(TEXT, ANCHOR_PREFIX + ": " + label);
        return map;
    }

    /**
     * The method return the href.
     * 
     * @param anchor
     *            the anchor
     * @return the href
     */
    private static String getHref(final String anchor) {
        return "#".concat(convertToValidId(anchor));
    }

    /**
     * The method return the anchor.
     * <p>
     * The method searches for the property "anchorId".
     * </p>
     * 
     * @param map
     *            the map
     * @return the anchor
     */
    private static String getAnchor(final ValueMap map) {
        return map.get(ANCHOR_ID_PROPERTY, String.class);
    }

    /**
     * The method return the anchor label.
     * <p>
     * The method searches for the property "anchor".
     * </p>
     * 
     * @param map
     *            the map
     * @return the anchor label
     */
    private static String getAnchorLabel(final ValueMap map) {
        return map.get(ANCHOR_LABEL_PROPERTY, String.class);
    }
}
