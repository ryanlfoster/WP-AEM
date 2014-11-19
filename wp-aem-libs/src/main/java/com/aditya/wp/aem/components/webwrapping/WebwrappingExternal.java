/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.components.webwrapping;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.wp.aem.components.AbstractComponent;
import com.aditya.wp.aem.utils.ProtocolUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class WebwrappingExternal extends AbstractComponent {

    public static final String FOR_EXTERNAL_APPLICATION_ATTRIBUTE = "for_external_application";

    public static final String PROTOCOL_AND_HOST_ATTRIBUTE = "protocol_and_host";
    public static final String HTTP_HOST_ATTRIBUTE = "http_host";

    private String webwrappingMode;

    /** The protocol and host which will be used in the snippets for external applications to build absolute urls. */
    private String protocolAndHost;
    private String httpHost;
    private boolean secureRequest;

    /**
     * Checks, if the request has the value "true" in the attribute "use_external_application".
     * 
     * @param pageContext
     *            the page context
     * @return true, if successful
     */
    private boolean requestHasUseExternalApplication() {
        final String useExternalApplicationString = (String) getRequest().getAttribute(
                FOR_EXTERNAL_APPLICATION_ATTRIBUTE);
        return Boolean.parseBoolean(useExternalApplicationString);
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.components.AbstractComponent#init()
     */
    @Override
    public void init() {
    	this.secureRequest = isSecureRequestSelector();

        // use the selected applicationType of the application and consider the set request flag that indicates that the
        // snippet is used for external applications
        this.webwrappingMode = getPropertyAsString("webwrapping" + Webwrapping.APPLICATION_TYPE_PARAM);

        final boolean requestHasUseExternalApplication = requestHasUseExternalApplication();
        if (requestHasUseExternalApplication) {
            this.webwrappingMode = Webwrapping.APPLICATION_TYPE_EXTERNAL;
        }
        final HttpServletRequest request = (HttpServletRequest) getRequest();
        this.httpHost = ProtocolUtil.getHttpHostFromConfiguration(request);
        this.protocolAndHost = this.httpHost;
        if (this.secureRequest) {
            this.protocolAndHost = ProtocolUtil.getHttpsHostFromConfiguration(request);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.components.AbstractComponent#getResourceType()
     */
    @Override
    public String getResourceType() {
    	return null;
    }

    /**
     * Returns if Webwrapping is set to simple mode.
     * 
     * @return the simpleMode
     */
    public final boolean isExternalMode() {
        return Webwrapping.APPLICATION_TYPE_EXTERNAL.equals(getWebwrappingMode());
    }

    /**
     * Return the port and host.
     * 
     * @return the absoluteUrl
     */
    public final String getProtocolAndHost() {
        return this.protocolAndHost;
    }

    /**
     * Return the http host.
     * 
     * @return the absoluteUrl
     */
    public final String getHttpHost() {
        return this.httpHost;
    }

    /**
     * Returns the mode that this webwrapping template has been set to.
     * 
     * @return the webwrappingMode
     */
    public final String getWebwrappingMode() {
        return this.webwrappingMode;
    }

    /**
     * Returns the absolute URL if the webwrapping template has been set to external mode or an empty string otherwise.
     * 
     * @return absolute path or empty string
     */
    public final String getProtocolAndHostOnDemand() {
        String protocolAndHostOnDemand = "";
        if (isExternalMode()) {
            protocolAndHostOnDemand = getProtocolAndHost();
        }
        return protocolAndHostOnDemand;
    }

    /**
     * Returns true if this request is HTTPS or not.
     * 
     * @return the secureRequest
     */
    public final boolean isSecureRequest() {
        return this.secureRequest;
    }

    /**
     * Checks the selectors inside the request and returns true if one selector is https.
     * 
     * @return true if https selector is present
     */
    private boolean isSecureRequestSelector() {
        final String[] selectors = getRequest().getRequestPathInfo().getSelectors();
        for (String selector : selectors) {
            if ("https".equalsIgnoreCase(selector)) {
                return true;
            }
        }
        return false;
    }
}
