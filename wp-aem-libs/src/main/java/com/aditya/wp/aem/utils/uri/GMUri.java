/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.uri;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class GMUri {

    private String scheme;
    private String host;
    private String extension;
    private String suffix;
    private String contentPath;
    private String anchor;
    private int port;
    private List<String> selectors;
    private Map<String, Set<String>> parameters;

    /**
     * @return the anchor
     */
    public final String getAnchor() {
        return this.anchor;
    }

    /**
     * @return the contentPath
     */
    public final String getContentPath() {
        return this.contentPath;
    }

    public String getExtension() {
        return this.extension;
    }

    /**
     * @return the host
     */
    public final String getHost() {
        return this.host;
    }

    /**
     * @return the parameters
     */
    public final Map<String, Set<String>> getParameters() {
        return this.parameters;
    }

    /**
     * @return the port
     */
    public final int getPort() {
        return this.port;
    }

    /**
     * @return the scheme
     */
    public final String getScheme() {
        return this.scheme;

    }

    public List<String> getSelectors() {
        return this.selectors;
    }

    /**
     * @return the suffix
     */
    public final String getSuffix() {
        return this.suffix;
    }

    /**
     * @param anchor
     *            the anchor to set
     */
    public final void setAnchor(final String anchor) {
        this.anchor = anchor;
    }

    /**
     * @param contentPath
     *            the contentPath to set
     */
    public final void setContentPath(final String contentPath) {
        this.contentPath = contentPath;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    /**
     * @param host
     *            the host to set
     */
    public final void setHost(final String host) {
        this.host = host;
    }

    /**
     * @param parameters
     *            the parameters to set
     */
    public final void setParameters(final Map<String, Set<String>> parameters) {
        this.parameters = parameters;
    }

    /**
     * @param port
     *            the port to set
     */
    public final void setPort(final int port) {
        this.port = port;
    }

    /**
     * @param scheme
     *            the scheme to set
     */
    public final void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    /**
     * @param selectors
     *            the selectors to set
     */
    public final void setSelectors(final List<String> selectors) {
        this.selectors = selectors;
    }

    /**
     * @param suffix
     *            the suffix to set
     */
    public final void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

}
