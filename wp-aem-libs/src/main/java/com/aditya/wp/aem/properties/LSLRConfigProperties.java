/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.properties;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;

import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LSLRConfigProperties extends Properties {

    /**
     * Returns the config value for the current property from the current page using a language
     * service.
     * 
     * @param languageService
     *            the language service
     * @param currentPage
     *            the current page
     * @return the config value
     */
    String getConfigValueFrom(final LanguageSLRService languageService, final Page currentPage);

    /**
     * Returns the config value for the current property from the given resource using a sling
     * script helper.
     * 
     * @param resource
     *            the resource
     * @param slingScriptHelper
     *            the script helper
     * @return the config value
     */
    String getConfigValueFrom(final Resource resource, final SlingScriptHelper slingScriptHelper);
}
