/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.link.aspects;

import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriterAspect;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ExternalLinkIconAspect implements LinkWriterAspect {
    private static final String NONE_ICON = "none";

    private final LinkModel linkModel;

    /**
     * Creates a new {@link ExternalLinkIconAspect}.
     * 
     * @param linkModel
     *            the link model. Must have an {@link ExternalLinkModel} present.
     */
    public ExternalLinkIconAspect(final LinkModel linkModel) {
        this.linkModel = linkModel;
    }

    @Override
    public void applyTo(final HTMLLink link) {
        link.setClazz(this.linkModel.getExternalLinkModel().getIcon());
    }

    @Override
    public boolean isApplicable() {
        final String icon = this.linkModel.getExternalLinkModel().getIcon();
        return StringUtils.isNotEmpty(icon) && !NONE_ICON.equals(icon);
    }
}
