/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.initializer;

import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ConcatenationInitializer extends TrackingVarInitializer {

    private static final String SEPARATOR = " | ";

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.model.tracking.initializer.TrackingVarInitializer#initialize()
     */
    @Override
    protected void initialize() {
        final String model = getVariables().get(OmnitureVariables.PROP01).getValue();
        final String siteSection = getVariables().get(OmnitureVariables.PROP10).getValue();
        final String activeState = getVariables().get(OmnitureVariables.PROP14).getValue();

        if (StringUtils.isNotEmpty(siteSection)) {
            if (StringUtils.isNotEmpty(model)) {
                getVariables().get(OmnitureVariables.PROP33).setValue(model + SEPARATOR + siteSection);
            }
            if (StringUtils.isNotEmpty(activeState)) {
                getVariables().get(OmnitureVariables.PROP34).setValue(siteSection + SEPARATOR + activeState);
            }
            if (StringUtils.isNotEmpty(model) && StringUtils.isNotEmpty(activeState)) {
                getVariables().get(OmnitureVariables.PROP35).setValue(
                        model + SEPARATOR + siteSection + SEPARATOR + activeState);
            }
        }
    }

}