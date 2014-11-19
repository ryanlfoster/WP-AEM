/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class CountryCodeInitializer extends TrackingVarInitializer {

    private final Locale locale;

    public CountryCodeInitializer(final Locale locale) {
        this.locale = locale;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.model.tracking.initializer.TrackingVarInitializer#initialize(com.day.
     * cq.wcm.api.Page, java.util.Map)
     */
    @Override
    protected void initialize() {

        if (this.locale != null) {
            String countryCode = "gmwp_" + StringUtils.lowerCase(this.locale.getCountry());
            setVariables(countryCode, OmnitureVariables.EVAR17, OmnitureVariables.PROP17);
        }
    }
}