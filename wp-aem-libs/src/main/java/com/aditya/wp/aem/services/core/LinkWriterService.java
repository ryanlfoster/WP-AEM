/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LinkWriterService {
    /**
     * This method write a {@link LinkModel} in a {@link HTMLLink}.
     * 
     * @param request
     *            the request object
     * @param linkModel
     *            the linkModel
     * @return a html link model
     */
    HTMLLink rewriteLink(final SlingHttpServletRequest request, final LinkModel linkModel);
}
