/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;

import com.aditya.gmwp.aem.components.webwrapping.WebwrappingExternal;
import com.aditya.gmwp.aem.global.GmdsRequestAttribute;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.model.OmnitureLinkTrackingData;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;
import com.aditya.gmwp.aem.services.tracking.OmnitureService;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.gmwp.aem.services.tracking.model.TrackingModel;
import com.aditya.gmwp.aem.utils.WCMModeUtil;
import com.aditya.gmwp.aem.utils.html.HtmlFilterType;
import com.aditya.gmwp.aem.utils.html.HtmlUtil;
import com.aditya.gmwp.aem.utils.tracking.LinkType;
import com.day.cq.wcm.api.PageManager;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public class ExternalLinkOmnitureTrackingAspect implements LinkWriterAspect {

	private final LinkModel linkModel;
	private final SlingHttpServletRequest request;
	private final OmnitureService omnitureService;

	/**
	 * Creates a new {@link ExternalLinkOmnitureTrackingAspect}.
	 * 
	 * @param request
	 *            used for checking whether Omniture tracking should be enabled.
	 * @param linkModel
	 *            used for checking whether link tracking data is already present.
	 */
	public ExternalLinkOmnitureTrackingAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
		this.request = request;
		this.linkModel = linkModel;
		final SlingBindings slingBindings = (SlingBindings) this.request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
		this.omnitureService = slingBindings.getSling().getService(OmnitureService.class);
	}

	@Override
	public void applyTo(final HTMLLink htmlLink) {
		htmlLink.addOnclick("getOmnitureLinktrackingCode('" + htmlLink.getHref() + "');");

		if (!this.linkModel.hasLinkTrackingData(OmnitureLinkTrackingData.class)) {
			// Add omniture tracking of prop27 to all external links.
			String pageArea = (String) GmdsRequestAttribute.CURRENT_PAGE_AREA.get(this.request);
			if (null == pageArea) {
				pageArea = (String) GmdsRequestAttribute.CURRENT_PAGE_MAIN_AREA.get(this.request);
			}

			//
			// GMDSSDS-58555: Request from analytics team.
			//
			if (StringUtils.equals(pageArea, "grid-5col")) {
				pageArea = "footer";
			}

			String linkName = StringUtils.EMPTY;
			if (StringUtils.isNotBlank(this.linkModel.getTitle())) {
				linkName = this.linkModel.getTitle();
				linkName = StringEscapeUtils.unescapeHtml(linkName);
				linkName = HtmlUtil.executeFiltering(linkName, HtmlFilterType.STRIP_ALL, false);
			} else if (StringUtils.isNotBlank(this.linkModel.getText())) {
				linkName = HtmlUtil.executeFiltering(this.linkModel.getText(), HtmlFilterType.STRIP_ALL, false);
			}
			final String prop32 = pageArea + ((StringUtils.isNotBlank(linkName)) ? ":" + linkName : StringUtils.EMPTY);

			final TrackingModel tm = (TrackingModel) GmdsRequestAttribute.TRACKING_MODEL.get(this.request);
			final Map<OmnitureVariables, String> props = new HashMap<OmnitureVariables, String>();
			if (tm != null) {
				props.put(OmnitureVariables.PAGENAME, tm.getPageName());
			}

			props.put(OmnitureVariables.PROP27, pageArea);
			props.put(OmnitureVariables.PROP32, prop32);

			this.linkModel.addLinkTrackingData(new OmnitureLinkTrackingData(null, LinkType.EXIT, props, null));
		}
	}

	@Override
	public boolean isApplicable() {
		return !WCMModeUtil.isAuthorMode(this.request)
		        && this.omnitureService != null
		        && this.omnitureService.isOmnitureEnabled(this.request.getResourceResolver().adaptTo(PageManager.class)
		                .getContainingPage(this.request.getResource()))
		        && !Boolean.parseBoolean((String) this.request.getAttribute(WebwrappingExternal.FOR_EXTERNAL_APPLICATION_ATTRIBUTE));
	}
}
