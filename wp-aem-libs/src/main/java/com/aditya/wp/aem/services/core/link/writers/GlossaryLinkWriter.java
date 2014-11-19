/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.writers;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.aditya.gmwp.aem.global.AEMComponentInfo;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.utils.GlossaryUtil;
import com.aditya.gmwp.aem.utils.LinkUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class GlossaryLinkWriter extends DefaultLinkWriter {

	@Override
	public final HTMLLink rewrite(final SlingHttpServletRequest request,
	                              final LinkModel linkModel) {
		HTMLLink link = new HTMLLink();
		final Resource r = request.getResourceResolver().getResource(linkModel.getGlossaryLink());
		if (null != r && r.isResourceType(AEMComponentInfo.COMPONENT_CNT_GLOSSARY_ITEM.getComponentPath())) {
			final Page p = request.getResourceResolver().adaptTo(PageManager.class).getContainingPage(r);

			link = super.rewrite(request, getGlossaryHrefLink(linkModel, r, p.getPath()));

			link.setDataGlossaryCallback(LinkUtil.rewriteLink(getGlossarySnippletLink(r.getPath()), request).getHref());
		}

		return link;
	}

	/**
	 * Returns the glossary item href link model.
	 *
	 * @param original
	 *            the original link passed to the rewrite method to make copy of
	 * @param glossaryItemResource
	 *            the glossary item resource to get anchor from
	 * @param t14PagePath
	 *            the t14 page path
	 * @return link
	 */
	private LinkModel getGlossaryHrefLink(final LinkModel original,
	                                      final Resource glossaryItemResource,
	                                      final String t14PagePath) {
		final LinkModel l = LinkModel.newInstance(original);
		l.setInternalLink(t14PagePath);
		l.setAnchor(GlossaryUtil.getAnchorFromResource(glossaryItemResource));

		return l;
	}

	/**
	 * Returns the glossary item snipplet link model.
	 *
	 * @param glossaryItemPath
	 *            the glossary item path to append selector 'layer' which in fact triggers layer.jsp
	 *            of cnt_glossary_item_c1.
	 * @return link
	 */
	private LinkModel getGlossarySnippletLink(final String glossaryItemPath) {
		final LinkModel l = new LinkModel(null, glossaryItemPath);
		l.addSelector("layer");

		return l;
	}
}
