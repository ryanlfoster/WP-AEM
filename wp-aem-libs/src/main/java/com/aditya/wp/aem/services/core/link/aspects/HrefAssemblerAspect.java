/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.services.core.link.HTMLLink;
import com.aditya.wp.aem.services.core.link.HrefAssembler;
import com.aditya.wp.aem.services.core.link.InPageLinkDecider;
import com.aditya.wp.aem.services.core.link.LinkWriterAspect;
import com.aditya.wp.aem.services.core.link.aspects.utils.LinkWriterAspectUtil;
import com.aditya.wp.aem.services.core.link.writers.ResourceLinkWriter;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class HrefAssemblerAspect implements LinkWriterAspect {
	private final SlingHttpServletRequest request;
	private final LinkModel linkModel;

	/**
	 * Creates a new {@link HrefAssemblerAspect}.
	 *
	 * @param request
	 *            the current request. Used to get the Http(s)Host settings and to resolve resource
	 *            paths.
	 * @param linkModel
	 *            the {@link LinkModel} containing selectors and parameters for the link.
	 */
	public HrefAssemblerAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
		this.request = request;
		this.linkModel = linkModel;
	}

	@Override
	public void applyTo(final HTMLLink htmlLink) {
		HrefAssembler hrefAssembler = new DefaultHrefAssembler();
		htmlLink.setHref(hrefAssembler.buildHref(this.request, this.linkModel));
	}

	@Override
	public boolean isApplicable() {
		if ((this.linkModel.getInternalLink() == null && !new InPageLinkDecider(this.linkModel, this.request).isDifferentRenderApplicable())
				|| ResourceLinkWriter.isResourceLink(LinkWriterAspectUtil.getInternalLink(this.request, this.linkModel))) {
			return false;
		}
		return LinkWriterAspectUtil.getTargetPage(this.request, this.linkModel) != null;
	}
}
