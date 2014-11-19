/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.model.LinkModel;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface HrefAssembler {

	/**
	 * Creates the correct href-value for the given combination of request and linkmodel
	 *
	 * @param request
	 *            the current request. Used to get the Http(s)Host settings and to resolve resource
	 *            paths.
	 * @param linkModel
	 *            the {@link LinkModel} containing selectors and parameters for the link.
	 * @return href for usage in a {@link HTMLLink}
	 */
	public abstract String buildHref(SlingHttpServletRequest request, LinkModel linkModel);
}
