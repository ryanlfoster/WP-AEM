/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.components.webwrapping;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.components.AbstractComponent;
import com.aditya.gmwp.aem.services.webwrapping.WebwrappedApp;
import com.aditya.gmwp.aem.services.webwrapping.WebwrappingService;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class Webwrapping extends AbstractComponent {

	private static final String RESOURCE_TYPE = "gmds/components/content/webwrapping";
    private String entryPointsJson;
    private String webwrappingAppId;
    private String webwrappingEntryPointId;
    private WebwrappingService webWrappingService;
    private String applicationType;
    private String absoluteExternalApplicationUrl;

    /**
     * The Constant APPLICATION_TYPE_WRAPPED. Flag for external applications which will not be included/wrapped in gmds
     */
    public static final String APPLICATION_TYPE_EXTERNAL = "external";

    /** The Constant APPLICATION_TYPE_WRAPPED. Flag for "normal" wrapped applications */
    public static final String APPLICATION_TYPE_WRAPPED = "wrapped";

    public static final String ABSOLUTE_EXTERNAL_APPLICATION_URL_PARAM = "absoluteExternalApplicationUrl";

    public static final String APPLICATION_TYPE_PARAM = "applicationType";

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.components.AbstractComponent#init()
	 */
	@Override
	public void init() {
        this.webwrappingAppId = getPropertyAsString("webwrappingAppId");
        this.webwrappingEntryPointId = getPropertyAsString("webwrappingEntryPointId");
        this.applicationType = getPropertyAsString(APPLICATION_TYPE_PARAM);
        this.absoluteExternalApplicationUrl = getPropertyAsString(ABSOLUTE_EXTERNAL_APPLICATION_URL_PARAM);
        this.webWrappingService = getSlingScriptHelper().getService(WebwrappingService.class);
        if (null == this.webWrappingService) {
            throw new IllegalStateException("Unable to retrieve the WebwrappingService, script-helper returned null!");
        }
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.components.AbstractComponent#getResourceType()
	 */
	@Override
	public String getResourceType() {
		return RESOURCE_TYPE;
	}
    /**
     * This returns a JSON to render the dropdown with all application paths in the dialog-dropdown. Since the
     * application name is human readable it has to be javascript escaped to avoid malformed json.
     * 
     * @return the json string with the content for the dialog.
     */
    public final String getAppPathsListJson() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final List<WebwrappedApp> webWrappedAppsList = this.webWrappingService.getAllWebWrappedApps();
        if (null == webWrappedAppsList || webWrappedAppsList.size() == 0) {
            sb.append("{value:\"\", text:\"There are no webwrapped-applications available!\"}");
        } else {
            final Iterator<WebwrappedApp> iter = webWrappedAppsList.iterator();
            int count = 0;
            while (iter.hasNext()) {
                final WebwrappedApp app = iter.next();
                final String appId = app.getAppId();
                final String appName = app.getAppName();
                if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appName)) {
                    sb.append("{");
                    sb.append("value: '").append(appId).append("',");
                    // escape javascript characters from the application name
                    sb.append("text: '").append(StringEscapeUtils.escapeJavaScript(appName)).append("'");
                    sb.append("}");
                    if (iter.hasNext()) {
                        sb.append(",");
                    }
                    count++;
                }
            }
            if (count == 0) {
                sb.append("{value:\"\", text:\"There are no webwrapped-applications available!\"}");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Gets the entry points json.
     * 
     * @return the json string for the entry points dropdown
     */
    public final String getEntryPointsJson() {
        return this.entryPointsJson;
    }

    /**
     * This method check if there are entrypoints and render the json string with include all application with the
     * corresponding entrypoints. This json will be use to get the entrypoints to the applcationsId. This method save
     * the json string in the entryPointsJson string.
     * 
     * @return true when a the json string with all applications ids and the corresponding entrypoints is saved.
     */
    public final boolean getHasEntryPointsJson() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final List<WebwrappedApp> webWrappedAppsList = this.webWrappingService.getAllWebWrappedApps();
        if (null == webWrappedAppsList || webWrappedAppsList.size() == 0) {
            return false;
        } else {
            int count = 0;
            final Iterator<WebwrappedApp> webWrappedAppsIt = webWrappedAppsList.iterator();
            while (webWrappedAppsIt.hasNext()) {
                final WebwrappedApp app = webWrappedAppsIt.next();
                final String appId = app.getAppId();
                if (StringUtils.isNotBlank(appId)) {
                    sb.append("{");
                    sb.append("\"appId\":");
                    sb.append("\"").append(appId).append("\",");
                    final List<WebwrappedApp.EntryPoint> entryPointList = app.getEntryPoints();
                    sb.append("\"entryPoints\":[");
                    if (null == entryPointList || entryPointList.size() == 0) {
                        sb.append("{name:\"There are no entry points available!\", url:\"\"}");
                    } else {
                        final Iterator<WebwrappedApp.EntryPoint> entryPointIt = entryPointList.iterator();
                        int epCount = 0;
                        while (entryPointIt.hasNext()) {
                            final WebwrappedApp.EntryPoint entryPoint = entryPointIt.next();
                            final String epName = entryPoint.getEntryPointName();
                            final String epId = entryPoint.getEntryPointId();
                            if (StringUtils.isNotBlank(epName) && StringUtils.isNotBlank(epId)) {
                                sb.append("{");
                                sb.append("\"name\":");
                                sb.append("\"").append(epName).append("\",");
                                sb.append("\"id\":");
                                sb.append("\"").append(epId).append("\"");
                                sb.append("}");
                                if (entryPointIt.hasNext()) {
                                    sb.append(",");
                                }
                                epCount++;
                            }
                        }
                        if (epCount == 0) {
                            sb.append("{name:\"There are no entry points available!\", id:\"\"}");
                        }
                    }
                    sb.append("]}");
                    if (webWrappedAppsIt.hasNext()) {
                        sb.append(",");
                    }
                    count++;
                }
            }
            if (count == 0) {
                return false;
            }
        }
        sb.append("]");
        setEntryPointsJson(sb.toString());
        return true;
    }

    /**
     * Gets the webwrapping application path.
     * 
     * @return the webwrapping application path
     */
    public final String getWebwrappingAppId() {
        return this.webwrappingAppId;
    }

    /**
     * Gets the webwrapping entry point.
     * 
     * @return the webwrapping entry point
     */
    public final String getWebwrappingEntryPointId() {
        return this.webwrappingEntryPointId;
    }

    /**
     * Returns true if this is an external webwrapping config.
     * 
     * @return true if external application
     */
    public final boolean isExternalApplication() {
        return APPLICATION_TYPE_EXTERNAL.equals(getApplicationType());
    }

    /**
     * Returns true if this is a wrapped application.
     * 
     * @return true if wrapped application
     */
    public final boolean isWrappedApplication() {
        return APPLICATION_TYPE_WRAPPED.equals(getApplicationType());
    }

    /**
     * Sets the entry points json.
     * 
     * @param entryPointsJson
     *            the entryPointsJson string
     */
    public final void setEntryPointsJson(final String entryPointsJson) {
        this.entryPointsJson = entryPointsJson;
    }

    /**
     * Gets the type of this WebWrapping configuration. Can be either '' or 'external'.
     * 
     * @return the webwrappingType
     */
    public final String getApplicationType() {
        return this.applicationType;
    }

    /**
     * Gets the configured absolute URL used with application type 'external'.
     * 
     * @return the webwrappingAbsoluteUrl
     */
    public final String getAbsoluteExternalApplicationUrl() {
        return this.absoluteExternalApplicationUrl;
    }
}
