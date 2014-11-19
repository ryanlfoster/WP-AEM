/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.html;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class NewWindowLinkBehavior extends LinkBehavior {

	public NewWindowLinkBehavior(final ValueMap config) {
		super(config);
	}

	@Override
	public void applyTo(final HTMLLink htmlLink,
	                    final LinkModel model,
	                    final SlingHttpServletRequest req) {
		htmlLink.setTarget("_blank");
	}

}