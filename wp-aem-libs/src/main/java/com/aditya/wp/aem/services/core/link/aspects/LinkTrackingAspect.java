/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.link.aspects;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.model.LinkTrackingData;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;
import com.aditya.gmwp.aem.utils.WCMModeUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class LinkTrackingAspect implements LinkWriterAspect {
	private final SlingHttpServletRequest request;
    private final LinkModel linkModel;

    /**
     * Creates a new {@link LinkTrackingAspect}.
     * 
     * @param request
     *            the request. Used for checking whether the CMS is in author mode.
     * @param linkModel
     *            the {@link LinkModel} containing the {@link LinkTrackingData}.
     */
    public LinkTrackingAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
        this.request = request;
        this.linkModel = linkModel;
    }

    @Override
    public void applyTo(final HTMLLink link) {
        for (LinkTrackingData ltd : this.linkModel.getLinkTrackingData()) {
            link.addOnclick(ltd.toJsCode());
        }
    }

    @Override
    public boolean isApplicable() {
        return !WCMModeUtil.isAuthorMode(this.request);
    }
}
