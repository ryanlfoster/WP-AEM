/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.services.core.link.HTMLLink;
import com.aditya.wp.aem.services.core.link.LinkWriterAspect;
import com.aditya.wp.aem.utils.html.LinkBehavior;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class LinkBehaviorAspect implements LinkWriterAspect {

	private final LinkModel linkModel;
	private final SlingHttpServletRequest request;

	public LinkBehaviorAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
		this.request = request;
		this.linkModel = linkModel;
	}

	@Override
	public void applyTo(final HTMLLink htmlLink) {
		final LinkBehavior behavior = this.linkModel.getBehavior();
		behavior.applyTo(htmlLink, this.linkModel, this.request);
	}

	@Override
	public boolean isApplicable() {
		return this.linkModel.getBehavior() != null;
	}
}