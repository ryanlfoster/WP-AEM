/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.link.writers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;

import com.aditya.gmwp.aem.components.webwrapping.WebwrappingExternal;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.writers.utils.LinkWriterUtil;
import com.aditya.gmwp.aem.services.webwrapping.WebwrappingService;
import com.aditya.gmwp.aem.utils.WCMModeUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class Template16wLinkWriter extends DefaultLinkWriter {

    private static final Map<String, String> ACCESS_LAYER_JS = new HashMap<String, String>();

    static {
        ACCESS_LAYER_JS.put("zipCodePopupForBYO", "return validateCookie(this, '${href}', 'byo', true);");
        ACCESS_LAYER_JS.put("zipCodePopupForDL", "return validateCookie(this, '${href}', 'dl', true);");
        ACCESS_LAYER_JS.put("zipCodePopupForVL", "return validateCookie(this, '${href}', 'vl', true);");
        ACCESS_LAYER_JS.put("zipCodePopupForCOMPVL", "return validateCookie(this, '${href}', 'compvl', true);");
    }

    /**
     * Creates the JavaScript code that has to be added to the onlick handler of the link if it
     * required an access layer.
     * 
     * @param targetPage
     *            the T16w page to which this link points
     * @param href
     *            the href to the T16w page.
     * @return JS code
     */
    private String getAccessLayerJs(final Page targetPage,
                                    final String href) {
        final String prop = targetPage.getProperties().get("access_layer_type", String.class);
        if (null != prop) {
            final String js = ACCESS_LAYER_JS.get(prop);
            if (null != js) {
                return js.replace("${href}", href);
            } else {
                // this is a hack that allows to delivery JS code that has been added into the CRX.
                // TODO: remove else-case!
                return prop.replace("${href}", href);
            }
        }
        return "";
    }

    /**
     * Checks if the access layer (for zipcode pop up) is needed. For external applications (NGDOE)
     * there should be no layer.
     * 
     * @param targetPage
     *            the T16w page that is targeted in this link
     * @param request
     *            the request
     * @return whether the T16w needs an access-layer on all links that point to the page.
     */
    private boolean needsAccessLayer(final Page targetPage,
                                     final ServletRequest request) {
        final boolean forExternalApplication = Boolean.parseBoolean((String) request.getAttribute(WebwrappingExternal.FOR_EXTERNAL_APPLICATION_ATTRIBUTE));
        final String prop = targetPage.getProperties().get("requires_access_layer", String.class);
        return "true".equals(prop) && !forExternalApplication;
    }

    // TODO (linkrefactor): besser im linkmodel entsprechnde werte bearbeiten und dann
    // super.rewrite(request,
    // linkModel, targetPage, link) aufrufen
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.linkwriter.impl.writer.DefaultLinkWriter#rewrite(org.apache.
     * sling.api. SlingHttpServletRequest, com.gm.gssm.gmds.cq.model.LinkModel,
     * com.day.cq.wcm.api.Page, com.gm.gssm.gmds.cq.services.linkwriter.HTMLLink)
     */
    @Override
    public final HTMLLink rewrite(final SlingHttpServletRequest request,
                                  final LinkModel linkModel) {
        // default link rewriting (if in author mode)
        final HTMLLink link = super.rewrite(request, linkModel);
        if (!WCMModeUtil.isAuthorMode(request)) {
            final Page targetPage = LinkWriterUtil.retrieveTargetPage(request, linkModel);
            // if in preview or publish mode do not use the internal link but diretly the redirect
            // link to the
            // application. Keep all request parameters, selectors and anchor
            final Resource res = targetPage.getContentResource().getResourceResolver().getResource(targetPage.getContentResource(), "webwrapping");
            if (res != null) {
                final SlingBindings slingBindings = (SlingBindings) request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
                final SlingScriptHelper slingScriptHelper = slingBindings.getSling();
                final WebwrappingService webwrappingService = slingScriptHelper.getService(WebwrappingService.class);
                final String entryPointHref = webwrappingService.buildEntryPointUrl(request, res, linkModel.getSelectorList(), linkModel.getParameters(),
                        linkModel.getAnchor());

                link.setHref(entryPointHref);

                if (needsAccessLayer(targetPage, request)) {
                    link.addOnclick(getAccessLayerJs(targetPage, link.getHref()));
                }
            }
        }

        return link;
    }
}
