/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.InPageLinkDecider;
import com.aditya.gmwp.aem.utils.PageUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class LinkWriterAspectUtil {

    private LinkWriterAspectUtil() {
        // private utility class constructor
    }

    /**
     * Uses an {@link InPageLinkDecider} to decide if this is an in page link, and returns an
     * internal link to be used.
     * 
     * @param request
     *            the request. Required for resolving references.
     * @param linkModel
     *            Model the link model. Used for getting the internal link.
     * @return the internal link to be used. May be null if the internal link from the link model
     *         was null.
     */
    public static String getInternalLink(final SlingHttpServletRequest request,
                                         final LinkModel linkModel) {
        if (useCurrentPageAsTarget(request, linkModel)) {
            return getTargetPage(request, request.getResource()).getPath();
        } else {
            return linkModel.getInternalLink();
        }
    }

    /**
     * Retrieves the page referenced in the {@link LinkModel}, following any redirects if allowed.
     * 
     * @param request
     *            the request. Required for resolving references.
     * @param linkModel
     *            the link model. Used for getting the internal link.
     * @return the found {@link Page}, or null if the reference is invalid.
     */
    public static Page getTargetPage(final SlingHttpServletRequest request,
                                     final LinkModel linkModel) {
        final Resource resource = request.getResourceResolver().resolve(getInternalLink(request, linkModel));
        if (null == resource) {
            return null;
        }

        if (!linkModel.isFollowRedirectAllowed()) {
            return getTargetPage(request, resource);
        } else {
            return PageUtil.getFinalTargetPage(getTargetPage(request, resource));
        }
    }

    private static Page getTargetPage(final SlingHttpServletRequest request,
                                      final Resource targetResource) {
        return request.getResourceResolver().adaptTo(PageManager.class).getContainingPage(targetResource);
    }

    private static boolean useCurrentPageAsTarget(final SlingHttpServletRequest request,
                                                  final LinkModel linkModel) {
        return new InPageLinkDecider(linkModel, request).isDifferentRenderApplicable();
    }
}
