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
public enum CompanyConfigResourcePath {
    DIVISIONAL_LOGO_PATH("divisional_logo"), //
    HIGH_RES_DIVISIONAL_LOGO_PATH("high_res_divisional_logo"), //
    GROUP_LOGO_PATH("group_logo"), //
    CGI_IMAGE_CONFIGURATION("cgi_image_config"), //
    SOCIAL_FEED_CONFIG_PATH("social_feed_config"), //
    SCRIPT_CONFIG_PATH("script_config"), //
    CGI_CONFIG_PATH("cgi_config"), //
    OPEN_GRAPH_CONFIG_PATH("open_graph_config");

    private String resourceName;

    /**
     * Constructor.
     * 
     * @param resourceName
     *            resource path.
     */
    private CompanyConfigResourcePath(final String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Gets the name of the resource.
     * 
     * @return resource path
     */
    public String getResourceName() {
        return this.resourceName;
    }
}