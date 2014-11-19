/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;

import com.aditya.gmwp.aem.properties.BaseballcardBodystyleProperties;
import com.aditya.gmwp.aem.properties.LanguageConfigProperties;
import com.aditya.gmwp.aem.properties.Properties;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum ShoppingLink {
    BUILD_YOUR_OWN(LanguageConfigProperties.BUILDYOUROWN_LINKTEXT, //
            null, //
            null, //
            "byo", //
            BaseballcardBodystyleProperties.BUILD_YOUR_OWN_LINK, //
            BaseballcardBodystyleProperties.BUILD_YOUR_OWN_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    COMPARE_TRIMS(LanguageConfigProperties.COMPARETRIMS_LINKTEXT, //
            null, //
            null, //
            "cpt", //
            BaseballcardBodystyleProperties.COMPARE_TRIMS_LINK, //
            BaseballcardBodystyleProperties.COMPARE_TRIMS_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITH_HASH_PARAMS), //
    COMPETITIVE_FEATURE_COMPARISON(LanguageConfigProperties.COMPETITIVE_FEATURE_COMPARISON_LINKTEXT, //
            null, //
            null, //
            "cfc", //
            BaseballcardBodystyleProperties.COMPETITIVE_FEATURE_COMPARISON_LINK, //
            BaseballcardBodystyleProperties.COMPETITIVE_FEATURE_COMPARISON_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    COMPETITIVE_PHOTO_COMPARISON(LanguageConfigProperties.COMPETITIVE_PHOTO_COMPARISON_LINKTEXT, //
            null, //
            null, //
            "cpc", //
            BaseballcardBodystyleProperties.COMPETITIVE_PHOTO_COMPARISON_LINK, //
            BaseballcardBodystyleProperties.COMPETITIVE_PHOTO_COMPARISON_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    DEALER_LOCATOR(LanguageConfigProperties.DEALERLOCATOR_LINKTEXT, //
            LanguageConfigProperties.DEALERLOCATOR_LINK, //
            LanguageConfigProperties.DEALERLOCATOR_EXTERNAL_LINK, //
            "lad", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    ESTIMATE_PAYMENTS(LanguageConfigProperties.ESTIMATE_PAYMENTS_LINKTEXT, //
            LanguageConfigProperties.ESTIMATE_PAYMENTS_LINK, //
            LanguageConfigProperties.ESTIMATE_PAYMENTS_EXTERNAL_LINK, //
            "epm", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    GALLERY(LanguageConfigProperties.GALLERY_LINKTEXT, //
            null, //
            null, //
            "gal", //
            BaseballcardBodystyleProperties.GALLERY_LINK, //
            null, // the gallery can't be an external link
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    MODEL_OVERVIEW(LanguageConfigProperties.MODELOVERVIEW_LINKTEXT, //
            null, //
            null, //
            "mov", //
            BaseballcardBodystyleProperties.MODEL_OVERVIEW_LINK, //
            null, // the model overview could not be an external link
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    MORE_DETAILS(LanguageConfigProperties.MORE_DETAILS_LINK_TEXT, //
            null, // DOFL Settings???
            null, //
            null, //
            null, //
            null, //
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    PLAN_ROUTE(LanguageConfigProperties.PLANROUTE_LINKTEXT, //
            LanguageConfigProperties.PLANROUTE_LINK, //
            LanguageConfigProperties.PLANROUTE_EXTERNAL_LINK, //
            "plr", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    REQUEST_A_BROCHURE(LanguageConfigProperties.REQUESTBROCHURE_LINKTEXT, LanguageConfigProperties.REQUESTBROCHURE_LINK, //
            LanguageConfigProperties.REQUESTBROCHURE_EXTERNAL_LINK, //
            "rab", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    REQUEST_A_QUOTE(LanguageConfigProperties.REQUESTQUOTE_LINKTEXT, //
            LanguageConfigProperties.REQUESTQUOTE_LINK, //
            LanguageConfigProperties.REQUESTQUOTE_EXTERNAL_LINK, //
            "raq", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    REQUEST_A_LEASE(LanguageConfigProperties.REQUESTLEASE_LINKTEXT, //
            LanguageConfigProperties.REQUESTLEASE_LINK, //
            LanguageConfigProperties.REQUESTLEASE_EXTERNAL_LINK, //
            "ral", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    REQUEST_A_SERVICE(LanguageConfigProperties.REQUESTSERVICE_LINKTEXT, //
            LanguageConfigProperties.REQUESTSERVICE_LINK, //
            LanguageConfigProperties.REQUESTSERVICE_EXTERNAL_LINK, //
            "ras", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    REQUEST_A_TEST_DRIVE(LanguageConfigProperties.TESTDRIVE_LINKTEXT, //
            LanguageConfigProperties.TESTDRIVE_LINK, //
            LanguageConfigProperties.TESTDRIVE_EXTERNAL_LINK, //
            "rtd", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    TIRE_FINDER(LanguageConfigProperties.TIREFINDER_LINKTEXT, //
            LanguageConfigProperties.TIREFINDER_LINK, //
            LanguageConfigProperties.TIREFINDER_EXTERNAL_LINK, //
            "tfa", // // tfa = tire finder app
            null, //
            null, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //            
    REQUEST_NEWSLETTER(LanguageConfigProperties.REQUESTNEWSLETTER_LINKTEXT, LanguageConfigProperties.REQUESTNEWSLETTER_LINK, //
            LanguageConfigProperties.REQUESTNEWSLETTER_EXTERNAL_LINK, //
            "rnl", //
            null, //
            null, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    SEARCH_INVENTORY(LanguageConfigProperties.SEARCHINVENTORY_LINKTEXT, //
            LanguageConfigProperties.SEARCHINVENTORY_LINK, //
            LanguageConfigProperties.SEARCHINVENTORY_EXTERNAL_LINK, //
            "lnv", // lnv = locate new vehicle
            BaseballcardBodystyleProperties.SEARCH_INVENTORY_LINK, //
            BaseballcardBodystyleProperties.SEARCH_INVENTORY_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    VIEW_CURRENT_OFFERS(LanguageConfigProperties.VIEW_CURRENT_OFFERS_LINKTEXT, //
            LanguageConfigProperties.VIEW_CURRENT_OFFERS_LINK, //
            LanguageConfigProperties.VIEW_CURRENT_OFFERS_EXTERNAL_LINK, //
            "vco", //
            BaseballcardBodystyleProperties.VIEW_CURRENT_OFFERS_INTERNAL_LINK, //
            BaseballcardBodystyleProperties.VIEW_CURRENT_OFFERS_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITH_CGI_PARAMS), //
    VIEW_CONFIGURATION(LanguageConfigProperties.VIEW_CONFIGURATION_LINKTEXT, //
            null, //
            null, //
            "byo", //
            BaseballcardBodystyleProperties.BUILD_YOUR_OWN_LINK, //
            BaseballcardBodystyleProperties.BUILD_YOUR_OWN_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    CLIPPED_COMPETITIVE_COMPARISON(LanguageConfigProperties.CLIPPED_COMPETITIVE_COMPARISON_LINKTEXT, //
            null, //
            null, //
            "ccc", //
            BaseballcardBodystyleProperties.CLIPPED_COMPETITIVE_COMPARISON_LINK, //
            BaseballcardBodystyleProperties.CLIPPED_COMPETITIVE_COMPARISON_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    REVIEW_LINK(LanguageConfigProperties.REVIEW_LINKS_TITLE, //
            null, //
            null, //
            null, //
            BaseballcardBodystyleProperties.REVIEW_LINK_INTERNAL_LINK, //
            BaseballcardBodystyleProperties.REVIEW_LINK_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITHOUT_PARAMS), //
    ACCESSORIES_LINK(LanguageConfigProperties.ACCESSORIES_LINKS_TITLE, //
            null, //
            null, //
            null, //
            BaseballcardBodystyleProperties.ACCESSORIES_INTERNAL_LINK, //
            BaseballcardBodystyleProperties.ACCESSORIES_EXTERNAL_LINK, //
            ShoppingLinkType.LINK_WITHOUT_PARAMS); //

    private String ddpKey;

    private Properties externalShoppingLink;

    private Properties internalShoppingLink;

    private LanguageConfigProperties lslrExternalLink;

    private LanguageConfigProperties lslrInternalLink;

    private LanguageConfigProperties lslrLinktext;

    private ShoppingLinkType type;

    /**
     * Instantiates a new shopping link.
     * 
     * @param lslrLinktext
     *            the key to the link text configured in the language repository.
     * @param lslrInternalLink
     *            the lslr link the key to the link configured in the language repository.
     * @param lslrExternalLink
     *            the lslr external link the key to the link configured in the language repository.
     * @param ddpKey
     *            the key which is used in data supply XML. Only relevant if the shopping link
     *            refers to an external application like "build your own".
     * @param internalShoppingLink
     *            when the link is not configured in the language repository, it will be looked up
     *            in page properties of the bodystyle page.
     * @param externalShoppingLink
     *            when the link is not configured in the language repository, it will be looked up
     *            in page properties of the bodystyle page.
     * @param type
     *            the type of the shopping-link
     */
    ShoppingLink(final LanguageConfigProperties lslrLinktext, final LanguageConfigProperties lslrInternalLink, final LanguageConfigProperties lslrExternalLink,
            final String ddpKey, final Properties internalShoppingLink, final Properties externalShoppingLink,
            final ShoppingLinkType type) {
        this.lslrLinktext = lslrLinktext;
        this.lslrInternalLink = lslrInternalLink;
        this.lslrExternalLink = lslrExternalLink;
        this.ddpKey = ddpKey;
        this.internalShoppingLink = internalShoppingLink;
        this.externalShoppingLink = externalShoppingLink;
        this.type = type;
    }

    /**
     * The key which is used in data supply XML. Will be null for shopping links that do not refer
     * to an external application.
     * 
     * @return the ddp key
     */
    public final String getDdpKey() {
        return this.ddpKey;
    }

    /**
     * Gets the external shopping link.
     * 
     * @return external shopping link
     */
    public final Properties getExternalShoppingLink() {
        return this.externalShoppingLink;
    }

    /**
     * Gets the baseballcard property.
     * 
     * @return the baseballcard property
     */
    public final Properties getInternalShoppingLink() {
        return this.internalShoppingLink;
    }

    /**
     * Gets the lslr external link.
     * 
     * @return the lslr external link
     */
    public final LanguageConfigProperties getLslrExternalLink() {
        return this.lslrExternalLink;
    }

    /**
     * Gets the lslr link.
     * 
     * @return the lslr link
     */
    public final LanguageConfigProperties getLslrInternalLink() {
        return this.lslrInternalLink;
    }

    /**
     * Returns the lslr link text.
     * 
     * @return the technical key for which in LSLR a human readable label is configured.
     */
    public final LanguageConfigProperties getLslrLinktext() {
        return this.lslrLinktext;
    }

    /**
     * Returns the type.
     * 
     * @return the technical type of the shopping link
     */
    public final ShoppingLinkType getType() {
        return this.type;
    }
}
