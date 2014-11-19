/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.components.webwrapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.components.AbstractComponent;
import com.aditya.gmwp.aem.services.webwrapping.WebwrappedApp;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class WebwrappedAppEntryPoint extends AbstractComponent {

    /**
     * content of a tab item.
     */
    public static class EntryPointParam {

        /** The param mandatory. */
        private boolean paramMandatory;

        /** The param name. */
        private String paramName;

        /** The param value. */
        private String paramValue;

        /**
         * Gets the param mandatory.
         * 
         * @return the entry point param mandatory
         */
        public final Boolean getParamMandatory() {
            return this.paramMandatory;
        }

        /**
         * Gets the param name.
         * 
         * @return the entry point param name
         */
        public final String getParamName() {
            return this.paramName;
        }

        /**
         * Gets the param value.
         * 
         * @return the entry point param value
         */
        public final String getParamValue() {
            return this.paramValue;
        }

        /**
         * Param value.
         * 
         * @param paramValue
         *            the new entry point param value
         */
        public final void paramValue(final String paramValue) {
            this.paramValue = paramValue;
        }

        /**
         * Sets the param mandatory.
         * 
         * @param paramMandatory
         *            the new entry point param mandatory
         */
        public final void setParamMandatory(final boolean paramMandatory) {
            this.paramMandatory = paramMandatory;
        }

        /**
         * Sets the param name.
         * 
         * @param paramName
         *            the new entry point param name
         */
        public final void setParamName(final String paramName) {
            this.paramName = paramName;
        }

    }

    /** The Constant ENTRY_POINT_ID. */
    private static final String ENTRY_POINT_ID = "entryPointId";

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(WebwrappedAppEntryPoint.class);

    /** The Constant MAX_PARAMS. */
    private static final int MAX_PARAMS = 10;

    private static final int MAX_SUB_URLS = 8;

    /**
     * Add an additional property "entryPointId". This id is not maintainable by the author. Instead, this property is
     * generated from the first entry-point name that is entered by the author. When the entry-point name is changed
     * later, the generated ID remains the same.
     * 
     * @param props
     *            the Valuemap with the properties.
     * @param currentNode
     *            the current node
     */
    public static void addEntryPointId(final ValueMap props,
                                       final Node currentNode) {
        if (!props.containsKey(WebwrappedAppEntryPoint.ENTRY_POINT_ID)) {
            try {
                final String entryPointName = props.get("entryPointName", String.class);
                if (StringUtils.isNotBlank(entryPointName)) {
                    final String entryPointId = WebwrappedAppConfig.getExplicitId(
                            entryPointName.toLowerCase(Locale.ENGLISH).replace(" ", "_"),
                            WebwrappedAppConfig.getExistingIds(currentNode, WebwrappedAppEntryPoint.ENTRY_POINT_ID), 1);
                    currentNode.setProperty(WebwrappedAppEntryPoint.ENTRY_POINT_ID, entryPointId);
                    currentNode.getSession().save();
                    currentNode.getSession().save();
                }
            } catch (RepositoryException e) {
                LOG.error("Could not save entryPointId property ", e);
            }
        }
    }

    /** The props. */
    private final EntryPointParam[] props;

    /** The res bundle. */
    private final ResourceBundle resBundle;

    /** The sub urls. */
    private final List<WebwrappedApp.EntryPoint.SubUrl> subUrls;

    /** The webwrapped app entry point name. */
    private final String webwrappedAppEntryPointName;

    /** The webwrapped app entry point url. */
    private final String webwrappedAppEntryPointUrl;

    /**
     * Instantiates a new webwrapped app entry point.
     * 
     */
    public WebwrappedAppEntryPoint() {
        final SlingHttpServletRequest slingRequest = getRequest();
        this.resBundle = slingRequest.getResourceBundle(slingRequest.getLocale());
        this.webwrappedAppEntryPointName = getProperties().get("entryPointName", String.class);
        this.webwrappedAppEntryPointUrl = getProperties().get("entryPointUrl", String.class);
        addEntryPointId(getProperties(), getCurrentNode());
        this.props = getPropItems();
        this.subUrls = getAllSubUrls();
    }

    /**
     * Gets the sub urls.
     * 
     * @return the all sub urls
     */
    private List<WebwrappedApp.EntryPoint.SubUrl> getAllSubUrls() {
        final List<WebwrappedApp.EntryPoint.SubUrl> list = new ArrayList<WebwrappedApp.EntryPoint.SubUrl>();
        for (int urlIndex = 1; urlIndex <= WebwrappedAppEntryPoint.MAX_SUB_URLS; urlIndex++) {
            final String crxUrlName = "subUrl" + urlIndex;
            final String url = getProperties().get(crxUrlName, String.class);

            if (StringUtils.isNotBlank(url)) {
                final Map<String, String> params = new HashMap<String, String>();
                for (int paramIndex = 1; paramIndex <= WebwrappedAppEntryPoint.MAX_PARAMS; paramIndex++) {
                    final String crxParamName = crxUrlName + "Param" + paramIndex + "Name";
                    final String crxParamValue = crxUrlName + "Param" + paramIndex + "Value";
                    final String name = getProperties().get(crxParamName, String.class);
                    final String value = getProperties().get(crxParamValue, String.class);
                    if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
                        params.put(name, value);
                    }
                }
                list.add(new WebwrappedApp.EntryPoint.SubUrl(url, null, null, params));
            }
        }
        return list;
    }

    /**
     * Gets the app entry point name string.
     * 
     * @return the i18n-ed name for the AppEntryPointName property.
     */
    public final String getAppEntryPointNameString() {
        return getI18nValue("webwrappedappentrypoint_name_label");
    }

    /**
     * Gets the entry point param mandatory string.
     * 
     * @return the i18n-ed name for the AppEntryPointName property.
     */
    public final String getEntryPointParamMandatoryString() {
        return getI18nValue("webwrappedappentrypoint_param_mandatory_label");
    }

    /**
     * Utility method to read a value from the resource bundle.
     * 
     * @param key
     *            the key to lookup a value in the resource bundle
     * @return the looked-up value or the key as a fallback, if no value can be found.
     */
    private String getI18nValue(final String key) {
        String value = "";
        try {
            value = this.resBundle.getString(key);
        } catch (MissingResourceException e) {
            LOG.info(e.getLocalizedMessage(), e);
            return key;
        }
        return value;
    }

    /**
     * get all parameters and add this to the prop list.
     * 
     * @return the list with all parameters
     */
    private EntryPointParam[] getPropItems() {
        final List<EntryPointParam> list = new ArrayList<EntryPointParam>();
        for (int paramIndex = 1; paramIndex <= WebwrappedAppEntryPoint.MAX_PARAMS; paramIndex++) {

            final String paramName = getProperties().get("entryPointParam" + paramIndex + "Name", String.class);
            if (StringUtils.isNotBlank(paramName)) {
                final EntryPointParam paramItem = new EntryPointParam();
                paramItem.paramName = paramName;
                paramItem.paramValue = getProperties().get("entryPointParam" + paramIndex + "Value", String.class);
                if ("true".equals(getProperties().get("entryPointParam" + paramIndex + "Mandatory", String.class))) {
                    paramItem.paramMandatory = true;
                }

                list.add(paramItem);
            }
        }
        return list.toArray(new EntryPointParam[list.size()]);
    }

    /**
     * Gets the props.
     * 
     * @return the props
     */
    public final EntryPointParam[] getProps() {
        if (this.props != null) {
            return this.props.clone();
        } else {
            return null;
        }
    }

    /**
     * Gets the props.
     * 
     * @return the props
     */
    public final List<WebwrappedApp.EntryPoint.SubUrl> getSubUrls() {
        return this.subUrls;
    }

    /**
     * Gets the webwrapped app entry point name.
     * 
     * @return the webwrapped app entry point name
     */
    public final String getWebwrappedAppEntryPointName() {
        return this.webwrappedAppEntryPointName;
    }

    /**
     * Gets the webwrapped app entry point url.
     * 
     * @return the webwrapped app entry point url
     */
    public final String getWebwrappedAppEntryPointUrl() {
        return this.webwrappedAppEntryPointUrl;
    }

    /**
     * Gets the webwrapped app entry point url string.
     * 
     * @return the i18n-ed name for the WebwrappedAppEntryPointUrl property.
     */
    public final String getWebwrappedAppEntryPointUrlString() {
        return getI18nValue("webwrappedappentrypoint_url_label");
    }

    /**
     * Checks for content.
     * 
     * @return true if the component has content.
     */
    public final boolean hasContent() {
        return (null != this.webwrappedAppEntryPointName && null != this.webwrappedAppEntryPointUrl);
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.components.AbstractComponent#init()
     */
    @Override
    public final void init() {
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.gmwp.aem.components.AbstractComponent#getResourceType()
     */
	@Override
    public String getResourceType() {
	    return null;
    }
}
