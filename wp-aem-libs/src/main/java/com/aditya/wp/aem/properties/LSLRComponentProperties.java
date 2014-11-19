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
public enum LSLRComponentProperties {

    /**
     * Get the resource path to the "Brochure Config" from the language specific label repository
     * (LSLR).
     */
    ACCESSORY_CONFIG_PATH("acc_config_c1"),

    /**
     * Get the resource path to the "Brochure Config" from the language specific label repository
     * (LSLR).
     */
    BROCHURE_CONFIG_PATH("cnt_brochure_config_c1"),

    /**
     * Get the resource path to the "Component Library" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    COMPONENT_LIBRARY_CONFIG_PATH("cl_config_c1"),

    /**
     * Get the resource path to the "Global Legal" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    FOOTER_GLOBAL_LEGAL_PATH("cnt_legalglobal_c1"),

    /**
     * Get the resource path to the "Footer Navigation" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    FOOTER_NAVIGATION_2("nav_footer_c2"),

    /**
     * Get the resource path to the "Header Meta Navigation" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    FOOTER_NAVIGATION_ROW1_PATH("nav_footer_c1_row1"),

    /**
     * Get the resource path to the "Footer Navigation Row 1" from LanguageSLR page fitting to
     * called "currentPage". The returned path has to be included with sling:include.
     */
    FOOTER_NAVIGATION_ROW2_PATH("nav_footer_c1_row2"),

    /**
     * Get the resource path to the "Glossary Configuration" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    GLOSSARY_CONFIG("glossary_config_c1"),

    /**
     * Get the resource path to the "Footer Navigation Row 2" from LanguageSLR page fitting to
     * called "currentPage". The returned path has to be included with sling:include.
     */
    HEADER_METANAVIGATION_PATH("nav_meta_c1"),

    /**
     * Get the resource path to the "Javascript Requirement" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    JAVASCRIPT_REQUIRED_PATH("javascript_required_config_c1"),

    /**
     * Get the resource path to the "Layer Button Component" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    LAYER_BUTTON_PATH("btn_lyr_c1"),

    /**
     * Get the resource path to the "Header Search" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    SEARCH_CONFIG_PATH("search_config_c1"),

    /**
     * Get the resource path to the "Social Links" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    SOCIAL_LINKS_PATH("social_links_c1"),

    /**
     * Get the resource path to the "Navigation Tools Links" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    NAV_TOOLS_PATH("shoppingtool_links_c1"),

    /**
     * Get the resource path to the "Item Attribute Container" from LanguageSLR page fitting to
     * called "currentPage". The returned path has to be included with sling:include.
     */
    ITEM_ATTRIBUTE_CONTAINER("attr_container_c1"),

    /**
     * Get the resource path to the "DAM path" from LanguageSLR page fitting to called
     * "currentPage". The returned path has to be included with sling:include.
     */
    DAM_PATH("dam_path_c1"),

    /**
     * Get the resource path to the "Zip Code Configuration Container" from LanguageSLR page fitting
     * to called "currentPage". The returned path has to be included with sling:include.
     */
    ZIP_CONFIG_CONTAINER("config_ut_zip_container_c1"),

    HMC_COMPARISON_CATEGORIES("hmc_comparison_config_c1"),

    /**
     * 
     */
    FEATURES_AND_SPECS("features_and_specs_config_c1");

    /** The resource name. */
    private String resourceName;

    /**
     * Instantiates a new resource path.
     * 
     * @param resourceName
     *            the resource name
     */
    private LSLRComponentProperties(final String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Gets the resource name.
     * 
     * @return the resource name
     */
    public String getResourceName() {
        return this.resourceName;
    }
}
