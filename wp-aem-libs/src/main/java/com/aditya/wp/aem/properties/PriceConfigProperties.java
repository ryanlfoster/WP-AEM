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
public enum PriceConfigProperties implements LSLRConfigProperties {

    /** text for the freight charge list component, to display the freight charge destination price. */
    FREIGHT_CHARGE_PRICE("language_config/freight_charge_price"),

    /** text for the freight charge list component, to display the freight charge start price. */
    FREIGHT_CHARGE_START_PRICE("language_config/freight_charge_start_price"),

    /** text for the freight charge list component, to display the freight charge total price. */
    FREIGHT_CHARGE_TOTAL_PRICE("language_config/freight_charge_total_price"),

    /** The legal link price. */
    LEGAL_PRICE_LINK("language_config/legal_price_link"),

    /** The legal price linktext. */
    LEGAL_PRICE_LINKTEXT("language_config/legal_price_linkText"),

    /** The LEGA l_ pric e_ linktitle. */
    LEGAL_PRICE_LINKTITLE("language_config/legal_price_linkTitle"),

    /** Get the legal price reference to mark if below the price should be display a legal text. */
    LEGAL_PRICE_SUFFIX("language_config/legal_price_suffix"),

    /** The price label position. */
    PRICE_LABEL_POSITION("language_config/price_label_position"),

    PRICE_TABLE_DROPDOWN("language_config/price_table_dropdown_label"),

    PRICE_TABLE_HEADER("language_config/price_table_header_1_label"),

    PRICE_TABLE_HEADER2("language_config/price_table_header_2_label"),

    /** The short_ price_label. */
    SHORT_PRICE_LABEL("language_config/short_price_label"),

    /** The TX1 price label. */
    TX1_PRICE_LABEL("language_config/tx1_price_label"),

    /**
     * The overwriteSSIPrices attribute defines if the prices should be taken directly from the
     * market.xml.
     */
    VEHICLE_DATA_CONFIG_OVERWRITESSIPRICE("vehicledata_config_c1/overwriteSSIPrices"),

    PRODUCT_ATTRIBUTE_PRICE_LABEL("product_attr_container_c1/priceLabel"),

    PRODUCT_ATTRIBUTE_PRICE_SUFFIX("product_attr_container_c1/priceSuffix"),

    NO_IMAGE_LABEL("language_config/no_image_label"),

    GROSS_PRICE_SUFFIX("language_config/gross_price_suffix"),

    NET_PRICE_SUFFIX("language_config/net_price_suffix");

    private static final String LANGUAGE_CONFIG_STRING = "language_config/";

    private String propertyName;

    /**
     * Instantiates a new config value.
     * 
     * @param propertyName
     *            the property name
     */
    private PriceConfigProperties(final String propertyName) {
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