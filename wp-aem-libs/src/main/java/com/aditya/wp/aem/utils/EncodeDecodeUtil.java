/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import com.aditya.wp.aem.exception.EncodeDecodeException;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class EncodeDecodeUtil {

    /**
     * Decodes the text which was encoded with extendedBase64Encode().
     * 
     * @param s
     *            the s
     * @return the string
     */
    public static String extendedBase64Decode(final String s) {
        final Charset charset = Charset.forName("UTF8");
        // GMDSST-3682: due to the fact that the apache webserver seems to mess around with the slashes
        // created by the base64 encoding those needed to be en- and decoded by ourselves (see also
        // RndTxtTag).
        String slashDecodedText = "";
        if (s != null) {
            slashDecodedText = s.replace("_slash_", "/");
        }

        // base64 decoding
        final byte[] baseDecodedText = Base64.decodeBase64(slashDecodedText.getBytes());
        return new String(baseDecodedText, charset);
    }

    /**
     * Encodes the text with Base 64 and additionally encodes slashes.
     * 
     * @param s
     *            the s
     * @return the string
     */
    public static String extendedBase64Encode(final String s) {
        final Charset charset = Charset.forName("UTF8");
        final byte[] baseEncoded = Base64.encodeBase64(s.getBytes(charset));
        final String baseEncodedText = new String(baseEncoded);

        // GMDSST-3682: due to the fact that the apache webserver seems to mess around with the slashes
        // created by the base64 encoding those needed to be en- and decoded by ourselves (see also
        // TextRenderService).
        return baseEncodedText.replace("/", "_slash_");

    }

    /**
     * URL-decodes the string with UTF-8 charset.
     * 
     * @param s
     *            the string to be decoded.
     * @return URL decoded string.
     */
    public static String urlDecode(final String s) {
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new EncodeDecodeException("Charset utf-8 unsupported, JVM either unsupported or broken!", e);
        }
    }

    /**
     * URL-encodes the string with UTF-8 charset.
     * 
     * @param s
     *            the string to be encoded.
     * @return URL encoded string.
     */
    public static String urlEncode(final String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new EncodeDecodeException("Charset utf-8 unsupported, JVM either unsupported or broken!", e);
        }
    }

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private EncodeDecodeUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}
