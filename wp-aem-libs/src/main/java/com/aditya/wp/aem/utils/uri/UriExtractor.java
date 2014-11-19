/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.uri;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.utils.EncodeDecodeUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class UriExtractor {

    private static final char ANCHOR = '#';

    private final GMUri gmUri;

    /**
     * Constructor.
     * 
     * @param baseUri
     *            uri
     */
    public UriExtractor(final String baseUri) {
        final String workingCopy = StringUtils.stripToEmpty(baseUri);
        String query = StringUtils.EMPTY;
        String pathSuffix = StringUtils.EMPTY;

        URI uri;

        try {
            uri = URI.create(workingCopy);
        } catch (IllegalArgumentException e) {
            // URI contains inner spaces. if spaces only occur in parameters, skip parameter string
            // for now
            final int queryStart = workingCopy.indexOf('?');
            if (queryStart >= 0) {
                uri = URI.create(workingCopy.substring(0, queryStart));
                query = workingCopy.substring(queryStart);
            } else if (hasNoSpacesInPathToResource(workingCopy)) {
                uri = URI.create(workingCopy.substring(0, workingCopy.lastIndexOf('/')));
                pathSuffix = workingCopy.substring(workingCopy.lastIndexOf('/'));
            } else {
                throw new IllegalArgumentException("URI extraction failed for invalid URI " + baseUri, e);
            }
        }

        this.gmUri = new GMUri();

        if (uri != null) {
            final StringBuffer path = new StringBuffer(uri.getPath());

            this.gmUri.setScheme(uri.getScheme());
            this.gmUri.setHost(uri.getHost());
            this.gmUri.setPort(uri.getPort());
            this.gmUri.setContentPath(extractContentPath(path) + pathSuffix);
            this.gmUri.setSuffix(extractSuffix(path));
            this.gmUri.setSelectors(extractSelectors(path));
            this.gmUri.setExtension(extractExtension(path));

            this.gmUri.setParameters(extractParametersFromQuery(StringUtils.isNotEmpty(uri.getQuery()) ? uri.getQuery() : query));

            final int anchorIndex = workingCopy.indexOf(ANCHOR);
            final String anchor = anchorIndex >= 0 ? workingCopy.substring(anchorIndex + 1, workingCopy.length()) : null;
            this.gmUri.setAnchor(anchor);
        }
    }

    private boolean hasNoSpacesInPathToResource(final String workingCopy) {
        return workingCopy.lastIndexOf('/') >= 0 && !workingCopy.substring(0, workingCopy.lastIndexOf('/')).contains(" ");
    }

    /**
     * Gets the anchor from the uri.
     * 
     * @return anchor
     */
    public String getAnchor() {
        return this.gmUri.getAnchor();
    }

    /**
     * Gets the content path from the URI.
     * 
     * @return content path
     */
    public String getContentPath() {
        return this.gmUri.getContentPath();
    }

    /**
     * Gets the extension from the URI.
     * 
     * @return extension
     */
    public String getExtension() {
        return this.gmUri.getExtension();
    }

    /**
     * Gets the GMUri object.
     * 
     * @return GMUri
     */
    public GMUri getGMUri() {
        return this.gmUri;
    }

    /**
     * Gets the host from the URI.
     * 
     * @return host
     */
    public String getHost() {
        return this.gmUri.getHost();
    }

    /**
     * Gets the parameters from the URI and returns it in a map object.
     * 
     * @return parameters
     */
    public Map<String, Set<String>> getParameters() {
        return this.gmUri.getParameters();
    }

    /**
     * Gets the port from the URI.
     * 
     * @return port
     */
    public int getPort() {
        return this.gmUri.getPort();
    }

    /**
     * Gets the scheme from the URI.
     * 
     * @return scheme.
     */
    public String getScheme() {
        return this.gmUri.getScheme();
    }

    /**
     * Gets the selectors from the URI.
     * 
     * @return selector
     */
    public List<String> getSelectors() {
        return this.gmUri.getSelectors();
    }

    /**
     * Gets the suffix part of the URI.
     * 
     * @return suffix
     */
    public String getSuffix() {
        return this.gmUri.getSuffix();
    }

    /**
     * Adds the parameter. A key can have multible values.
     * 
     * @param parameters
     * @param key
     *            the key
     * @param value
     *            the value
     */
    private static void addParameter(final Map<String, Set<String>> parameters,
                                     final String key,
                                     final String value) {
        if (StringUtils.isEmpty(key)) {
            return;
        }

        Set<String> values = parameters.get(key);

        if (values == null) {
            values = new HashSet<String>();
        }

        values.add(value);
        parameters.put(key, values);
    }

    /**
     * Finds and removes the content path part of the URI path.
     * 
     * @param path
     *            the path as a {@link StringBuffer}
     */
    private static String extractContentPath(final StringBuffer path) {
        final int firstPoint = path.indexOf(".");
        final String contentPath = firstPoint >= 0 ? path.substring(0, firstPoint) : path.toString();
        path.delete(0, contentPath.length() + 1);

        return contentPath;
    }

    /**
     * Extracts extension from the URI path, after content part and suffix have been extracted.
     * 
     * @param path
     *            the path as a {@link StringBuffer}
     * @return
     */
    private static String extractExtension(final StringBuffer path) {
        final String[] selectors = path.toString().split("\\.");

        String returnValue = null;
        if (selectors.length > 0 && StringUtils.isNotEmpty(selectors[selectors.length - 1])) {
            returnValue = selectors[selectors.length - 1];
        }
        return returnValue;
    }

    /**
     * Extract the parameter from the query and return it as a map object.
     * 
     * @return parameters
     */
    private static Map<String, Set<String>> extractParametersFromQuery(final String query) {
        final Map<String, Set<String>> parameters = new HashMap<String, Set<String>>();

        if (StringUtils.isNotEmpty(query)) {
            String decodedQuery = query;
            try {
                decodedQuery = URLDecoder.decode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                decodedQuery = query;
            }
            final String[] keyValuePairs = decodedQuery.split("&");
            for (String keyValuePair : keyValuePairs) {
                final String[] pair = keyValuePair.split("=");
                if (pair.length == 1) {
                    final String paramName = EncodeDecodeUtil.urlDecode(pair[0]);
                    parameters.put(paramName, null);
                } else if (pair.length == 2) {
                    final String paramName = EncodeDecodeUtil.urlDecode(pair[0]);
                    final String paramValue = EncodeDecodeUtil.urlDecode(pair[1]);
                    addParameter(parameters, paramName, paramValue);
                }
            }
        }
        return parameters;
    }

    /**
     * Extracts selectors from the URI path, after content part and suffix have been extracted.
     * 
     * @param path
     *            the path as a {@link StringBuffer}
     * @return selector
     */
    private static List<String> extractSelectors(final StringBuffer path) {
        final List<String> selectors = new ArrayList<String>();

        final String[] possibleSelectors = path.toString().split("\\.");
        for (int i = 0; i < possibleSelectors.length - 1; i++) {
            if (StringUtils.isNotEmpty(possibleSelectors[i])) {
                selectors.add(possibleSelectors[i]);
            }
        }

        return selectors;
    }

    /**
     * Finds and removes the suffix of the URI path, after the content path has been extracted.
     * 
     * @param path
     *            the path as a {@link StringBuffer}
     * @return suffix
     */
    private static String extractSuffix(final StringBuffer path) {
        String suffix = null;

        final int suffixStart = path.indexOf("/");
        if (suffixStart >= 0) {
            suffix = path.substring(suffixStart, path.length());
            path.delete(suffixStart, path.length());
        }

        return suffix;
    }
}
