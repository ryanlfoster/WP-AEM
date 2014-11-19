/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.services.core.LinkWriterService;
import com.aditya.wp.aem.services.core.ServiceProvider;
import com.aditya.wp.aem.services.core.link.HTMLLink;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class LinkUtil {

    /**
     * Constructor.
     */
    private LinkUtil() {

    }

    /**
     * Rewrites a link (model) and returns it wrapped in a {@link HTMLLink}.
     * 
     * @param linkModel
     *            the link model
     * @param slingRequest
     *            the sling http request
     * @return {@link HTMLLink}
     */
    public static HTMLLink rewriteLink(final LinkModel linkModel,
                                       final SlingHttpServletRequest slingRequest) {
        return rewriteLink(linkModel, ServiceProvider.INSTANCE.fromSling(slingRequest).getLinkWriterService(),
                slingRequest);
    }

    /**
     * Rewrites a link (model) and returns it wrapped in a {@link HTMLLink}.
     * 
     * @param linkModel
     *            the link model
     * @param linkWriterService
     *            the link writer service
     * @param slingRequest
     *            slingRequest the sling http request
     * @return {@link HTMLLink}
     */
    public static HTMLLink rewriteLink(final LinkModel linkModel,
                                       final LinkWriterService linkWriterService,
                                       final SlingHttpServletRequest slingRequest) {
        return linkWriterService.rewriteLink(slingRequest, linkModel);
    }
}
