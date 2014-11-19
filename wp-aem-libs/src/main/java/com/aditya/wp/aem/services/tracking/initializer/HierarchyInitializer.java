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
public class HierarchyInitializer extends TrackingVarInitializer {

    private final LevelService levelService;
    private final CompanyService companyService;

    public HierarchyInitializer(final LevelService levelService, final CompanyService companyService) {
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

        if (!isSubpageOfHomeLevel()) {
            return;
        }

        final int numberOfParents = getParentCount();
        final StringBuilder variableValue = new StringBuilder();
        printDebugMessage("TrackingModel() hierLevels: " + numberOfParents);

        try {
            for (int i = 0; i <= numberOfParents; i++) {
                appendPathElement(numberOfParents, variableValue, i);
            }
            printDebugMessage("TrackingModel() hier1 = " + variableValue.toString());
            setVariables(variableValue.toString(), OmnitureVariables.HIER1);

        } catch (ParseException pe) {
            getLog().error(
                    "The function containsElementNo must have the same parameters as the function"
                            + "getElementFromContentPath!");
        }
    }

    /**
     * @param numberOfSublevels
     * @param hier1
     * @param i
     * @return
     * @throws ParseException
     */
    private String appendPathElement(final int numberOfSublevels,
                                     final StringBuilder hier1,
                                     final int i) throws ParseException {

        String pathElement = this.companyService.getElementFromContentPath(getCurrentPage().getPath(),
                this.levelService.getHomeLevel() + i);

        hier1.append(pathElement);

        if (i < numberOfSublevels) {
            hier1.append(",");
        }
        return pathElement;
    }

    private int getParentCount() {
        return getCurrentPage().getDepth() - 1 - this.levelService.getHomeLevel();
    }

    private boolean isSubpageOfHomeLevel() {
        return getCurrentPage().getDepth() > this.levelService.getHomeLevel();
    }

}