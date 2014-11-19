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
public enum BodystyleNodeProperties implements Properties {
	MANUAL_ATTRIBUTE_VALUE_1("manualAttributeValue1"), //
	MANUAL_ATTRIBUTE_VALUE_2("manualAttributeValue2"), //
	DISCLAIMER_INDICATION("disclaimerIndication"), //
	MANUAL_UNIT("manualUnit"), //
	;

    private final String propertyName;

    /**
     * Constructor.
     * 
     * @param propertyName
     *            the propertyName
     */
    private BodystyleNodeProperties(final String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
	public String getPropertyName() {
	    return this.propertyName;
	}
}
