/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.writers;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.model.ExternalLinkModel;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriter;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.EdxTaggingAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.ExternalHrefAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.ExternalLinkIconAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.ExternalLinkOmnitureTrackingAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.LinkBehaviorAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.LinkTrackingAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.RelLinkAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.TitleIdLinkAspect;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ExternalLinkWriter implements LinkWriter {

	/*
	 * (non-Javadoc)
	 * @see
	 * com.gm.gssm.gmds.cq.services.linkwriter.impl.writer.LinkWriter#rewrite(org.apache.sling.api
	 * .SlingHttpServletRequest , com.gm.gssm.gmds.cq.model.LinkModel, com.day.cq.wcm.api.Page,
	 * com.gm.gssm.gmds.cq.services.linkwriter.HTMLLink)
	 */
	@Override
	public final HTMLLink rewrite(final SlingHttpServletRequest request,
	                              final LinkModel linkModel) {
		final HTMLLink link = new HTMLLink();
		final ExternalLinkModel extLink = linkModel.getExternalLinkModel();

		// fixes http 500 when external link is set as internal
		if (null == extLink) {
			return link;
		}

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
		potentialAspects.add(new ExternalHrefAspect(request, linkModel)); 	// insert this prior to
		                                                                  // any others
		                                                                  // - following aspects may
																		  // depend
		                                                                  // on the href being set
		potentialAspects.add(new TitleIdLinkAspect(linkModel));
		potentialAspects.add(new ExternalLinkIconAspect(linkModel));
		potentialAspects.add(new ExternalLinkOmnitureTrackingAspect(request, linkModel));
		potentialAspects.add(new EdxTaggingAspect(request, linkModel));
		potentialAspects.add(new LinkTrackingAspect(request, linkModel));
		potentialAspects.add(new RelLinkAspect(linkModel));
		potentialAspects.add(new LinkBehaviorAspect(request, linkModel));
		return potentialAspects;
	}
}
