/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HrefAssembler;
import com.aditya.gmwp.aem.services.core.link.InPageLinkDecider;
import com.aditya.gmwp.aem.services.core.link.aspects.utils.LinkWriterAspectUtil;
import com.aditya.gmwp.aem.utils.EncodeDecodeUtil;
import com.aditya.gmwp.aem.utils.PathUtil;
import com.aditya.gmwp.aem.utils.ProtocolUtil;
import com.aditya.gmwp.aem.utils.WCMModeUtil;
import com.aditya.gmwp.aem.utils.uri.UriBuilder;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class DefaultHrefAssembler implements HrefAssembler {

	private static final String HTML = "html";

	private String protocolAndHost(final Page targetPage,
	                               final SlingHttpServletRequest request) {
		if (WCMModeUtil.isAuthorInstance(request)) {
			return null;
		}

		String protocolAndHost = null;
		final boolean isHttps = ProtocolUtil.isHttpsRequest(request);
		final boolean targetPageRequiresHttps = ProtocolUtil.doesPageRequireHttps(targetPage);
		if (isHttps && !targetPageRequiresHttps) {
			protocolAndHost = ProtocolUtil.getHttpHostFromConfiguration(request);
		} else if (!isHttps && targetPageRequiresHttps) {
			protocolAndHost = ProtocolUtil.getHttpsHostFromConfiguration(request);
		}
		return protocolAndHost;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.gm.gssm.gmds.cq.services.linkwriter.impl.writer.aspect.IHrefAssembler#buildHref(org.apache
	 * .sling.api.SlingHttpServletRequest, com.gm.gssm.gmds.cq.model.LinkModel)
	 */
	@Override
	public String buildHref(final SlingHttpServletRequest request,
	                        final LinkModel linkModel) {
		Page targetPage = LinkWriterAspectUtil.getTargetPage(request, linkModel);
		final boolean useInPageLink = new InPageLinkDecider(linkModel, request).isDifferentRenderApplicable();
		final String mappedContentPathOrVanityPath = PathUtil.getRelativePublisherUrl(targetPage.getPath(), request);

		final UriBuilder hostUb = new UriBuilder(protocolAndHost(targetPage, request));
		return new UriBuilder(mappedContentPathOrVanityPath)	//
				.setScheme(hostUb.getScheme())	//
		        .setHost(hostUb.getHost())	//
		        .addSelector(EncodeDecodeUtil.urlEncode(linkModel.getSelectorListAsString()))	//
		        .addAllMultiParameters(linkModel.getParameters())	//
		        .setExtension(HTML)	//
		        .setAnchor(useInPageLink ? linkModel.getInPageLink() : linkModel.getAnchor()) //
		        .build();
	}
}