/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import com.aditya.wp.aem.services.config.LevelService;
import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class VehicleTypeInitializer extends TrackingVarInitializer {

    private static final int REQUIRED_PARENT_OFFSET = 3;
    public static final String REQUIRED_TEMPLATE_NAME = "t03b_view_all_vehicles_fallback";
    public static final String REQUIRED_TEMPLATE_PATH = "/apps/gmds/templates/" + REQUIRED_TEMPLATE_NAME;

    private static final String UNKNOWN_RESOURCE_MESSAGE = "TrackingModel() try to set prop3 and eVar7 failed, because"
            + "the template of the page couldn't be identified or the resource wasn't found. "
            + "Possibly the resource was a Folder Template.";
    private static final String WRONG_TEMPLATE_MESSAGE = "TrackingModel() try to set prop3 and eVar7 failed, because page is not "
            + REQUIRED_TEMPLATE_NAME + ".";
    private final LevelService levelService;

    /**
     * Main constructor.
     * 
     * @param levelService
     *            a valid {@link LevelService}
     */
    public VehicleTypeInitializer(final LevelService levelService) {
        this.levelService = levelService;
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.model.tracking.initializer.TrackingVarInitializer#initialize(com.day.cq.wcm.api.Page,
     * java.util.Map)
     */
    @Override
    protected void initialize() {

        if (!isPossibleSubpageOfCorrectTemplate()) {
            return;
        }

        final Page templateCandidate = isDirectSubpageOfRequiredLevel() ? getCurrentPage() : getCurrentPage()
                .getAbsoluteParent(getRequiredParentLevel());

        if (null == templateCandidate) {
            printDebugMessage("TrackingModel() try to set prop3 and eVar7 failed, because page is null.");
            return;
        }

        checkTemplateAndSetVariables(templateCandidate);
    }

    /**
     * @param templateCandidate
     */
    private void checkTemplateAndSetVariables(final Page templateCandidate) {
        final Template template = templateCandidate.getTemplate();

        if (null == template) {
            printDebugMessage(UNKNOWN_RESOURCE_MESSAGE);
            return;
        }
        if (!isCorrectTemplate(template)) {
            printDebugMessage(WRONG_TEMPLATE_MESSAGE);
            return;
        }

        setVariables(templateCandidate.getName(), OmnitureVariables.PROP03, OmnitureVariables.EVAR07);
    }

    /**
     * @return
     */
    private int getRequiredParentLevel() {
        return this.levelService.getHomeLevel() + REQUIRED_PARENT_OFFSET;
    }

    /**
     * @param template
     * @return
     */
    private boolean isCorrectTemplate(final Template template) {
        return REQUIRED_TEMPLATE_PATH.equals(template.getPath());
    }

    /**
     * @return
     */
    private boolean isDirectSubpageOfRequiredLevel() {
        return getCurrentPage().getDepth() == getRequiredParentLevel() + 1;
    }

    /**
     * @return
     */
    private boolean isPossibleSubpageOfCorrectTemplate() {
        return getCurrentPage().getDepth() > getRequiredParentLevel();
    }
}