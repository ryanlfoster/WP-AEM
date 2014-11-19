/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.model.LinkModel;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LinkWriter {

    /**
     * Write a link for the given link model.
     * 
     * @param request
     *            request object
     * @param linkModel
     *            linkModel object
     * @param targetPage
     *            the linked page
     * @return the {@link HTMLLink} object which represents the link
     */
    HTMLLink rewrite(SlingHttpServletRequest request, LinkModel linkModel);
}
