/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.ComponentContext;

import com.aditya.gmwp.aem.components.webwrapping.WebwrappingExternal;
import com.aditya.gmwp.aem.global.AEMTemplateInfo;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.LinkWriterService;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriter;
import com.aditya.gmwp.aem.services.core.link.writers.DefaultLinkWriter;
import com.aditya.gmwp.aem.services.core.link.writers.ExternalLinkWriter;
import com.aditya.gmwp.aem.services.core.link.writers.GlossaryLinkWriter;
import com.aditya.gmwp.aem.services.core.link.writers.Template16LinkWriter;
import com.aditya.gmwp.aem.services.core.link.writers.Template16wLinkWriter;
import com.aditya.gmwp.aem.services.core.link.writers.Template17cLinkWriter;
import com.aditya.gmwp.aem.services.core.link.writers.Template17dLinkWriter;
import com.aditya.gmwp.aem.services.core.link.writers.utils.LinkWriterUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(value = LinkWriterService.class)
@Component(immediate = true, name="com.aditya.gmwp.aem.services.core.LinkWriterService")
public class LinkWriterServiceImpl implements LinkWriterService {

    private LinkWriter defaultResolver;

    private final Map<AEMTemplateInfo, LinkWriter> plugins = new HashMap<AEMTemplateInfo, LinkWriter>();

    /**
     * Activates this component, called by SCR before registering as a service.
     * 
     * @param componentContext
     *            componentContext
     */
    @Activate
    protected final void activate() {

        // configuration of LinkWriterService
        // ==================================
        this.defaultResolver = new DefaultLinkWriter();
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T05, new Template16LinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T05b, new Template16LinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T06b, new Template16LinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T06c, new Template16LinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T10, new Template16LinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T16w, new Template16wLinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T16, new Template16LinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T17c, new Template17cLinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_T17d, new Template17dLinkWriter());
        this.plugins.put(AEMTemplateInfo.TEMPLATE_EXTERNAL_LINK, new ExternalLinkWriter());

    }

    /**
     * Resolve and return the matched LinkWriter.
     * 
     * @param page
     *            page object
     * @return the matched {@link LinkWriter}
     */
    protected LinkWriter getLinkRewriter(final Page page) {
        final LinkWriter plugin = this.plugins.get(AEMTemplateInfo.lookup(page));
        if (plugin != null) {
            return plugin;
        } else {
            return this.defaultResolver;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.linkwriter.LinkWriterService#rewriteLink
     * (org.apache.sling.api.SlingHttpServletRequest , com.gm.gssm.gmds.cq.model.LinkModel)
     */
    @Override
    public final HTMLLink rewriteLink(final SlingHttpServletRequest request,
                                      final LinkModel linkModel) {
        if (linkModel == null) {
            return new HTMLLink();
        }
        final String handle = LinkWriterUtil.buildHandle(request, linkModel);

        if (handle == null && linkModel.getExternalLinkModel() == null && linkModel.getGlossaryLink() == null) {
            return new HTMLLink();
        }

        LinkWriter plugin;
        if (handle == null) {
            if (linkModel.getExternalLinkModel() != null) {
                plugin = this.plugins.get(AEMTemplateInfo.TEMPLATE_EXTERNAL_LINK);
            } else {
                plugin = new GlossaryLinkWriter();
            }
        } else {
            final Page targetPage = LinkWriterUtil.retrieveTargetPage(request, linkModel);
            plugin = getLinkRewriter(targetPage);
        }

        final HTMLLink result = plugin.rewrite(request, linkModel);
        ensureLinkIsAbsoluteIfForExternalApplication(request, result);

        return result;
    }

    /**
     * Ensure link is absolute if for external application.
     * 
     * @param request
     *            the request
     * @param result
     *            the result
     */
    protected final void ensureLinkIsAbsoluteIfForExternalApplication(final SlingHttpServletRequest request,
                                                                      final HTMLLink result) {
        // if the link is rendered for an external application and is not
        // already absolute add protocoll and port to the
        // relativs link. The link is always http.
        if (Boolean.parseBoolean((String) request.getAttribute(WebwrappingExternal.FOR_EXTERNAL_APPLICATION_ATTRIBUTE)) && result.getHref() != null
                && !result.getHref().startsWith("http")) {
            result.setHref((String) request.getAttribute(WebwrappingExternal.HTTP_HOST_ATTRIBUTE) + result.getHref());
        }
    }
}
