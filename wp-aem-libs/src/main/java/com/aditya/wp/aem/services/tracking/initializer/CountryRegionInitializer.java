/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import java.text.ParseException;

import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.LevelService;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class CountryRegionInitializer extends TrackingVarInitializer {

    private final LevelService levelService;
    private final CompanyService companyService;

    public CountryRegionInitializer(final LevelService levelService, final CompanyService companyService) {
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

        try {
            String country = this.companyService.getElementFromContentPath(getCurrentPage().getPath(),
                    this.levelService.getCompanyLevel());
            setVariables(country, OmnitureVariables.EVAR31);
            String region = this.companyService.getElementFromContentPath(getCurrentPage().getPath(),
                    this.levelService.getRegionLevel());
            setVariables(region, OmnitureVariables.EVAR32);

        } catch (ParseException e) {
            getLog().warn("Parse exception while trying to read CompanyLevel from contentPath.");
        }
    }

}
