/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.InPageLinkDecider;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;
import com.aditya.gmwp.aem.utils.StringUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class InPageRelativeLinkAspect implements LinkWriterAspect {

	private final LinkModel linkModel;
    private final SlingHttpServletRequest request;

    /**
     * Creates a new {@link InPageRelativeLinkAspect}.
     * 
     * @param request
     *            the request. Used for checking whether another {@link LinkWriterAspect} can be
     *            used
     * @param linkModel
     *            the {@link LinkModel}. Used for checking whether another {@link LinkWriterAspect}
     *            can be used, and for retrieving the in-page link
     */
    public InPageRelativeLinkAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
        this.request = request;
        this.linkModel = linkModel;
    }

    @Override
    public boolean isApplicable() {
        return this.linkModel.getInternalLink() == null && !new InPageLinkDecider(this.linkModel, this.request).isDifferentRenderApplicable()
                && StringUtils.isBlank(this.linkModel.getDisclaimerLink()) && StringUtil.startsWith(this.linkModel.getInPageLink(), '#');
    }

    @Override
    public void applyTo(final HTMLLink link) {
        link.setHref(this.linkModel.getInPageLink());
    }
}
