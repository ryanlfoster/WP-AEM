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
public enum BaseballcardConfigurationProperties implements Properties {

    /** the configuration property for the code. */
    CONFIGURATION_CODE("bbc_configuration"),
    /** The overwritten price on the configuration bbc. */
    DDP_PRICE_OVERWRITE("ddp_price_overwrite"),
    /** This property is to determine if the image is selectable on the nav_bodystyle_selection component. */
    SELECTABLE_NAV_BODYSTYLE_SELECTION("selectableNavBodystyleSelection"),
    /** the small dam thumbnail. */
    THUMBNAIL_DAM_SMALL("imageReferenceSmall"),
    /** the small thumbnail. */
    THUMBNAIL_SMALL("bbc_configuration_thumbnail_small"),
    /** Option for selecting no bodystyle. */
    NO_SELECTION("no_selection");

    /** the property name. */
    private String property;

    /**
     * Instantiates a new component property.
     * 
     * @param property
     *            the property name
     */
    private BaseballcardConfigurationProperties(final String property) {
        this.property = property;
    }

    /**
     * Gets the property name.
     * 
     * @return the property name
     */
    @Override
    public final String getPropertyName() {
        return this.property;
    }
}