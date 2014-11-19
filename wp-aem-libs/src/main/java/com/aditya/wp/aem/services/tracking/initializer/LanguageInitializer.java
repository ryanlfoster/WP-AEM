/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import java.util.Locale;

import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class LanguageInitializer extends TrackingVarInitializer {

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.model.tracking.initializer.TrackingVarInitializer#initialize(com.day.cq.wcm.api.Page,
     * java.util.Map)
     */
    @Override
    protected void initialize() {
        final Locale locale = getCurrentPage().getLanguage(false);
        if (locale != null) {
            setVariables(locale.getLanguage(), OmnitureVariables.EVAR04, OmnitureVariables.PROP23);
        }
    }
}
