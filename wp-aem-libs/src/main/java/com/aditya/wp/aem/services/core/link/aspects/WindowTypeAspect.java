/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.link.aspects;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.InPageLinkDecider;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;
import com.aditya.gmwp.aem.services.core.link.aspects.utils.LinkWriterAspectUtil;
import com.aditya.gmwp.aem.services.core.link.aspects.utils.PopupJsBuilder;
import com.aditya.gmwp.aem.services.core.link.writers.ResourceLinkWriter;
import com.aditya.gmwp.aem.utils.WCMModeUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class WindowTypeAspect implements LinkWriterAspect {
	private final Page targetPage;
    private final SlingHttpServletRequest request;
    private final LinkModel linkModel;

    /**
     * Creates a new WindowTypeAspect for the passed properties of the target page.
     * 
     * @param request
     *            the sling request. Used for disabling in author mode
     * @param linkModel
     *            the link model for the link to be rewritten. Used for retrieving the target page.
     */
    public WindowTypeAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
        this.targetPage = LinkWriterAspectUtil.getTargetPage(request, linkModel);
        this.linkModel = linkModel;
        this.request = request;
    }

    /**
     * On the passed link, sets how the page should be opened. If in new window, sets the link
     * target - if as a popup, generates a JS onClick event.
     * 
     * @param htmlLink
     *            the link to be modified
     */
    @Override
    public void applyTo(final HTMLLink htmlLink) {
        if (WCMModeUtil.isAuthorMode(this.request) || this.targetPage == null) {
            return;
        }

        final ValueMap properties = this.targetPage.getProperties();
        final String windowType = properties.get("windowType", "sameWindow");
        if ("newWindow".equals(windowType)) {
            htmlLink.setTarget("_blank");
        } else if ("popup".equals(windowType)) {
            htmlLink.addOnclick(createPopupEvent(properties));
        }
    }

    /**
     * Creates the JS snippet that opens the popup.
     * 
     * @param properties
     *            the page properties from which to retrieve the popup settings (width, height...)
     * @return a JavaScript call to window.open() using the settings found.
     */
    private String createPopupEvent(final ValueMap properties) {
        final boolean enableScrollBars = !"true".equals(properties.get("disableScrollBars", ""));
        final boolean enableResizeable = !"true".equals(properties.get("disableResizeable", ""));
        return new PopupJsBuilder().width(properties.get("width", 0)).height(properties.get("height", 0)).scrollbars(enableScrollBars)
                .resizable(enableResizeable).build()
                + " return false;";
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
