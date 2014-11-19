/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.model.DisclaimerModel;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.InPageLinkDecider;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class DisclaimerLinkAspect implements LinkWriterAspect {
	private final LinkModel linkModel;
	private final SlingHttpServletRequest request;

	/**
	 * Creates a new {@link DisclaimerLinkAspect}.
	 *
	 * @param request
	 *            the current request. Used for adding the new disclaimer ID. May be null.
	 * @param linkModel
	 *            the {@link LinkModel} used for retrieving the disclaimer link. May not be null.
	 */
	public DisclaimerLinkAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
		this.request = request;
		this.linkModel = linkModel;
	}

	@Override
	public void applyTo(final HTMLLink link) {
		link.setClazz("ln disclaimer_1");
		link.setHref("#" + DisclaimerModel.escapeDisclaimerID(this.linkModel.getDisclaimerLink()));
		if (this.request != null) {
			DisclaimerModel.addReferencedDisclaimerId(this.request, this.linkModel.getDisclaimerLink());
		}
	}

	@Override
	public boolean isApplicable() {
		return this.linkModel.getInternalLink() == null && !new InPageLinkDecider(this.linkModel, this.request).isDifferentRenderApplicable()
				&& StringUtils.isNotBlank(this.linkModel.getDisclaimerLink());
	}
}
