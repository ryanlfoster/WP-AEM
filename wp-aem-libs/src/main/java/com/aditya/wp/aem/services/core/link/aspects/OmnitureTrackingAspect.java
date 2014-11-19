/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.wp.aem.components.webwrapping.WebwrappingExternal;
import com.aditya.wp.aem.global.GmdsRequestAttribute;
import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.model.LinkTrackingData;
import com.aditya.wp.aem.model.OmnitureLinkTrackingData;
import com.aditya.wp.aem.services.core.ServiceProvider;
import com.aditya.wp.aem.services.core.link.HTMLLink;
import com.aditya.wp.aem.services.core.link.InPageLinkDecider;
import com.aditya.wp.aem.services.core.link.LinkWriterAspect;
import com.aditya.wp.aem.services.core.link.aspects.utils.LinkWriterAspectUtil;
import com.aditya.wp.aem.services.core.link.writers.ResourceLinkWriter;
import com.aditya.wp.aem.services.tracking.OmnitureService;
import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.wp.aem.services.tracking.model.TrackingModel;
import com.aditya.wp.aem.utils.PageUtil;
import com.aditya.wp.aem.utils.WCMModeUtil;
import com.aditya.wp.aem.utils.html.HtmlFilterType;
import com.aditya.wp.aem.utils.html.HtmlUtil;
import com.aditya.wp.aem.utils.tracking.LinkType;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class OmnitureTrackingAspect implements LinkWriterAspect {

	private final SlingHttpServletRequest request;
	private final Page targetPage;
	private final LinkModel linkModel;
	private static final Logger LOG = LoggerFactory.getLogger(OmnitureTrackingAspect.class);

	/**
	 * Creates a new {@link OmnitureTrackingAspect}.
	 * 
	 * @param request
	 *            the current request. Used for checking configuration values and determining the
	 *            page area.
	 * @param linkModel
	 *            the {@link LinkModel} containing Omniture tracking data.
	 */
	public OmnitureTrackingAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
		this.request = request;
		this.targetPage = LinkWriterAspectUtil.getTargetPage(request, linkModel);
		this.linkModel = linkModel;
	}

	@Override
	public void applyTo(final HTMLLink link) {
		final boolean forExternalApplication = Boolean.parseBoolean((String) this.request.getAttribute(WebwrappingExternal.FOR_EXTERNAL_APPLICATION_ATTRIBUTE));
		if (!forExternalApplication) {
			addOmnitureLinkTrackingData();
			new LinkTrackingAspect(this.request, this.linkModel).applyTo(link);
		}

	}

	/**
	 * Adds link tracking data to the link under certain circumstances. If:
	 * <ul>
	 * <li>The target page exists</li>
	 * <li>We are on preview/publish mode</li>
	 * <li>Omniture is enabled in general</li>
	 * <li>Full link tracking is enabled</li>
	 * <li>No other link tracking data for omniture is already present.</li>
	 * </ul>
	 * 
	 * @param linkModel
	 *            the link model
	 */
	private void addOmnitureLinkTrackingData() {
		if (WCMModeUtil.isAuthorMode(this.request)) {
			return;
		}

		final List<LinkTrackingData> datas = this.linkModel.getLinkTrackingData();
		if (datas.isEmpty()) {
			final OmnitureService os = ServiceProvider.INSTANCE.getService(OmnitureService.class);
			if (os != null && os.isOmnitureEnabled(this.targetPage) && os.isFullLinkTrackingEnabled(this.targetPage)
			        && !this.linkModel.hasLinkTrackingData(OmnitureLinkTrackingData.class)) {
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

				String linkName = PageUtil.getNavigationTitleFromPage(this.targetPage);
				if (StringUtils.isNotBlank(this.linkModel.getTitle())) {
					linkName = this.linkModel.getTitle();
				} else if (StringUtils.isNotBlank(this.linkModel.getText())) {
					linkName = this.linkModel.getText();
				}
				// In cases where the link name comes from the Title of a component such as in the
				// manual sitemap and the title contains a line break <br> this causes the title to
				// have a line break which creates a javascript error when put into something like
				// the linkName property. The if statement below strips any line breaks from the
				// linkname so that it does not have any line breaks. Fix for ticket GMDSSDS-58988.
				linkName = StringUtils.trimToEmpty(linkName);
				if (StringUtils.isNotBlank(linkName)) {
					if (linkName.contains("\n")) {
						LOG.info("OmnitureTrackingAspect: linkName contains linebreak so stripping");
						linkName = linkName.replaceAll("\n", " ").replaceAll("\r", " ");
					}
					if (linkName.contains("&amp;amp;")) {
						LOG.info("OmnitureTrackingAspect: linkName contains double amp so stripping");
						linkName = linkName.replaceAll("&amp;amp;", "&amp;");
					}
					if (linkName.contains("'")) {
						linkName = linkName.replaceAll("'", "");
					}
				}
				final String prop32 = pageArea + ":" + HtmlUtil.executeFiltering(linkName, HtmlFilterType.STRIP_ALL, false);

				final Map<OmnitureVariables, String> props = new HashMap<OmnitureVariables, String>();
				final TrackingModel tm = (TrackingModel) GmdsRequestAttribute.TRACKING_MODEL.get(this.request);
				if (tm != null) {
					props.put(OmnitureVariables.PAGENAME, tm.getPageName());
				}

				props.put(OmnitureVariables.PROP27, pageArea);
				props.put(OmnitureVariables.PROP32, prop32);

				this.linkModel.addLinkTrackingData(new OmnitureLinkTrackingData(null, LinkType.GENERAL, props, null));
			}
		}
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
