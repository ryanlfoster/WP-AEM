/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.webwrapping.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;

import com.aditya.gmwp.aem.components.webwrapping.WebwrappedAppConfig;
import com.aditya.gmwp.aem.components.webwrapping.WebwrappedAppEntryPoint;
import com.aditya.gmwp.aem.services.core.AbstractService;
import com.aditya.gmwp.aem.services.core.JcrService;
import com.aditya.gmwp.aem.services.webwrapping.WebwrappedApp;
import com.aditya.gmwp.aem.services.webwrapping.WebwrappingService;
import com.aditya.gmwp.aem.utils.uri.UriBuilder;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(value = WebwrappingService.class)
@Component(metatype = true, name = "com.aditya.gmwp.aem.services.webwrapping.WebwrappingService", label="GMWP Webwrapping Service", description = "%webwrappingservice.description")
public class WebwrappingServiceImpl extends AbstractService<WebwrappingServiceImpl> implements WebwrappingService, EventListener {
    private static final String ENTRY_POINT_PROPERTY = "entryPointParam";

    @org.apache.felix.scr.annotations.Property(value = { "/etc/admin/webwrapping_config.html", "" }, cardinality = 1)
    private static final String CONFIGURATION_PAGES = "webwrappingservice.configurationpages";

    @Reference
    private transient final JcrService jcrService = null;

    private static final String CRX_URL_BASE_NAME = "subUrl";

    private static final int MAX_PARAMS = 10;

    private static final int MAX_SUB_URLS = 8;

    private static final String PATH_PLACEHOLDER = "${PATH}";

    private static final String QUERY_FOR_WEBWRAPPING_CONFIGS = "select * from [nt:base] " + //
            "where isdescendentnode('" + PATH_PLACEHOLDER + "/') " + //
            "and [sling:resourceType]='gmds/components/webwrapping/webwrappedappconfig'";

    private static final String QUERY_LANGUAGE_NAME = Query.JCR_SQL2; // this needs to be lower-case!

    private static final int RELEVANT_NODE_EVENTS = Event.NODE_ADDED | Event.NODE_REMOVED;
    private static final int RELEVANT_PROPERTY_EVENTS = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;

    private static final int RELEVANT_EVENT_TYPES = RELEVANT_NODE_EVENTS | RELEVANT_PROPERTY_EVENTS;

    private static final String WEBWRAPPING_APP_ENTRYPOINT_RESOURCE_TYPE = "gmds/components/webwrapping/webwrappedappentrypoint";

    public static final String WEBWRAPPING_APP_ID_PROPERTY = "webwrappingAppId";

    private static final String WEBWRAPPING_APP_RESOURCE_TYPE = "gmds/components/webwrapping/webwrappedappconfig";

    private static final Set<String> WEBWRAPPING_CONFIG_ITEM_RESOURCE_TYPES = new HashSet<String>();

    public static final String WEBWRAPPING_ENTRYPOINT_ID_PROPERTY = "webwrappingEntryPointId";

    static {
        WEBWRAPPING_CONFIG_ITEM_RESOURCE_TYPES.add(WEBWRAPPING_APP_RESOURCE_TYPE);
        WEBWRAPPING_CONFIG_ITEM_RESOURCE_TYPES.add(WEBWRAPPING_APP_ENTRYPOINT_RESOURCE_TYPE);
    }

    private List<String> configurationPaths = null;

    private List<WebwrappedApp> webWrappedApps = new ArrayList<WebwrappedApp>();

    @Activate
    protected final void activate(final Map<String, Object> config) {

        getLog(this).info("Webwrapping-service is being activated...");
        this.configurationPaths = getFilteredWebwrappingConfigPaths(config);
        reloadWebwrappingAppsConfigs();

        try {
            registerEventListener(this.configurationPaths);
        } catch (RepositoryException e) {
            getLog(this).error("Unable to register WebwrappingService for repository-events, "
                    + "changes in webwrapping-configuration will not be detected!. Cause: ", e);
        }

        getLog(this).info("Activation of Webwrapping-service done.");
    }
    
    @Deactivate
    protected final void deactivate() {
        getLog(this).info("Webwrapping-service is being deactivated...");
        synchronized (this) {
            this.webWrappedApps.clear();
        }

        try {
            this.jcrService.getAdminSession().getWorkspace().getObservationManager().removeEventListener(this);
        } catch (RepositoryException e) {
            getLog(this).warn("Unable to remove WebwrappingService-EventListener from obersevation manager. Cause: ", e);
        }
        getLog(this).info("Deactivation of Webwrapping-service done.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String buildEntryPointUrl(final HttpServletRequest request,
                                           final Resource res,
                                           final List<String> selectors,
                                           final Map<String, Set<String>> parameters,
                                           final String anchor) {
        String entryPointUrl;
        if (null == res) {
            // probably nothing has yet been configured on the page.
            entryPointUrl = "";
            return entryPointUrl;
        }
        final Node node = res.adaptTo(Node.class);
        try {
            final String appId = node.getProperty(WEBWRAPPING_APP_ID_PROPERTY).getString();
            final String entryPointId = node.getProperty(WEBWRAPPING_ENTRYPOINT_ID_PROPERTY).getString();

            final WebwrappedApp app = getWebWrappedApp(appId);
            if (null != app) {
                final WebwrappedApp.EntryPoint entryPoint = app.getEntryPoint(entryPointId);
                if (null != entryPoint) {
                    final UriBuilder ub = new UriBuilder(entryPoint.getEntryPointUrlPath());
                    ub.addAllSelectors(selectors);
                    final Map<String, String[]> parameterMap = entryPoint.getParameterMap();

                    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                        final String[] values = entry.getValue();
                        if (values != null) {
                            for (String value : values) {
                                ub.addParameter(entry.getKey(), value);
                            }
                        }
                    }
                    ub.addAllMultiParameters(parameters);
                    final Enumeration<?> enumr = request.getParameterNames();
                    while (enumr.hasMoreElements()) {
                        final String name = (String) enumr.nextElement();
                        final String[] values = request.getParameterValues(name);
                        if (null != values) {
                            for (String value : values) {
                                ub.addParameter(name, value);
                            }
                        }
                    }
                    ub.setAnchor(anchor);
                    return ub.build();
                }
            }
        } catch (RepositoryException e) {
            getLog(this).error("Cannot determine web-wrapping entry-point URL because of the following exception:", e);
            return "";
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public List<WebwrappedApp> getAllWebWrappedApps() {
		return this.webWrappedApps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final WebwrappedApp getWebWrappedApp(final String appId) {
        if (appId != null) {
            for (WebwrappedApp app : this.webWrappedApps) {
                if (appId.equals(app.getAppId())) {
                    return app;
                }
            }
        }
        return null;
    }

    /**
     * @param eventIter
     *            an iterator containing events.
     */
    @Override
    public final void onEvent(final EventIterator eventIter) {
        try {
            while (eventIter.hasNext()) {
                final Event event = eventIter.nextEvent();
                final String eventPath = event.getPath();
                if (eventPath.endsWith("sling:resourceType")) {
                    final Item item = this.jcrService.getAdminSession().getItem(eventPath);
                    if (item instanceof Property) {
                        final Value value = ((Property) item).getValue();
                        if (null != value && WEBWRAPPING_CONFIG_ITEM_RESOURCE_TYPES.contains((value.getString()))) {
                            getLog(this).info("---------------> Detected a modification in a webwrapping-config-item. Reloading configurations.");
                            reloadWebwrappingAppsConfigs();
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            getLog(this).error("Unable to process event. Cause: " + e);
        }
    }

    /**
     * @param apps
     *            all WebWrappedApp from the config template.
     */
    public final void setAllWebWrappedApps(final List<WebwrappedApp> apps) {
        this.webWrappedApps = apps;
    }

    /**
     * Helper method which loads all webwrapping-applications that are placed on the page with the given handle or on a
     * subpage of that. The newly created webwrapping-app-objects will not be initialized, this has to be done later!
     * 
     * @param resolver
     *            the Sling resource resolver
     * @param handle
     *            the handle of the page from which on webwrapping-configs shall be searched.
     * @param tmpAppList
     *            a list in which newly created webwrapping-app objects are collected.
     */
    private void loadWebwrappingAppsFromConfigPage(final ResourceResolver resolver,
                                                   final String handle,
                                                   final List<WebwrappedApp> tmpAppList) {
        // Get the page as a resource:
        final Resource page = resolver.resolve(handle);
        if (page == null || ResourceUtil.isNonExistingResource(page)) {
            getLog(this).warn("Configuration of webclipping service specified to read webclipping-configs from " + handle
                    + ", but this page does not exist!");
            return;
        }
        // Recursively search for webclipping-config-item components below the page:
        final String query = QUERY_FOR_WEBWRAPPING_CONFIGS.replace(PATH_PLACEHOLDER, page.getPath());
        Iterator<Resource> webwrappingConfigsIt = resolver.findResources(query, QUERY_LANGUAGE_NAME);
        final List<Resource> webwrappingConfigsList = new ArrayList<Resource>();
        while (webwrappingConfigsIt.hasNext()) {
            webwrappingConfigsList.add(webwrappingConfigsIt.next());
        }
        webwrappingConfigsIt = webwrappingConfigsList.iterator();
        // iterate over all webWrappedapps
        while (null != webwrappingConfigsIt && webwrappingConfigsIt.hasNext()) {
            final Resource webwrappingConfig = webwrappingConfigsIt.next();
            final ValueMap props = webwrappingConfig.adaptTo(ValueMap.class);
            final WebwrappedAppImpl webWrappedApp = new WebwrappedAppImpl();
            if (props.get("appId", String.class) == null) {
                WebwrappedAppConfig.addAppId(props, webwrappingConfig.adaptTo(Node.class));
            }
            webWrappedApp.setAppId(props.get("appId", String.class));
            webWrappedApp.setAppName(props.get("webWrappedAppName", String.class));
            final Resource res = resolver.getResource(webwrappingConfig.getPath() + "/WebwrappedAppConfigArea");
            if (res != null) {
                final List<WebwrappedApp.EntryPoint> entryPointList = new ArrayList<WebwrappedApp.EntryPoint>();
                // iterate over all entry points for this webWrappedapp
                final Iterator<Resource> webwrappedEntryPointConfigsIt = res.listChildren();
                while (null != webwrappedEntryPointConfigsIt && webwrappedEntryPointConfigsIt.hasNext()) {
                    final Resource webwrappingEntryPointConfig = webwrappedEntryPointConfigsIt.next();
                    final ValueMap entryPointProps = webwrappingEntryPointConfig.adaptTo(ValueMap.class);
                    final WebwrappedApp.EntryPoint entryPoint = new WebwrappedApp.EntryPoint();
                    if (entryPointProps.get("entryPointId") == null) {
                        WebwrappedAppEntryPoint.addEntryPointId(entryPointProps, webwrappingEntryPointConfig.adaptTo(Node.class));
                    }
                    entryPoint.setEntryPointId(entryPointProps.get("entryPointId", String.class));
                    entryPoint.setEntryPointName(entryPointProps.get("entryPointName", String.class));
                    entryPoint.setEntryPointUrlPath(entryPointProps.get("entryPointUrl", String.class));
                    entryPoint.setSubUrls(getAllSubUrls(entryPointProps));
                    entryPoint.setParameterNames(getPropItems(entryPointProps));
                    entryPointList.add(entryPoint);
                }
                webWrappedApp.setEntryPoints(entryPointList);
            }
            tmpAppList.add(webWrappedApp);
        }
    }

    /**
     * Registers this service as an event listener to repository events. Every time, a webwrapped application gets
     * edited, added or removed, the service will receive a notification an can then reload the
     * webwrapped-application-configs.
     * 
     * @param relevantPaths
     *            all the paths that are configured to contain webwrapped-application-configs an need to be watched.
     * @throws RepositoryException
     *             if an error occurs when registering the event listener.
     */
    private void registerEventListener(final List<String> relevantPaths) throws RepositoryException {
        final String[] nodeTypeNames = null;
        final ObservationManager observationManager = this.jcrService.getAdminSession().getWorkspace().getObservationManager();
        for (String path : relevantPaths) {
        	observationManager.addEventListener(this, RELEVANT_EVENT_TYPES, path, true, null, nodeTypeNames, false);
        }
    }

    /**
     * Loads all webwrapped-applications from all paths which have been configured.
     */
    private void reloadWebwrappingAppsConfigs() {
        // Load all configs and store the according webclipped-app-instances temporarily:
        final ResourceResolver resolver = this.jcrService.getResourceResolver();
        final List<WebwrappedApp> newApps = new ArrayList<WebwrappedApp>();
        for (String path : this.configurationPaths) {
            loadWebwrappingAppsFromConfigPage(resolver, path, newApps);
        }
        setAllWebWrappedApps(newApps);
    }

    /**
     * get all parameters and add this to the prop list.
     * 
     * @param props
     *            the map with all parameters
     * @return the list with all parameters
     */
    private List<WebwrappedApp.EntryPoint.EntryPointParamItem> getPropItems(final ValueMap props) {
        final List<WebwrappedApp.EntryPoint.EntryPointParamItem> list = new ArrayList<WebwrappedApp.EntryPoint.EntryPointParamItem>();
        for (int paramIndex = 1; paramIndex <= WebwrappingServiceImpl.MAX_PARAMS; paramIndex++) {
            if (props.get(ENTRY_POINT_PROPERTY + paramIndex + "Name") != null) {
                final WebwrappedApp.EntryPoint.EntryPointParamItem paramItem = new WebwrappedApp.EntryPoint.EntryPointParamItem();
                paramItem.setEntryPointParamName(props.get(ENTRY_POINT_PROPERTY + paramIndex + "Name", String.class));
                paramItem.setEntryPointParamValue(props.get(ENTRY_POINT_PROPERTY + paramIndex + "Value", String.class));
                if ("true".equals(props.get(ENTRY_POINT_PROPERTY + paramIndex + "Mandatory"))) {
                    paramItem.setEntryPointParamMandatory(true);
                }
                list.add(paramItem);
            }
        }
        return list;
    }

    /**
     * Returns a list of paths, where webwrapping-apps shall be loaded from. The list will not contain any sub-pages if
     * a related parent-page is present. The initial list of paths is loaded from the services config.
     * 
     * @param context
     *            the component context
     * @return see method description.
     */
    private List<String> getFilteredWebwrappingConfigPaths(final Map<String, Object> config) {
    	final String[] webwrappingConfigPages = PropertiesUtil.toStringArray(CONFIGURATION_PAGES, null);
        final List<String> relevantPaths = new ArrayList<String>();
        for (String cfgPage : webwrappingConfigPages) {
            final String cfgPagePath = trimPath(cfgPage);
            boolean skip = false;
            for (String testPage : webwrappingConfigPages) {
                final String testPagePath = trimPath(testPage);
                if (cfgPagePath.length() > testPagePath.length() && cfgPagePath.startsWith(testPagePath)) {
                    skip = true;
                    break;
                }
            }
            if (!skip) {
                relevantPaths.add(cfgPagePath);
            }
        }
        return relevantPaths;
    }
    
    /**
     * Gets the all sub urls.
     * 
     * @param entryPointProps
     *            the entry point props
     * @return the all sub urls
     */
    private List<WebwrappedApp.EntryPoint.SubUrl> getAllSubUrls(final ValueMap entryPointProps) {
        final List<WebwrappedApp.EntryPoint.SubUrl> list = new ArrayList<WebwrappedApp.EntryPoint.SubUrl>();
        for (int urlIndex = 1; urlIndex <= WebwrappingServiceImpl.MAX_SUB_URLS; urlIndex++) {
            final String crxUrlName = WebwrappingServiceImpl.CRX_URL_BASE_NAME + urlIndex;
            final String url = entryPointProps.get(crxUrlName, String.class);

            if (StringUtils.isNotBlank(url)) {
                final boolean separatorEnabled = Boolean.parseBoolean(entryPointProps.get(crxUrlName + "SeparatorEnable", String.class));
                String separator = null;
                String separatorParamName = null;
                if (separatorEnabled) {
                    separator = entryPointProps.get(crxUrlName + "Separator", String.class);
                    separatorParamName = entryPointProps.get(crxUrlName + "SeparatorParamName", String.class);
                }
                final Map<String, String> params = new HashMap<String, String>();
                for (int paramIndex = 1; paramIndex <= WebwrappingServiceImpl.MAX_PARAMS; paramIndex++) {
                    final String crxParamName = crxUrlName + "Param" + paramIndex + "Name";
                    final String crxParamValue = crxUrlName + "Param" + paramIndex + "Value";
                    final String name = entryPointProps.get(crxParamName, String.class);
                    final String value = entryPointProps.get(crxParamValue, String.class);
                    if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
                        params.put(name, value);
                    }
                }
                list.add(new WebwrappedApp.EntryPoint.SubUrl(url, separator, separatorParamName, params));
            }
        }
        return list;
    }

    /**
     * Simple method to remove file-extension and selectors from a given path to a resource.
     * 
     * @param path
     *            the path to be trimmed.
     * @return a trimmed path.
     */
    protected final String trimPath(final String path) {
        final int slashIndex = path.lastIndexOf('/');
        if (-1 != slashIndex) {
            final int dotIndex = path.indexOf('.', slashIndex);
            if (-1 != dotIndex) {
                return path.substring(0, dotIndex);
            }
        }
        return path;
    }
}
