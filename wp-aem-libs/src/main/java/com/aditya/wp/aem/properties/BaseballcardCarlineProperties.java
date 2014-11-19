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
public enum BaseballcardCarlineProperties implements Properties {
    CARLINE_CODE("carline_code"), // The carline property for the code
    CARLINE_TEXT("carline_text"), // The carline property for the manually entered text
    FAMILY_LINK("familylink"), // The property of the family link
    MODEL_YEAR("does not matter, because this is saved as part of the carline code"), // The model year property
    MODEL_YEAR_SUFFIX("does not matter, because this is saved as part of the carline code"), // The model year suffix property
    SHOW_CARLINE_TEXT("show_carlinetext"), // The show model year property
    THUMBNAIL_DAM("imageReferenceCarline"), // The DAM thumbnail
    THUMBNAIL_URL_CARLINE("bbcThumbnailUrl"), // The URL thumbnail
    LEGAL_PRICE_SUFFIX("legal_price_suffix_bbc_n02b"), // The legal price suffix
    LEGAL_PRICE_SUFFIX_N01("legal_price_suffix_bbc_n01"), // The legal price suffix for n01
    VEHICLE_NAVIGATION_LABEL("vehicle_navigation_label"), // The vehicle navigationb label
    DDP_PRICE_OVERWRITE("ddp_price_overwrite"), // The vehicle overwrite price
    ;

    /** The property name. */
    private String propName;

    /**
     * Instantiates a new component property.
     * 
     * @param propName
     *            the prop name
     */
    private BaseballcardCarlineProperties(final String propName) {
        this.propName = propName;
    }

    /**
     * Gets the property name.
     * 
     * @return the property name
     */
    @Override
    public String getPropertyName() {
        return this.propName;
    }
}