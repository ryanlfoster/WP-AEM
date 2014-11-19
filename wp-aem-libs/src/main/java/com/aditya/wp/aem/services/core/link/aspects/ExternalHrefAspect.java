/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;

import com.aditya.gmwp.aem.components.webwrapping.WebwrappingExternal;
import com.aditya.gmwp.aem.model.ExternalLinkModel;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;
import com.aditya.gmwp.aem.services.tracking.OmnitureService;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.gmwp.aem.utils.WCMModeUtil;
import com.aditya.gmwp.aem.utils.uri.UriBuilder;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ExternalHrefAspect implements LinkWriterAspect {

	private final LinkModel linkModel;
    private final SlingHttpServletRequest request;

    /**
     * Creates a new {@link ExternalHrefAspect}.
     * 
     * @param request
     *            the request. Used for service access.
     * @param linkModel
     *            the link model containing the {@link ExternalLinkModel} with the path to be set as
     *            a href. The contained {@link ExternalLinkModel} must not be null.
     */
    public ExternalHrefAspect(final SlingHttpServletRequest request, final LinkModel linkModel) {
        this.request = request;
        this.linkModel = linkModel;
    }

    @Override
    public void applyTo(final HTMLLink link) {
        link.setHref(addOmnitureParameter(this.request, this.linkModel.getExternalLinkModel()));
    }

    @Override
    public boolean isApplicable() {
        return StringUtils.isNotEmpty(this.linkModel.getExternalLinkModel().getPath());
    }

    private String addOmnitureParameter(final SlingHttpServletRequest request,
                                        final ExternalLinkModel extLink) {
        final UriBuilder uri = new UriBuilder(extLink.getPath());
        if (!WCMModeUtil.isAuthorMode(request) && isOmnitureEnabled(request)
                && !Boolean.parseBoolean((String) request.getAttribute(WebwrappingExternal.FOR_EXTERNAL_APPLICATION_ATTRIBUTE))
                && "true".equals(extLink.geteVarParam()) && StringUtils.isNotEmpty(extLink.geteVarValue())) {
            uri.addParameter(OmnitureVariables.EVAR25.getJavaScriptVariableName().toLowerCase(Locale.ENGLISH), extLink.geteVarValue());
        }
        return uri.build();
    }

    private boolean isOmnitureEnabled(final SlingHttpServletRequest request) {
    	final SlingBindings slingBindings = (SlingBindings) request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
        final OmnitureService omnitureService = slingBindings.getSling().getService(OmnitureService.class);
        return omnitureService != null
                && omnitureService.isOmnitureEnabled(request.getResourceResolver().adaptTo(PageManager.class).getContainingPage(request.getResource()));
    }
}
