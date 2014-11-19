/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.webwrapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.Resource;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface WebwrappingService {
    /**
     * Builds the URL of the entry point by reading web-wrapping config from the current page.
     * 
     * @param request
     *            the request
     * @param res
     *            the resource of the web-wrapping page
     * @param selectors
     *            the selectors
     * @param parameters
     *            the parameters in addition to the configured webwrapping parameters
     * @param anchor
     *            the anchor
     * @return the URL to the entry point.
     */
    String buildEntryPointUrl(final HttpServletRequest request,
                              final Resource res,
                              final List<String> selectors,
                              final Map<String, Set<String>> parameters,
                              final String anchor);

    /**
     * @return a list of WebWrappedApp objects, where each one contains the configuraion of one app.
     */
    List<WebwrappedApp> getAllWebWrappedApps();

    /**
     * @param appId
     *            the application id.
     * @return the WebWrappedApp object which holds the configuration for the app with the given ID.
     */
    WebwrappedApp getWebWrappedApp(String appId);
}
