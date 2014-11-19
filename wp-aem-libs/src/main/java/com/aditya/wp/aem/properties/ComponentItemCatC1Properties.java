/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.properties;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum ComponentItemCatC1Properties implements Properties {
	ICON_PATH("iconPath"), //
	CATEGORY_LABEL("categoryLabel"), //
	;

    private final String propertyName;

    /**
     * Constructor.
     * 
     * @param propertyName
     *            the propertyName
     */
    private ComponentItemCatC1Properties(final String propertyName) {
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
