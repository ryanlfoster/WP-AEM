/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import org.apache.commons.lang.StringUtils;

import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class EditableVarsInitializer extends TrackingVarInitializer {

    public static final OmnitureVariables[] EDITABLE_VARS = { OmnitureVariables.EVAR01, OmnitureVariables.EVAR02,
            OmnitureVariables.EVAR05, OmnitureVariables.EVAR06, OmnitureVariables.EVAR07, OmnitureVariables.EVAR16,
            OmnitureVariables.EVAR24, OmnitureVariables.EVAR27, OmnitureVariables.EVAR28, OmnitureVariables.EVAR34,
            OmnitureVariables.EVAR38, OmnitureVariables.PROP01, OmnitureVariables.PROP02, OmnitureVariables.PROP03,
            OmnitureVariables.PROP09, OmnitureVariables.PROP15, OmnitureVariables.PROP16, OmnitureVariables.PROP17,
            OmnitureVariables.PROP30, OmnitureVariables.PROP31, OmnitureVariables.PROP33, OmnitureVariables.PROP34,
            OmnitureVariables.PROP35, };

    /**
     * @param currentPage
     */
    @Override
    protected void initialize() {
        for (OmnitureVariables editableVariable : EDITABLE_VARS) {
            String pagePropsValue = getCurrentPage().getProperties().get(editableVariable.getJavaScriptVariableName(),
                    String.class);
            if (StringUtils.isNotEmpty(pagePropsValue)) {
                setVariables(pagePropsValue, editableVariable);
            }
        }

    }
}