/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data.impl;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.global.ShoppingLinkContext;
import com.aditya.gmwp.aem.model.ShoppingLinkModel;
import com.aditya.gmwp.aem.properties.BaseballcardConfigurationProperties;
import com.aditya.gmwp.aem.properties.Properties;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.services.vehicledata.data.BodystyleBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.ConfigurationBaseballcardData;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ConfigurationBaseballcardDataImpl implements ConfigurationBaseballcardData {

    private final ValueMap configurationProperties;
    private final BodystyleBaseballcardData bodystyleData;

    /**
     * Constructor.
     * 
     * @param configurationPage
     *            the configuration page
     * @param languageSlrService
     *            the language slr service
     * @param companyService
     *            the company service
     */
    public ConfigurationBaseballcardDataImpl(final Page configurationPage, final LanguageSLRService languageSlrService,
            final CompanyService companyService) {
        this.configurationProperties = configurationPage.getProperties("./bbc_configuration");
        this.bodystyleData = new BodystyleBaseballcardDataImpl(configurationPage.getParent(), languageSlrService,
                companyService, configurationPage);
    }

    @Override
    public final String getBaseballcardProperty(final Properties baseballCardProperty) {
        String property = null;
        if (baseballCardProperty instanceof BaseballcardConfigurationProperties) {
            property = this.configurationProperties.get(baseballCardProperty.getPropertyName(), String.class);
        } else {
            property = this.bodystyleData.getBaseballcardProperty(baseballCardProperty);
        }

        return property;
    }

    @Override
    public final List<ShoppingLinkModel> getTemplateShoppingLinks(final ShoppingLinkContext shoppingLinkContext,
                                                                  final ResourceResolver resourceResolver) throws RepositoryException {
        return this.bodystyleData.getTemplateShoppingLinks(shoppingLinkContext, resourceResolver);
    }

    @Override
    public final BodystyleBaseballcardData getBodystyleBaseballcardData() {
        return this.bodystyleData;
    }
}