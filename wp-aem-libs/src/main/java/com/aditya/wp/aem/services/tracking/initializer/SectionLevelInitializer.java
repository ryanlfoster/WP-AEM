/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import java.text.ParseException;

import com.aditya.wp.aem.services.config.CompanyService;
import com.aditya.wp.aem.services.config.LevelService;
import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class SectionLevelInitializer extends TrackingVarInitializer {

    public static final OmnitureVariables[] SECTION_VARS = { OmnitureVariables.PROP10, OmnitureVariables.PROP11,
            OmnitureVariables.PROP12, OmnitureVariables.PROP13, OmnitureVariables.PROP14 };

    private final CompanyService companyService;
    private final LevelService levelService;

    public SectionLevelInitializer(final LevelService levelService, final CompanyService companyService) {
        this.levelService = levelService;
        this.companyService = companyService;
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.model.tracking.initializer.TrackingVarInitializer#initialize(com.day.cq.wcm.api.Page,
     * java.util.Map)
     */
    @Override
    protected void initialize() {
        for (int i = 0; i < SECTION_VARS.length; i++) {
            initSectionLevel(SECTION_VARS[i], this.levelService.getHomeLevel() + 1 + i);
        }
    }

    /**
     * inits a single site section level variable.
     * 
     * @param variable
     *            the variable to be set
     * @param level
     *            pagelevel to be used.
     */
    private void initSectionLevel(final OmnitureVariables variable,
                                  final int level) {
        if (!this.companyService.containsElementNo(getCurrentPage().getPath(), level)) {
            return;
        }

        try {

            final String value = this.companyService.getElementFromContentPath(getCurrentPage().getPath(), level);
            setVariables(value, variable);

        } catch (ParseException pe) {
            getLog().error(
                    "The function containsElementNo must have the same parameters as the function"
                            + "getElementFromContentPath!");
        }
    }
}