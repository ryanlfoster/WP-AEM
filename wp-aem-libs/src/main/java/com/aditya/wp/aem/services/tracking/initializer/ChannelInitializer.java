/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import java.text.ParseException;

import org.slf4j.Logger;

import com.aditya.wp.aem.services.config.CompanyService;
import com.aditya.wp.aem.services.config.LevelService;
import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ChannelInitializer extends TrackingVarInitializer {

    private final LevelService levelService;
    private final CompanyService companyService;

    public ChannelInitializer(final LevelService levelService, final CompanyService companyService) {
        this.levelService = levelService;
        this.companyService = companyService;
    }

    @Override
    protected void initialize() {

        final int channelLevel = this.levelService.getHomeLevel() + 1;

        if (isHomepage(getCurrentPage(), channelLevel)) {
            setVariables(getCurrentPage().getName(), OmnitureVariables.CHANNEL);
        }

        // set channel to path element
        if (this.companyService.containsElementNo(getCurrentPage().getPath(), channelLevel)) {
            try {
                final String channel = this.companyService.getElementFromContentPath(getCurrentPage().getPath(), channelLevel);
                setVariables(channel, OmnitureVariables.CHANNEL);
            } catch (ParseException pe) {
                // shouldn't happen unless containsElementNo() returns wrong values
                if (getLog().isErrorEnabled()) {
                    getLog().error("Parse Exception occurred while trying to read path element no. " + channelLevel + " from " + getCurrentPage().getPath() + ": ",
                            pe);
                }
            }
        }

    }

    /**
     * @param currentPage
     * @param channelLevel
     * @return
     */
    private boolean isHomepage(final Page currentPage, final int channelLevel) {
        return currentPage.getDepth() == channelLevel;
    }

}
