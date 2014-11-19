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
public enum ConfigProperties {
    BANDWIDTH_CHECK_COOKIE_EXPIRES_TIME("config_values/bandwidthCheckCookieExpiresTime"), //
    BANDWIDTH_CHECK_IMAGE_PATH("config_values/bandwidthCheckImagePath"), //
    BANDWIDTH_CHECK_MAX_LOADING_TIME("config_values/bandwidthCheckMaxLoadingTime"), //
    HTTP_HOST_AUTHOR("config_values/httpHostAuthor"), //
    HTTP_HOST_PUBLISH("config_values/httpHostPublish"), //
    HTTPS_HOST_AUTHOR("config_values/httpsHostAuthor"), //
    MANUEL_SITEMAP_LINK("nav_footer_config/manualSitemapLink"), //
    HTTPS_HOST_PUBLISH("config_values/httpsHostPublish"), //
    GSA_SITE_PARAMETER_SEARCH("search_config_c1/search_site_parameter"), //
    GSA_CLIENT_PRAMETER_SEARCH("search_config_c1/search_client_parameter"), //
    GSA_PROXYSTYLESHEET_PARAMETER("search_config_c1/search_proxystylesheet_parameter"), //
    GSA_SITE_SEARCH_PARAMETER_SEARCH("search_config_c1/search_sitesearch_parameter"), //
    ;

    private String propertyName;

    /**
     * @param propertyName
     *            property name
     */
    private ConfigProperties(final String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @return the path of the current property
     */
    public String getPropertyName() {
        return this.propertyName;
    }
}