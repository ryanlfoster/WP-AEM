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
public enum BaseballcardBodystyleProperties implements Properties {
    BODYSTYLE_CODE("bodystyle"), //
    MODEL_OVERVIEW_LINK("modeloverview_internal_link"), //
    BUILD_YOUR_OWN_EXTERNAL_LINK("build_your_own_external_link"), //
    BUILD_YOUR_OWN_LINK("build_your_own_internal_link"), //
    SEARCH_INVENTORY_EXTERNAL_LINK("search_inventory_external_link"), //
    SEARCH_INVENTORY_LINK("search_inventory_link"), //
    VIEW_CURRENT_OFFERS_EXTERNAL_LINK("offers_external_link"), //
    VIEW_CURRENT_OFFERS_INTERNAL_LINK("offers_internal_link"), //
    COMPARE_TRIMS_EXTERNAL_LINK("comparetrims_external_link"), //
    COMPARE_TRIMS_LINK("comparetrims_internal_link"), //
    COMPETITIVE_FEATURE_COMPARISON_EXTERNAL_LINK("competitive_feature_comparison_external_link"), //
    COMPETITIVE_FEATURE_COMPARISON_LINK("competitive_feature_comparison_internal_link"), //
    COMPETITIVE_PHOTO_COMPARISON_EXTERNAL_LINK("competitive_photo_comparison_external_link"), //
    COMPETITIVE_PHOTO_COMPARISON_LINK("competitive_photo_comparison_internal_link"), //
    CLIPPED_COMPETITIVE_COMPARISON_EXTERNAL_LINK("clipped_competitive_comparison_external_link"), //
    CLIPPED_COMPETITIVE_COMPARISON_LINK("clipped_competitive_comparison_internal_link"), //
    DEFAULT_LOCATION("defaultLocation"), //
    DUPLICATE("duplicate"), // Indicator variable weather the BBC is a duplicate
    DDP_PRICE_OVERWRITE("ddp_price_overwrite"), //
    TYPE_OF_PRICE("type_of_price"), //
    GALLERY_LINK("gallery_internal_link"), //
    LEGAL_PRICE_SUFFIX_BBC("legal_price_suffix_bbc"), //
    LEGAL_PRICE_SUFFIX_TBL_FS("legal_price_suffix_tbl_fs"), //
    LEGAL_PRICE_SUFFIX_TEASERAREA_N01("legal_price_suffix_vi1_n01"), //
    LEGAL_PRICE_SUFFIX_TEASERAREA_N02("legal_price_suffix_vi6_n02"), //
    LEGAL_PRICE_SUFFIX_TEASERAREA_T03("legal_price_suffix_vi4"), //
    LEGAL_PRICE_SUFFIX_TEASERAREA_T04("legal_price_suffix_vi1"), //
    LEGAL_PRICE_SUFFIX_VI2("legal_price_suffix_vi2"), //
    LEGAL_PRICE_SUFFIX_VI3("legal_price_suffix_vi3"), //
    LEGAL_PRICE_SUFFIX_VI5("legal_price_suffix_vi5"), //
    LEGAL_PRICE_SUFFIX_MODEL_PRICES("legal_price_suffix_model_prices"), //
    LEGAL_PRICE_SUFFIX_T02_VEHICLE_SELECTION("legal_price_suffix_nav_vhl_selection_2"), //
    LEGAL_TEXT("legal_text"), // Property for the override of the legal text from the LSLR
    MANUAL_FREIGHT_CHARGE_PRICE("manual_freight_charge_price"), // The fallback value of the freight_charge_price
    MANUAL_PRICE("price"), // The fallback value of the price
    PRICE_PREFIX("pricePrefix"), //
    MANUAL_TOTAL_PRICE("manual_total_price"), // The fallback value of the total_price
    NO_SELECTION("no_selection"), // Option for selecting no bodystyle
    THUMBNAIL_DAM_NEW_LARGE("imageReferenceNewLarge"), // The new DAM thumbnail_large
    THUMBNAIL_DAM_BBC_LARGE("imageReferenceLarge"), // The old DAM thumbnail_large since chevy 2.5 used as fallback
    THUMBNAIL_DAM_MEDIUM("imageReferenceMedium"), //
    THUMBNAIL_DAM_SMALL("imageReferenceSmall"), //
    THUMBNAIL_DAM_CUSTOM("imageReferenceCustom"), //
    THUMBNAIL_DAM_RATING("imageReferenceRating"), //
    THUMBNAIL_BBC_LARGE("thumbnail_large"), // The old thumbnail_large since chevy 2.5 used as fallback
    THUMBNAIL_NEW_LARGE("thumbnail_new_large"), // The new thumbnail_large
    THUMBNAIL_MEDIUM("thumbnail_medium"), //
    THUMBNAIL_SMALL("thumbnail_small"), //
    THUMBNAIL_CUSTOM("thumbnail_custom"), //
    TX3("tx3"), //
    VI1_BODYLONGDESCRIPTION("vi1_bodylongdescription"), //
    VI2_BODYLONGDESCRIPTION("vi2_bodylongdescription"), //
    VI3_BODYLONGDESCRIPTION("vi3_bodylongdescription"), //
    VI6_BODYLONGDESCRIPTION("vi6_bodylongdescription"), //
    SHORTNAME("bodystyle_shortname"), //
    MODELYEAR_SWITCH_SHORTNAME("bodystyle_modelyear_switch_shortname"), //
    PRICE_LIST_DOWNLOAD_TEXT("priceListDownloadText"), //
    SEGMENT_FILTER_LABEL("segmentFilterLabel"), //
    PRICE_LIST_DOWNLOAD_FILE_SMALL("downloadFileSmall"), //
    PRICE_LIST_DOWNLOAD_FILE_REFERENCE("downloadFileReference"), //
    PRICE_LIST_DOWNLOAD_FILE("downloadFile"), //
    PRICE_LIST_TRACKING_ID("trackingId"), //
    REEVOO_SERIES_ID("bodystyle_reevoo_series_id"), //
    REVIEW_LINK_EXTERNAL_LINK("review_link_external_link"), //
    REVIEW_LINK_INTERNAL_LINK("review_link_internal_link"), //
    ACCESSORIES_EXTERNAL_LINK("accessories_external_link"), //
    ACCESSORIES_INTERNAL_LINK("accessories_internal_link"), //
    ;

    private String propertyName;

    /**
     * Instantiates a new baseball card body style properties.
     * 
     * @param propertyName
     *            the property name
     */
    private BaseballcardBodystyleProperties(final String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Gets the property name.
     * 
     * @return the property name
     */
    @Override
    public final String getPropertyName() {
        return this.propertyName;
    }
}
