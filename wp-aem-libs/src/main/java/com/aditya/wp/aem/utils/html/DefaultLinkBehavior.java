/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.html;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.services.core.link.HTMLLink;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class DefaultLinkBehavior extends LinkBehavior {

	public DefaultLinkBehavior(final ValueMap config) {
		super(config);
	}

	@Override
	public void applyTo(final HTMLLink htmlLink,
	                    final LinkModel model,
	                    final SlingHttpServletRequest req) {
		// This does nothing right now
	}

}