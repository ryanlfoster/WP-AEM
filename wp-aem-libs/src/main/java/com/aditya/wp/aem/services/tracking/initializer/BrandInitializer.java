/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.initializer;

import java.text.ParseException;

import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.gmwp.aem.services.vehicledata.data.Brand;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class BrandInitializer extends TrackingVarInitializer {

    private final CompanyService companyService;

    public BrandInitializer(final CompanyService companyService) {
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
            final Brand brand = this.companyService.getBrandNameFromPath(getCurrentPage().getPath());
            if (brand != null) {
                setVariables(brand.getName(), OmnitureVariables.EVAR18, OmnitureVariables.PROP18);
            }
        } catch (ParseException e) {
            getLog().error("Unable to parse brand name from path: " + getCurrentPage().getPath(), e);
        }

    }
}
