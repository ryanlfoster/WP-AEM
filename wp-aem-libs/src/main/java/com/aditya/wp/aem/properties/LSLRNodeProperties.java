/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.properties;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum LSLRNodeProperties implements Properties {
	TITLE("title"), //
	UNIT("unit"), //
	ICON_PATH("iconPath"), //
	REFERENCE_ATTRIBUTE_ID_SINGLE("referenceAttributeIdSingle"), //
	REFERENCE_ATTRIBUTE_ID_RANGE_1("referenceAttribute1"), //
    REFERENCE_ATTRIBUTE_ID_RANGE_2("referenceAttribute2"), //
    CUSTOM_ATTRIBUTE_ID("customAttributeId"), //
    TYPE("type"), //
    PANEL_SINGLE_VALUE("panelSingleValue"), //
    PANEL_RANGE_VALUE("panelRangeValue"), //
    PANEL_CUSTOM_ATTRIBUTE("panelCustomValue"), //
    SEPARATOR("separator"), //
    SORT_ORDER("sortOrder"), //
    ;

    private final String propertyName;

    /**
     * Constructor.
     * 
     * @param propertyName
     *            the propertyName
     */
    private LSLRNodeProperties(final String propertyName) {
        this.propertyName = propertyName;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.gmwp.aem.properties.Properties#getPropertyName()
     */
	@Override
    public String getPropertyName() {
	    return this.propertyName;
    }
}
