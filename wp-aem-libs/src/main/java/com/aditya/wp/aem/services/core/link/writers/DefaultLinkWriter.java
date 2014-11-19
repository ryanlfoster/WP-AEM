/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.writers;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.services.core.link.HTMLLink;
import com.aditya.wp.aem.services.core.link.InPageLinkDecider;
import com.aditya.wp.aem.services.core.link.LinkWriter;
import com.aditya.wp.aem.services.core.link.LinkWriterAspect;
import com.aditya.wp.aem.services.core.link.aspects.DisclaimerLinkAspect;
import com.aditya.wp.aem.services.core.link.aspects.HrefAssemblerAspect;
import com.aditya.wp.aem.services.core.link.aspects.InPageRelativeLinkAspect;
import com.aditya.wp.aem.services.core.link.aspects.LinkBehaviorAspect;
import com.aditya.wp.aem.services.core.link.aspects.OmnitureTrackingAspect;
import com.aditya.wp.aem.services.core.link.aspects.TitleIdLinkAspect;
import com.aditya.wp.aem.services.core.link.aspects.WindowTypeAspect;
import com.aditya.wp.aem.services.core.link.aspects.utils.LinkWriterAspectUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class DefaultLinkWriter implements LinkWriter {

	/**
	 * Rewrites an internal link (content path) to work on publisher. Among others, considers vanity
	 * path, omniture link tracking, and the window type of the target page.
	 *
	 * @param request
	 *            the request.
	 * @param linkModel
	 *            the link model, must not be null.
	 * @return the rewritten {@link HTMLLink}.
	 */
	@Override
	public HTMLLink rewrite(final SlingHttpServletRequest request,
	                        final LinkModel linkModel) {
		if (linkModel == null) {
			throw new IllegalArgumentException("linkModel must not be null");
		}

		boolean isInternalLink = linkModel.getInternalLink() != null;
		boolean isDifferentRenderApplicable = new InPageLinkDecider(linkModel, request).isDifferentRenderApplicable();
		boolean isResourceLink = ResourceLinkWriter.isResourceLink(LinkWriterAspectUtil.getInternalLink(request, linkModel));

		if ((isInternalLink || isDifferentRenderApplicable) && isResourceLink) {
			return ResourceLinkWriter.rewrite(request, linkModel);
		}

		final HTMLLink link = new HTMLLink();
		for (LinkWriterAspect aspect : getAspects(request, linkModel)) {
			if (aspect.isApplicable()) {
				aspect.applyTo(link);
			}
		}

		return link;
	}

	private List<LinkWriterAspect> getAspects(final SlingHttpServletRequest request,
			final LinkModel linkModel) {
		final List<LinkWriterAspect> potentialAspects = new ArrayList<LinkWriterAspect>();
		potentialAspects.add(new HrefAssemblerAspect(request, linkModel));
		potentialAspects.add(new WindowTypeAspect(request, linkModel));
		potentialAspects.add(new OmnitureTrackingAspect(request, linkModel));
		potentialAspects.add(new DisclaimerLinkAspect(request, linkModel));
		potentialAspects.add(new InPageRelativeLinkAspect(request, linkModel));
		potentialAspects.add(new TitleIdLinkAspect(linkModel));
		potentialAspects.add(new LinkBehaviorAspect(request, linkModel));
		return potentialAspects;
	}
}
