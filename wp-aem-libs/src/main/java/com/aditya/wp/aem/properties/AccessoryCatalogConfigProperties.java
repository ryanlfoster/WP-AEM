/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.properties;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;

import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.utils.diff.DiffUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum AccessoryCatalogConfigProperties implements LSLRConfigProperties {

    /** The All Accessories Tab Label property (e.g., the 'All' tab) in the Catalog View. */
    // Note: Keep the property name as-is for backwards compatibility with existing content.
    ACCESSORY_CATALOGVIEW_ALL_ACCESSORIES_TAB_LABEL("acc_config_c1/allcategorieslabel"),

    /** The Catalog Title property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_CATALOG_TITLE("acc_config_c1/catalogview/catalogtitle"),

    /** The path to the default thumbnail image displayed in the Catalog View for the ACE feed. */
    ACCESSORY_CATALOGVIEW_ACE_DEFAULT_THUMBNAIL_PATH("acc_config_c1/catalogview/acedefaultthumbnailpath"),

    /**
     * The path to the default thumbnail image displayed in the Catalog View for the Ignition feed
     * or manual data.
     */
    ACCESSORY_CATALOGVIEW_IGNITION_DEFAULT_THUMBNAIL_PATH("acc_config_c1/catalogview/ignitiondefaultthumbnailpath"),

    /** The Featured Accessories Tab Label property (e.g., the 'Featured' tab) in the Catalog View. */
    ACCESSORY_CATALOGVIEW_FEATURED_ACCESSORIES_TAB_LABEL("acc_config_c1/catalogview/featuredtablabel"),

    /** The Name Sort Enabled property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_NAME_SORT_ENABLED("acc_config_c1/catalogview/namesortenabled"),

    /** The Name Sort Label property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_NAME_SORT_LABEL("acc_config_c1/catalogview/namesortlabel"),

    /** The Ascending Name Label property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_NAME_ASC_LABEL("acc_config_c1/catalogview/nameascendinglabel"),

    /** The Descending Name Label property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_NAME_DESC_LABEL("acc_config_c1/catalogview/namedescendinglabel"),

    /** The Package Price property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_PACKAGE_PRICE_LABEL("acc_config_c1/catalogview/packagepricelabel"),

    /** The Price Label property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_PRICE_LABEL("acc_config_c1/catalogview/pricelabel"),

    /** The Price Sort Enabled property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_PRICE_SORT_ENABLED("acc_config_c1/catalogview/pricesortenabled"),

    /** The Price Sort Label property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_PRICE_SORT_LABEL("acc_config_c1/catalogview/pricesortlabel"),

    /** The Ascending Name Label property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_PRICE_ASC_LABEL("acc_config_c1/catalogview/priceascendinglabel"),

    /** The Descending Name Label property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_PRICE_DESC_LABEL("acc_config_c1/catalogview/pricedescendinglabel"),

    /** The Starting Price Label property in the Catalog View. */
    ACCESSORY_CATALOGVIEW_STARTING_PRICE_LABEL("acc_config_c1/catalogview/startingpricelabel"),

    /** The Installation Time Label property in the Item View. */
    ACCESSORY_ITEMVIEW_INSTALLATION_TIME_LABEL("acc_config_c1/installationtimelabel"),

    /** The Part Number Label property in the Item View. */
    ACCESSORY_ITEMVIEW_PART_LABEL("acc_config_c1/partnumberlabel"),

    /** The Price Label property in the Item View. */
    ACCESSORY_ITEMVIEW_PRICE_LABEL("acc_config_c1/pricelabel"),

    /** The Image Label property in the Table View. */
    ACCESSORY_TABLEVIEW_IMAGE_LABEL("acc_config_c1/tableview/imagelabel"),

    /** The Image Link Text property in the Table View. */
    ACCESSORY_TABLEVIEW_IMAGE_LINK_TEXT("acc_config_c1/tableview/imagelinktext"),

    /** The Installation Time Label property in the Table View. */
    ACCESSORY_TABLEVIEW_INSTALLATION_TIME_LABEL("acc_config_c1/tableview/installationtimelabel"),

    /** The Item Label property in the Table View. */
    ACCESSORY_TABLEVIEW_ITEM_LABEL("acc_config_c1/tableview/itemlabel"),

    /** The Package Price Label property in the Table View. */
    ACCESSORY_TABLEVIEW_PACKAGE_PRICE_LABEL("acc_config_c1/tableview/packagepricelabel"),

    /** The Part Number Label property in the Table View. */
    ACCESSORY_TABLEVIEW_PART_NUMBER_LABEL("acc_config_c1/tableview/partnumberlabel"),

    /** The Price Label property in the Table View. */
    ACCESSORY_TABLEVIEW_PRICE_LABEL("acc_config_c1/tableview/pricelabel"),

    /** The Override Item Images property in the Table View. */
    ACCESSORY_TABLEVIEW_OVERRIDE_ITEM_IMAGES("acc_config_c1/tableview/overrideitemimages"),

    /** Continue Image Reference */
    CONTINUE_ICON_REF("video_endframe_config/continueIconReference"),

    /** Continue Label */
    CONTINUE_LABEL("video_endframe_config/continueLabel"),

    /** Replay Image Reference */
    REPLAY_ICON_REF("video_endframe_config/replayIconReference"),

    /** Replay Label */
    REPLAY_LABEL("video_endframe_config/replayLabel");

    private static final String LANGUAGE_CONFIG_STRING = "language_config/";

    private String propertyName;

    /**
     * Instantiates a new config value.
     * 
     * @param propertyName
     *            the property name
     */
    private AccessoryCatalogConfigProperties(final String propertyName) {
        this.propertyName = propertyName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.config.LSLRConfigProperties#getPropertyName()
     */
    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.config.LSLRConfigProperties#getConfigValueFrom(com.gm.gssm.gmds
     * .cq.services.config.LanguageSLRService, com.day.cq.wcm.api.Page)
     */
    @Override
    public String getConfigValueFrom(final LanguageSLRService languageService,
                                     final Page currentPage) {
        return languageService.getConfigValue(currentPage, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.config.LSLRConfigProperties#getConfigValueFrom(org.apache.sling
     * .api.resource.Resource, org.apache.sling.api.scripting.SlingScriptHelper)
     */
    @Override
    public String getConfigValueFrom(final Resource resource,
                                     final SlingScriptHelper slingScriptHelper) {
        final String actualPropertyName = getPropertyName().replace(LANGUAGE_CONFIG_STRING, "");
        return DiffUtil.getDiff(resource, actualPropertyName, false, slingScriptHelper);
    }
}