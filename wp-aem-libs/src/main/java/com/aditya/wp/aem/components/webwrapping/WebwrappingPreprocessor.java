/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.components.webwrapping;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.wp.aem.components.AbstractComponent;
import com.aditya.wp.aem.global.GmdsRequestAttribute;
import com.aditya.wp.aem.services.webwrapping.WebwrappingService;
import com.aditya.wp.aem.wrapper.DeepResolvingResourceUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class WebwrappingPreprocessor extends AbstractComponent {

    private String entryPointUrl;
    private String absoluteExternalApplicationUrl;
    private String applicationType;
    private Resource webwrappingResource;

    /**
     * Gets the entry point url.
     * 
     * @return the complete URL to the entry point that is currently configured on the webwrapping-page.
     */
    private String getEntryPointURL() {
        if (null == this.entryPointUrl) {
            final WebwrappingService webwrappingService = getSlingScriptHelper().getService(WebwrappingService.class);
            this.entryPointUrl = webwrappingService.buildEntryPointUrl(getRequest(), this.webwrappingResource, null, null, null);
        }
        return this.entryPointUrl;
    }

    /**
     * Gets the absolute external application url.
     * 
     * @return the URL of the external application currently configured on the webwrapping-page.
     */
    private String getAbsoluteExternalApplicationUrl() {
        return this.absoluteExternalApplicationUrl;
    }

    /**
     * Gets the application url.
     * 
     * @return the application url
     */
    public final String getApplicationUrl() {
        if (Webwrapping.APPLICATION_TYPE_EXTERNAL.equals(this.applicationType)) {
            return getAbsoluteExternalApplicationUrl();
        } else {
            return getEntryPointURL();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.components.AbstractComponent#init()
     */
    @Override
    public final void init() {
    	this.webwrappingResource = getResource().getResourceResolver().getResource(getResource(), "webwrapping");
        if (this.webwrappingResource != null) {
            ValueMap properties = DeepResolvingResourceUtil.getValueMap(this.webwrappingResource);
            this.applicationType = properties.get(Webwrapping.APPLICATION_TYPE_PARAM, "");
            this.absoluteExternalApplicationUrl = properties.get(Webwrapping.ABSOLUTE_EXTERNAL_APPLICATION_URL_PARAM,
                    "");
        }
        // Data from this object may be needed several times during request handling, so put this instance
        // into the request:
        GmdsRequestAttribute.WEBWRAPPING_PREPROCESSOR.set(getRequest(), this);
    }

	@Override
    public String getResourceType() {
	    return null;
    }
}
