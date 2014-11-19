/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.link.aspects;

import com.aditya.gmwp.aem.model.ExternalLinkModel;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class RelLinkAspect implements LinkWriterAspect {

    private final LinkModel linkModel;

    /**
     * Creates a new {@link RelLinkAspect}.
     * 
     * @param linkModel
     *            the {@link LinkModel}. Used for retrieving rel
     */
    public RelLinkAspect(final LinkModel linkModel) {
        this.linkModel = linkModel;
    }

    @Override
    public void applyTo(final HTMLLink link) {
        final ExternalLinkModel externalLink = this.linkModel.getExternalLinkModel();
        if (externalLink.getRel() != null) {
            link.setRel(externalLink.getRel());
        }
    }

    @Override
    public boolean isApplicable() {
        return true;
    }
}