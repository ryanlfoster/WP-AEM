/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link;

import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.aditya.wp.aem.global.AEMComponentInfo;
import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.utils.StringUtil;
import com.day.cq.commons.jcr.JcrConstants;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class InPageLinkDecider {

    /** whether in page link is different render applicable. */
    private boolean isDifferentRenderApplicable;

    /**
     * Enum holding applicable resources that use layer functionality.
     */
    private enum ApplicableResource {
        NON_EXISTING("gmds/components/non_existing", "non_existing_indicator"), //
        CNT_GLOSSARY_ITEM_C1(AEMComponentInfo.COMPONENT_CNT_GLOSSARY_ITEM.getComponentPath(), "layer"), //
        T14_B("gmds/pages/t14b_extended_glossary", "detail");

        /** the resource type. */
        private final String resourceType;

        /** the indicator, mainly a fixed selector. */
        private final String indicator;

        /**
         * Constructor.
         * 
         * @param resourceType
         *            the resource type
         * @param indicator
         *            the indicator
         */
        private ApplicableResource(final String resourceType, final String indicator) {
            this.resourceType = resourceType;
            this.indicator = indicator;
        }

        /**
         * Returns an applicable resource by passed resource.
         * 
         * @param resource
         *            the resource
         * @return applicable resource
         */
        static ApplicableResource fromResource(final Resource resource) {
            ApplicableResource applicable = NON_EXISTING;
            for (ApplicableResource r : values()) {
                if (null != resource) {
                    final Resource jcr = resource.getChild(JcrConstants.JCR_CONTENT);
                    if (resource.isResourceType(r.resourceType) || (null != jcr && jcr.isResourceType(r.resourceType))) {
                        applicable = r;
                        break;
                    }
                }
            }

            return applicable;
        }
    }

    /**
     * Constructor.
     * 
     * @param linkModel
     *            the link model
     * @param request
     *            the sling http servlet request
     */
    public InPageLinkDecider(final LinkModel linkModel, final SlingHttpServletRequest request) {
        determineDifferentInPageLinkRendering(linkModel, request);
    }

    /**
     * Determines different in page link rendering.
     * 
     * @param linkModel
     *            the link model
     * @param request
     *            the sling http servlet request
     */
    private void determineDifferentInPageLinkRendering(final LinkModel linkModel,
                                                       final SlingHttpServletRequest request) {
        if (StringUtil.startsWith(linkModel.getInPageLink(), '#')) {
            final ApplicableResource r = ApplicableResource.fromResource(request.getResourceResolver().resolve(request.getRequestURI()));
            if (ArrayUtils.contains(request.getRequestPathInfo().getSelectors(), r.indicator)) {
                this.isDifferentRenderApplicable = true;
            }
        }
    }

    /**
     * Returns whether in page link is different render applicable, e.g. when part of a layer or different page view.
     * 
     * @return is different render applicable
     */
    public final boolean isDifferentRenderApplicable() {
        return this.isDifferentRenderApplicable;
    }
}
