/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;

import com.aditya.wp.aem.components.webwrapping.WebwrappingExternal;
import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.properties.CompanyConfigProperties;
import com.aditya.wp.aem.services.config.CompanyService;
import com.aditya.wp.aem.services.core.link.HTMLLink;
import com.aditya.wp.aem.services.core.link.LinkWriterAspect;
import com.aditya.wp.aem.utils.WCMModeUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class EdxTaggingAspect implements LinkWriterAspect {

    private final SlingHttpServletRequest request;
    private final LinkModel linkModel;

    /**
     * Creates a new {@link EdxTaggingAspect}.
     * 
     * @param request
     *            the current request. Used for retrieving required services.
     * @param linkModel
     *            the {@link LinkModel} to be used in modifying the {@link HTMLLink}. The
     *            ExternalLinkModel must be non-null for this parameter.
     */
    public EdxTaggingAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
        this.request = request;
        this.linkModel = linkModel;
    }

    @Override
    public void applyTo(final HTMLLink link) {
        link.addOnclick("tc_log('" + this.linkModel.getExternalLinkModel().getPath() + "');");
    }

    @Override
    public boolean isApplicable() {
        final SlingBindings slingBindings = (SlingBindings) this.request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
        final CompanyService compServ = slingBindings.getSling().getService(CompanyService.class);
        final PageManager pageManager = this.request.getResourceResolver().adaptTo(PageManager.class);
        final Page containingPage = pageManager.getContainingPage(this.request.getResource());

        final boolean forExternalApplication = Boolean.parseBoolean((String) this.request.getAttribute(WebwrappingExternal.FOR_EXTERNAL_APPLICATION_ATTRIBUTE));
        final boolean edxEnabledGlobally = "true".equals(compServ.getConfigValue(containingPage, CompanyConfigProperties.EDX_ENABLED));
        final boolean edxEnabledForLink = this.linkModel.getExternalLinkModel().isEdxEnable();

        return !WCMModeUtil.isAuthorMode(this.request) && edxEnabledForLink && edxEnabledGlobally && !forExternalApplication;
    }
}
