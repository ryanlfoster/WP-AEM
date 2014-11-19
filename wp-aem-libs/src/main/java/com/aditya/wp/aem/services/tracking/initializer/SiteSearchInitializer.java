/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.wp.aem.services.tracking.util.SiteSearchTrackingUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class SiteSearchInitializer extends TrackingVarInitializer {

    private static final String SITE_SEARCH_APP_ID = "gsa";

    private final Page currentPage;

    public SiteSearchInitializer(final Page currentPage) {
        this.currentPage = currentPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.model.tracking.initializer.TrackingVarInitializer#initialize()
     */
    @Override
    protected void initialize() {
        if (this.currentPage == null) {
            return;
        }

        final Resource pageContent = this.currentPage.getContentResource();
        if (pageContent == null) {
            return;
        }

        final Resource webClipingResource = pageContent.getChild("webclipping");
        if (webClipingResource != null) {
            final ValueMap properties = webClipingResource.adaptTo(ValueMap.class);
            final String gadgetId = properties.get("webclipping_businessgadgetid", String.class);
            if (!StringUtils.equals(gadgetId, SITE_SEARCH_APP_ID)) {
                setVariables("", OmnitureVariables.EVAR30, OmnitureVariables.PROP36, OmnitureVariables.EVENTS, OmnitureVariables.PROP47);
            } else {
                setVariables(SiteSearchTrackingUtil.getValue(OmnitureVariables.EVAR30), OmnitureVariables.EVAR30);
                setVariables(SiteSearchTrackingUtil.getValue(OmnitureVariables.PROP36), OmnitureVariables.PROP36);
                setVariables(SiteSearchTrackingUtil.getValue(OmnitureVariables.PROP47), OmnitureVariables.PROP47);
                setVariables(SiteSearchTrackingUtil.getValue(OmnitureVariables.EVENTS), OmnitureVariables.EVENTS);
            }
        }
    }
}