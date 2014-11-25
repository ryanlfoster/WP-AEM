/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.writers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.wp.aem.components.webwrapping.WebwrappingExternal;
import com.aditya.wp.aem.global.AEMTemplateInfo;
import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.properties.BaseballcardBodystyleProperties;
import com.aditya.wp.aem.properties.BaseballcardCarlineProperties;
import com.aditya.wp.aem.properties.CompanyConfigProperties;
import com.aditya.wp.aem.services.config.CompanyService;
import com.aditya.wp.aem.services.core.ServiceProvider;
import com.aditya.wp.aem.services.core.link.HTMLLink;
import com.aditya.wp.aem.services.core.link.writers.utils.LinkWriterUtil;
import com.aditya.wp.aem.services.vehicledata.VehicleDataService;
import com.aditya.wp.aem.services.vehicledata.data.BodystyleBaseballcardData;
import com.aditya.wp.aem.utils.WCMModeUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class Template16LinkWriter extends DefaultLinkWriter {

    /**
     * Enumeration to hold information about the webclippings
     * 
     * @author aditya.vennelakanti, MRM Detroit
     * @since WP Release 4.0
     */
    private enum Applications {
        BYO("gmna-byo", "byo"),
        BTL("gmna-btl", "lnv"),
        VCO("gmna-current-offers", "vco"),
        CCC("gmna-competitive-compare", "ccc"),
        RAQ("gmna-sl", "raq", "requestQuoteSingleForm"),
        RTD("gmna-sl", "rtd", "requestTestdriveSingleForm"),
        IPE("gmna-ipe", "epm"),
        DL("gmna-dl", "lad");

        /** the application name */
        private final String appName;
        /** the application symbolic name */
        private final String appSymName;
        /** parameter value that differentiates between the same application name */
        private final String paramValue;

        /**
         * constructor
         * 
         * @param name
         *            the name of the application
         * @param symbolic
         *            the symbolic name associated with the application
         */
        private Applications(final String name, final String symbolic) {
            this.appName = name;
            this.appSymName = symbolic;
            this.paramValue = StringUtils.EMPTY;
        }

        /**
         * constructor
         * 
         * @param name
         *            the name of the application
         * @param symbolic
         *            the symbolic name associated with the application
         * @param paramValue
         *            the value of the x-requestType parameter that differentiates between raq & rtd
         */
        private Applications(final String name, final String symbolic, final String paramValue) {
            this.appName = name;
            this.appSymName = symbolic;
            this.paramValue = paramValue;
        }

        /**
         * Gets the symbolic name
         * 
         * @return the appSymName
         */
        public final String getApplicationSymbolicName() {
            return this.appSymName;
        }

        /**
         * Looks up the application enumeration given the application name
         * 
         * @param appName
         *            the application name for which the application to lookup
         * @return the application if found, null otherwise
         */
        public static Applications lookup(final String appName) {
            if (StringUtils.isBlank(appName)) {
                return null;
            }
            for (Applications app : values()) {
                if (StringUtils.isBlank(app.paramValue) && StringUtils.equals(app.appName, appName)) {
                    return app;
                }
            }
            return null;
        }

        /**
         * Looks up the application enumeration given the application name and the x-requestType
         * value
         * 
         * @param appName
         *            the application name
         * @param requestParamValue
         *            the request value
         * @return the application if found, null otherwise
         */
        public static Applications lookup(final String appName,
                                          final String requestParamValue) {
            if (StringUtils.isBlank(appName)) {
                return null;
            }
            for (Applications app : values()) {
                if (StringUtils.equals(app.paramValue, requestParamValue) && StringUtils.equals(app.appName, appName)) {
                    return app;
                }
            }
            return null;
        }
    }

    private static final Map<String, String> WEBCLIPPING_ACCESS_LAYER_JS = new HashMap<String, String>();

    static {
        WEBCLIPPING_ACCESS_LAYER_JS.put("zipCodePopupForBYO16", "return validateCookie(this, '${href}', 'byo16', true);");
        WEBCLIPPING_ACCESS_LAYER_JS.put("zipCodePopupForBTL", "return validateCookie(this, '${href}', 'btl', true);");
        WEBCLIPPING_ACCESS_LAYER_JS.put("zipCodePopupForDLC", "return validateCookie(this, '${href}', 'dlc', true);");
        WEBCLIPPING_ACCESS_LAYER_JS.put("zipCodePopupForBYO16state", "return validateCookie(this, '${href}', 'byo16-state', true);");
    }

    /**
     * Creates the JavaScript code that has to be added to the onlick handler of the link if it
     * required an access layer.
     * 
     * @param targetPage
     *            the T16 page to which this link points
     * @param href
     *            the href to the T16 page.
     * @return JS code
     */
    private String getWcAccessLayerOnclickJs(final Page targetPage,
                                             final String href) {
        final String prop = targetPage.getProperties().get("access_layer_type", String.class);
        if (null != prop) {
            final String js = WEBCLIPPING_ACCESS_LAYER_JS.get(prop);
            if (null != js) {
                return js.replace("${href}", href);
            } else {
                // this is a hack that allows to delivery JS code that has been added into the CRX.
                return prop.replace("${href}", href);
            }
        }
        return "";
    }

    /**
     * Checks if the access layer (for zipcode pop up) is needed. For external applications (NGDOE)
     * there should be no layer.
     * 
     * @param targetPage
     *            the T16w page that is targeted in this link
     * @param request
     *            the request
     * @return whether the T16w needs an access-layer on all links that point to the page.
     */
    private boolean needsWcAccessLayer(final Page targetPage,
                                       final ServletRequest request) {
        final boolean forExternalApplication = Boolean.parseBoolean((String) request.getAttribute(WebwrappingExternal.FOR_EXTERNAL_APPLICATION_ATTRIBUTE));
        final String layerRequired = targetPage.getProperties().get("requires_access_layer", String.class);
        return "true".equals(layerRequired) && !forExternalApplication;
    }

    /**
     * @param targetPage
     * @param request
     * @return
     */
    private boolean isPreferredVehicleSaved(final Page targetPage,
                                            final SlingHttpServletRequest request) {
        final CompanyService companyService = ServiceProvider.INSTANCE.fromSling(request).getCompanyService();
        if (companyService != null) {
            final String value = companyService.getConfigValue(targetPage, CompanyConfigProperties.SAVE_PREFERRED_VEHICLE);
            return BooleanUtils.toBoolean(value);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.linkwriter.impl.writer.DefaultLinkWriter#rewrite(org.apache.
     * sling.api. SlingHttpServletRequest, com.gm.gssm.gmds.cq.model.LinkModel,
     * com.day.cq.wcm.api.Page, com.gm.gssm.gmds.cq.services.linkwriter.HTMLLink)
     */
    @Override
    public final HTMLLink rewrite(final SlingHttpServletRequest request,
                                  final LinkModel linkModel) {
        final HTMLLink link = super.rewrite(request, linkModel);

        // if in author mode, do not do anything else.
        if (WCMModeUtil.isAuthorMode(request)) {
            return link;
        }

        final Page targetPage = LinkWriterUtil.retrieveTargetPage(request, linkModel);
        // if the link does not need access layer and the preferred vehicle configuration is not
        // set, do not do anything else.
        if (!needsWcAccessLayer(targetPage, request) && !isPreferredVehicleSaved(targetPage, request)) {
            return link;
        }

        // if preferred vehicle configuration is set, setup the link as a smart link
        if (isPreferredVehicleSaved(targetPage, request)) {
            setupSmartLink(request, targetPage, link);
        } else if (needsWcAccessLayer(targetPage, request)) { // else if the link just needs access
                                                              // layer, add the access layer js to
                                                              // onclick
            final String onclickJs = getWcAccessLayerOnclickJs(targetPage, link.getHref());
            link.addOnclick(onclickJs);
        }

        return link;
    }

    /**
     * sets up the smart link
     * 
     * @param request
     *            the {@link SlingHttpServletRequest request}
     * @param targetPage
     *            the target {@link Page page}
     * @param link
     *            the {@link HTMLLink link}
     */
    private void setupSmartLink(final SlingHttpServletRequest request,
                                final Page targetPage,
                                final HTMLLink link) {
        if (!AEMTemplateInfo.TEMPLATE_T16.matchesTemplate(targetPage)) {
            return;
        }

        if (needsWcAccessLayer(targetPage, request)) {
            link.setClazz("smart-link validate-cookie");
        } else {
            link.setClazz("smart-link");
        }
        final ValueMap props = targetPage.getProperties("webclipping");
        final String appId = props.get("webclipping_businessgadgetid", StringUtils.EMPTY);

        Applications app = Applications.lookup(appId);
        if (app != null) {
            link.setAppSymbolicName(app.getApplicationSymbolicName());
        }

        final String bbcRef = props.get("webclipping_baseballcard", StringUtils.EMPTY);
        if (StringUtils.isNotBlank(bbcRef)) {
            final VehicleDataService vds = ServiceProvider.INSTANCE.fromSling(request).getVehicleDataService();
            final BodystyleBaseballcardData bbcdata = vds.getBaseballcardData(bbcRef, request);
            if (bbcdata != null) {
                link.setCarline(bbcdata.getCarlineBaseballcardData().getBaseballcardProperty(BaseballcardCarlineProperties.CARLINE_CODE));
                link.setBodystyle(bbcdata.getBaseballcardProperty(BaseballcardBodystyleProperties.BODYSTYLE_CODE));
                link.setModelyear(String.valueOf(bbcdata.getModelYear()));
            }
        } else {
            final String startPath = props.get("webclipping_startpath", StringUtils.EMPTY);
            if (StringUtils.isNotBlank(startPath)) {
                setupSmartLinkWithStartPath(link, appId, app, startPath);
            }
        }
    }

    /**
     * sets up the smart link information from the startpath
     * 
     * @param link
     *            the {@link HTMLLink link}
     * @param appId
     *            the application id
     * @param app
     *            the {@link Applications app}
     * @param startPath
     *            the start path
     */
    private void setupSmartLinkWithStartPath(final HTMLLink link,
                                             final String appId,
                                             Applications app,
                                             final String startPath) {
        final Map<String, String> params = getParameters(startPath);
        if (params.containsKey("x-carline")) {
            link.setCarline(params.get("x-carline"));
        }
        if (params.containsKey("x-bodystyle")) {
            link.setBodystyle(params.get("x-bodystyle"));
        }
        if (params.containsKey("x-modelyear")) {
            link.setModelyear(params.get("x-modelyear"));
        } else if (params.containsKey("x-year")) {
            link.setModelyear(params.get("x-year"));
        } else if (params.containsKey("x-requestType")) {
            final String value = params.get("x-requestType");
            if (app == null) {
                app = Applications.lookup(appId, value);
                if (app != null) {
                    link.setAppSymbolicName(app.getApplicationSymbolicName());
                }
            }
        }
    }

    /**
     * gets a map of the request parameters and their values stored in the startpath of the
     * webclipping component
     * 
     * @param path
     *            the startpath configured in the webclipping component
     * @return a map of the parameters and their values
     */
    private Map<String, String> getParameters(final String path) {
        final Map<String, String> urlParams = new HashMap<String, String>();
        if (StringUtils.isBlank(path)) {
            return urlParams;
        }

        String queryPath;
        if (StringUtils.contains(path, '?')) {
            final String[] parts = StringUtils.split(path, '?');
            if (parts.length == 2) {
                queryPath = parts[1];
            } else {
                queryPath = path;
            }
        } else {
            queryPath = path;
        }

        final String[] parameters = StringUtils.split(queryPath, '&');
        if (parameters != null) {
            for (String param : parameters) {
                final int index = StringUtils.indexOf(param, '=');
                if (index != -1) {
                    final String key = StringUtils.substring(param, 0, index);
                    final String value = StringUtils.substring(param, index + 1);
                    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                        urlParams.put(key, value);
                    }
                }
            }
        }
        return urlParams;
    }
}
