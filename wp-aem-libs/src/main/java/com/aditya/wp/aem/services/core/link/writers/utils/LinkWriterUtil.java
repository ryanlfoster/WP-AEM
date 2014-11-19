/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.link.writers.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.utils.StringUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class LinkWriterUtil {

    private LinkWriterUtil() {
        // private utility class constructor
    }

    /**
     * Successively checks the different properties of the passed {@link LinkModel} and
     * {@link SlingHttpServletRequest} objects for a reference that can be resolved to a target
     * page. Will not follow redirects.
     * 
     * @param request
     *            Used for retrieving a resolvable resource handle, for example in the case of in
     *            page links.
     * @param linkModel
     *            Must not be null.
     * @return a possible resource handle if one is found, or null if not.
     */
    public static String buildHandle(final SlingHttpServletRequest request,
                                     final LinkModel linkModel) {
        String handle = linkModel.getInternalLink();
        if (handle == null) {
            handle = linkModel.getDisclaimerLink();
            if (handle == null) {
                handle = linkModel.getInPageLink();
                if (handle == null) {
                    handle = linkModel.getExternalLink();
                } else if (StringUtil.startsWith(handle, '#')) {
                    handle = request.getRequestURI();
                } else {
                    // special treatment for page-based layers,
                    // since the in-page-link contains the path to the page layer
                    // and not to the t17c
                    final Resource pageLayer = request.getResourceResolver().resolve(handle);
                    if (!(pageLayer instanceof NonExistingResource)) {
                        final ValueMap properties = ResourceUtil.getValueMap(pageLayer);
                        // link to a t17c to only! get the properties from
                        handle = properties.get("internalLink", String.class);
                    }
                }
            }
        }
        if (handle != null) {
            int index = handle.indexOf(".html?");
            if (index != -1) {
                handle = handle.substring(0, index);
            } else {
                index = handle.indexOf(".html#");
                if (index != -1) {
                    handle = handle.substring(0, index);
                } else if (handle.endsWith(".html")) {
                    handle = handle.substring(0, handle.length() - ".html".length());
                }
            }
        }
        return handle;
    }

    /**
     * Retrieves a target page, following the logic of {@link LinkWriterUtil.buildHandle()}.
     * 
     * @param request
     *            Used for retrieving a resolvable resource handle, for example in the case of in
     *            page links.
     * @param linkModel
     *            Must not be null.
     * @return the target page if a valid handle is found, or null if not.
     */
    public static Page retrieveTargetPage(final SlingHttpServletRequest request,
                                          final LinkModel linkModel) {
        final String handle = buildHandle(request, linkModel);
        if (handle == null) {
            return null;
        }
        return request.getResourceResolver().resolve(handle).adaptTo(Page.class);
    }
}
