/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class TitleIdLinkAspect implements LinkWriterAspect {

    private final LinkModel linkModel;

    /**
     * Creates a new {@link TitleIdLinkAspect}.
     * 
     * @param linkModel
     *            the {@link LinkModel}. Used for retrieving Title and ID
     */
    public TitleIdLinkAspect(final LinkModel linkModel) {
        this.linkModel = linkModel;
    }

    @Override
    public void applyTo(final HTMLLink link) {
        // cla 2014.05.15 Reverting to previous revision before 25199
        // Changes made for 25199 were a duplicate fix of the same issue in two different
        // places. GMDSSDS-58988 and GMDSSDS-58888 were the same issue and got two fixes
        // from two people in different (but valid) places. Taking out changes here
        // which were from GMDSSDS-58888 incorporating them into GMDSSDS-58988 changes
        // in the OmnitureTrackingAspect.java addOmnitureLinkTrackingData method.
        // This is necessary because the fix made here for GMDSSDS-58888 caused GMDSST-53358
        // which turned all non-latin characters - ie cyrillic/thai/etc to not show up
        // on title mouseover and unicode to show up instead.
        link.setTitle(StringEscapeUtils.unescapeHtml(this.linkModel.getTitle()), true);
        link.setId(StringUtils.replace(this.linkModel.getId(), " ", "_"));
    }

    @Override
    public boolean isApplicable() {
        return true;
    }
}
