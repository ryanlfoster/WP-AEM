/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.properties;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public enum BodystyleAttributeProperties implements Properties {
	DISPLAY_TYPE("hideAttr"), //
	OVERRIDE_BBC("bbcCarlineOverride"), //
	OVERRIDE_HMC("helpmeCarlineOverride"), //
	OVERRIDE_FAMILY("familyCarlineOverride"), //
	INHERIT("inherit"), //
	CUSTOM("custom"), //
	SUPPRESS("true"), //
	;

	private String propertyName;

	/**
	 * Private constructor.
	 * 
	 * @param propertyName
	 *            property name
	 */
	private BodystyleAttributeProperties(final String propertyName) {
		this.propertyName = propertyName;
	}
	
	@Override
	public String getPropertyName() {
	    return this.propertyName;
	}
}
