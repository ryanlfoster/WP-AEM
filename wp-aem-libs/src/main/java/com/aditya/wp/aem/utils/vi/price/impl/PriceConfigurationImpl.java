/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.vi.price.impl;

import com.aditya.wp.aem.properties.CompanyConfigProperties;
import com.aditya.wp.aem.properties.PriceConfigProperties;
import com.aditya.wp.aem.services.core.ServiceProvider;
import com.aditya.wp.aem.utils.vi.price.capi.PriceConfiguration;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class PriceConfigurationImpl implements PriceConfiguration {

    private final ServiceProvider serviceProvider;

    /** the current page helping getting configuration values. */
    private final Page page;

    /**
     * Constructor.
     * 
     * @param serviceProvider
     *            the {@link ServiceProvider}
     * @param page
     *            the {@link Page}
     */
    public PriceConfigurationImpl(final ServiceProvider serviceProvider, final Page page) {
        this.serviceProvider = serviceProvider;
        this.page = page;
    }

    @Override
    public final boolean getOverwriteSsi() {
        return "yes"
                .equals(this.serviceProvider.getLanguageSLRService().getConfigValue(this.page, PriceConfigProperties.VEHICLE_DATA_CONFIG_OVERWRITESSIPRICE));
    }

    @Override
    public final String getPriceLabel() {
        return this.serviceProvider.getLanguageSLRService().getConfigValue(this.page, PriceConfigProperties.TX1_PRICE_LABEL);
    }

    @Override
    public final boolean getPriceLabelRightAligned() {
        return "right".equals(this.serviceProvider.getLanguageSLRService().getConfigValue(this.page, PriceConfigProperties.PRICE_LABEL_POSITION));
    }

    @Override
    public final String getShortPriceLabel() {
        return this.serviceProvider.getLanguageSLRService().getConfigValue(this.page, PriceConfigProperties.SHORT_PRICE_LABEL);
    }

    @Override
    public final boolean getShowPrice() {
        return Boolean.parseBoolean(this.serviceProvider.getCompanyService().getConfigValue(this.page, CompanyConfigProperties.SHOW_MARRKET_PRICE));
    }

    @Override
    public String getGrossPriceSuffix() {
        return this.serviceProvider.getLanguageSLRService().getConfigValue(this.page, PriceConfigProperties.GROSS_PRICE_SUFFIX);
    }

    @Override
    public String getNetPriceSuffix() {
        return this.serviceProvider.getLanguageSLRService().getConfigValue(this.page, PriceConfigProperties.NET_PRICE_SUFFIX);
    }
}