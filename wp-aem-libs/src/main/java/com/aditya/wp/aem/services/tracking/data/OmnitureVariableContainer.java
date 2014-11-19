/*
 * (c) 2010 General Motors Corp. All rights reserved. This material is solely and exclusively owned by General Motors
 * and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.data;

/**
 * Omniture variable wrapper.
 * 
 * @author Johannes Seitz, Namics (Deutschland) GmbH
 * @since GMDS Release 1.2
 */
public final class OmnitureVariableContainer {

    private final String description;

    private final String javaScriptVariableName;

    private String value;

    /**
     * Constructs a new omniture variable container.
     * 
     * @param key
     *            the key for a variable
     * @param description
     *            the descriptioon of the variable.
     */
    public OmnitureVariableContainer(final String key, final String description) {
        this.description = description;
        this.javaScriptVariableName = key;
    }

    /**
     * Returns the javascript variable name.
     * 
     * @return the key
     */
    public String getJavaScriptVariableName() {
        return this.javaScriptVariableName;
    }

    /**
     * Returns the value of the javascript variable name.
     * 
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the value for the javascript variable name.
     * 
     * @param value
     *            the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * String representation of the variable including description, key and value.
     * 
     * @return String representation.
     */
    @Override
    public String toString() {
        return this.description + ": " + this.value;
    }

}
