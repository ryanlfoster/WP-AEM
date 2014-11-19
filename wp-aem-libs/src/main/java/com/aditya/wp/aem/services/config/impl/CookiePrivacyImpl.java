/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.config.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONArray;

import com.aditya.gmwp.aem.properties.CompanyConfigProperties;
import com.aditya.gmwp.aem.properties.CompanyConfigResourcePath;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.CookiePrivacyService;
import com.aditya.gmwp.aem.utils.AnchorUtil;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(value = CookiePrivacyService.class)
@Component(name = "com.aditya.gmwp.aem.services.config.CookiePrivacyService", label = "GMWP Cookie Privacy", metatype = true)
public class CookiePrivacyImpl implements CookiePrivacyService {

    @Reference
    private CompanyService companyService;
    
    private static final String CONSENT_REQUIRED = "required";
    private static final String INCLUSION_ALWAYS_ALLOWED = "not_required";

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.config.CookiePrivacyService#getTrackingProvidersAsJSONString(com.day.cq.wcm.api.Page, java.lang.String)
	 */
	@Override
	public String getTrackingProvidersAsJSONString(final Page currentPage,
                                                   final String status) {
		final List<String> trackingProviders = getTrackingProviders(currentPage, status);
        final JSONArray trackingProvidersJSON = new JSONArray();

        for (String trackingProvider : trackingProviders) {
            trackingProvidersJSON.put(trackingProvider);
        }
        return trackingProvidersJSON.toString();
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.config.CookiePrivacyService#isInclusionAlwaysAllowed(com.day.cq.wcm.api.Page, java.lang.String)
	 */
	@Override
	public boolean isInclusionAlwaysAllowed(final Page currentPage,
                                            final String trackingProvider) {
		final List<String> trackingProviders = getTrackingProviders(currentPage, INCLUSION_ALWAYS_ALLOWED);
		return trackingProviders.contains(trackingProvider);
	}

	/**
     * Returns a list of all tracking providers with a certain status that were selected in the
     * company template.
     * 
     * @param currentPage
     *            the current page
     * @param status
     *            the status of the authors selection
     * @return List<String> a list of Tracking Cookies
     */
    private List<String> getTrackingProviders(final Page currentPage,
                                              final String status) {
        final List<String> trackingProviders = new ArrayList<String>();
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_ADDTHIS_ENABLED)) {
        	trackingProviders.add("addthis");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_BRIGHTTAG_ENABLED)) {
            trackingProviders.add("brighttag");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_CELEBRUS_ENABLED)) {
            trackingProviders.add("celebrus");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_DARTTAGGING_ENABLED)) {
            trackingProviders.add("darttagging");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_DARTTAGGING_ENABLED)) {
            trackingProviders.add("edx");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_DARTTAGGING_ENABLED)) {
            trackingProviders.add("eloqua");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_FORESEE_ENABLED)) {
            trackingProviders.add("foresee");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_QUALTRICS_ENABLED)) {
            trackingProviders.add("qualtrics");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_ELOQUA_ENABLED)) {
            trackingProviders.add("googleanalytics");
        }

        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_GOOGLEADWORDS_ENABLED)) {
            trackingProviders.add("googleadwords");
        }

        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_IPERCEPTION_ENABLED)) {
            trackingProviders.add("iperception");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_NETMINING_ENABLED)) {
            trackingProviders.add("netmining");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_OMNITURE_ENABLED)) {
            trackingProviders.add("omniture");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_PSYMA_ENABLED)) {
            trackingProviders.add("psyma");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_SEARCHIGNITE_ENABLED)) {
            trackingProviders.add("searchignite");
        }
        if (isTrackingProviderRequired(currentPage, status, CompanyConfigProperties.COOKIEPRIVACY_TESTANDTARGET_ENABLED)) {
            trackingProviders.add("testandtarget");
        }

        final String scriptConfigPath = this.companyService.getResourcePath(currentPage, CompanyConfigResourcePath.SCRIPT_CONFIG_PATH);
        if (StringUtils.isNotBlank(scriptConfigPath)) {
            final Resource scriptConfigResource = currentPage.getContentResource().getResourceResolver().getResource(scriptConfigPath);
            if (scriptConfigResource != null) {
                final Iterator<Resource> scriptConfigResources = scriptConfigResource.listChildren();
                while (scriptConfigResources.hasNext()) {
                    final GMResource scriptResource = new GMResource(scriptConfigResources.next());
                    if (status.equals(scriptResource.getPropertyAsString("privacy", INCLUSION_ALWAYS_ALLOWED))
                            && StringUtils.isNotBlank(scriptResource.getPropertyAsString("path"))) {
                        final String name = StringUtils.isNotBlank(scriptResource.getPropertyAsString("name")) ? scriptResource.getPropertyAsString("name")
                                : scriptResource.getPropertyAsString("path");
                        trackingProviders.add(AnchorUtil.createValidId(name));
                    }
                }
            }
        }

        return trackingProviders;
    }

    private boolean isTrackingProviderRequired(final Page currentPage, final String status, final CompanyConfigProperties property) {
    	return StringUtils.equals(status, this.companyService.getConfigValue(currentPage, property))
    			|| (StringUtils.equals(CONSENT_REQUIRED, status) && StringUtils.isBlank(this.companyService.getConfigValue(currentPage, property)));
    }
}
