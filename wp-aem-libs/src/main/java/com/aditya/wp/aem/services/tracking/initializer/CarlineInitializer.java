/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.Resource;

import com.aditya.gmwp.aem.global.AEMTemplateInfo;
import com.aditya.gmwp.aem.properties.BaseballcardBodystyleProperties;
import com.aditya.gmwp.aem.properties.BaseballcardCarlineProperties;
import com.aditya.gmwp.aem.services.config.LevelService;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.gmwp.aem.services.vehicledata.VehicleDataService;
import com.aditya.gmwp.aem.services.vehicledata.data.Bodystyle;
import com.aditya.gmwp.aem.services.vehicledata.data.BodystyleBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.Carline;
import com.aditya.gmwp.aem.services.vehicledata.data.VehicleData;
import com.aditya.gmwp.aem.utils.PageUtil;
import com.aditya.gmwp.aem.wrapper.DeepResolvingResourceUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class CarlineInitializer extends TrackingVarInitializer {

    private static final int CARLINE_LEVEL_OFFSET = 5;

    private static final int DEFAULT_PAGE_HIERARCHY_SEARCH_DEPTH = 5;

    private final LevelService levelService;
    private final VehicleDataService vehicleDataService;
    private final HttpServletRequest request;

    public CarlineInitializer(final LevelService levelService, final VehicleDataService vehicleDataService,
            final HttpServletRequest request) {
        this.levelService = levelService;
        this.vehicleDataService = vehicleDataService;
        this.request = request;
    }

    @Override
    protected void initialize() {
        if (getCurrentPage().getDepth() < this.levelService.getHomeLevel() + CARLINE_LEVEL_OFFSET) {
            return;
        }
        getLog().debug("initEVar5() try to set Model and Bodystyle, because level could be right.");

        // t04a has baseballcard carline and could not have parallel pages, excepting folders with
        // redirects
        if (isT04aTemplate()) {
            initializeFromT04aTemplate();
        } else {
            initializeFromT06Template();
        }
    }

    /**
     * @throws NumberFormatException
     */
    private void initializeFromT04aTemplate() {
        // get BaseballCard Information
        getLog().debug("initializeFromT04aTemplate() t04a_vehicleinformation found.");

        String carlineCode = getCarlineCode();
        if (null == carlineCode) {
            return;
        }
        int modelYear = Carline.INVALID_MODEL_YEAR;
        final String[] parts = carlineCode.split("~");
        carlineCode = parts[0];
        if (parts.length > 1) {
            modelYear = Integer.parseInt(parts[1]);
            getVariables().get(OmnitureVariables.EVAR28).setValue(modelYear + "");
        }
        String suffix = null;
        if (parts.length > 2) {
            suffix = parts[2];
        }

        final Carline carline = getCarline(carlineCode, modelYear, suffix);
        if (carline == null) {
            return;
        }

        setVariables(carline.getTitle(), OmnitureVariables.PROP01, OmnitureVariables.EVAR05, OmnitureVariables.EVAR27);
    }

    /**
     * @param carlineCode
     * @param modelYear
     * @param suffix
     * @return
     */
    protected Carline getCarline(final String carlineCode,
                                 final int modelYear,
                                 final String suffix) {
        return this.vehicleDataService.getVehicleData(getCurrentPage(), this.request).getCarline(carlineCode,
                modelYear, suffix);
    }

    /**
     * @return
     */
    protected String getCarlineCode() {
        String carlineCode;
        Resource bbcResource = getBaseballCardResource();
        if (bbcResource == null) {
            return null;
        }
        getLog().debug("initEVar5() bbcResource {}.", bbcResource.getPath());

        carlineCode = DeepResolvingResourceUtil.getValueMap(bbcResource).get(
                BaseballcardCarlineProperties.CARLINE_CODE.getPropertyName(), String.class);
        return carlineCode;
    }

    /**
     * @return
     */
    private Resource getBaseballCardResource() {
        Resource bbcResource;
        Page baseballCardPage = getBaseballCardPage();
        if (baseballCardPage == null) {
            return null;
        }
        getLog().debug("initEVar5() baseballCardPage {}.", baseballCardPage.getPath());

        bbcResource = baseballCardPage.getContentResource("baseballcard_carline");
        return bbcResource;
    }

    /**
     * @return
     */
    private Page getBaseballCardPage() {
        Page baseballCardPage;
        final String baseballCardLink = getCurrentPage().getProperties().get("baseballcardCarlineLink", String.class);
        final PageManager pageManager = getCurrentPage().getContentResource().getResourceResolver().adaptTo(PageManager.class);
        if (baseballCardLink == null || pageManager == null) {
            return null;
        }
        getLog().debug("initEVar5() baseballCardLink {}.", baseballCardLink);
        baseballCardPage = pageManager.getPage(baseballCardLink);
        return baseballCardPage;
    }

    /**
     * 
     */
    private void initializeFromT06Template() {
        // search for T06 when calling a page underneath of Content Level 4 (e.g. passenger_cars)
        final Page modelOverviewPage = findRelatedModelOverviewPage(getCurrentPage());
        if (null == modelOverviewPage) {
            return;
        }
        final String baseballCardLink = PageUtil.getPropertyFromPageIncludingAncestors(modelOverviewPage,
                "baseballCardLink");
        if (baseballCardLink == null) {
            return;
        }

        getLog().debug("initEVar5() baseballCardLink: {}", baseballCardLink);
        final BodystyleBaseballcardData bcData = this.vehicleDataService.getBaseballcardData(baseballCardLink, this.request);
        if (bcData == null) {
            return;
        }
        getLog().debug("initEVar5() BodystyleBaseballcardData: {}", bcData);
        final VehicleData vehicleData = this.vehicleDataService.getVehicleData(getCurrentPage(), this.request);
        final String carlineCode = bcData.getBaseballcardProperty(BaseballcardCarlineProperties.CARLINE_CODE);
        final int modelYear = bcData.getModelYear();
        if (Carline.INVALID_MODEL_YEAR != modelYear) {
            getVariables().get(OmnitureVariables.EVAR28).setValue(modelYear + "");
        }
        final String suffix = bcData.getBaseballcardProperty(BaseballcardCarlineProperties.MODEL_YEAR_SUFFIX);
        final Carline carline = vehicleData.getCarline(carlineCode, modelYear, suffix);
        if (carline == null) {
            return;
        }
        // set Model
        getVariables().get(OmnitureVariables.PROP01).setValue(carline.getTitle());
        getVariables().get(OmnitureVariables.EVAR05).setValue(carline.getTitle());

        // set Bodystyle
        final Bodystyle bodystyle = carline.getBodystyle(bcData.getBaseballcardProperty(BaseballcardBodystyleProperties.BODYSTYLE_CODE));
        if (bodystyle != null) {
            getVariables().get(OmnitureVariables.PROP02).setValue(bodystyle.getTitle());
            getVariables().get(OmnitureVariables.EVAR06).setValue(bodystyle.getTitle());
        }
    }

    /**
     * @return
     */
    protected boolean isT04aTemplate() {
        return "/apps/gmds/templates/t04a_vehicleinformation".equals(getCurrentPage().getTemplate().getPath());
    }

    /**
     * Tries to find a related model-overview page.
     * 
     * @param currentPage
     *            the current page.
     * @return a model-overview page or null.
     */
    private Page findRelatedModelOverviewPage(final Page currentPage) {
        Page lookUpwardsPage = currentPage;
        for (int i = 0; i < DEFAULT_PAGE_HIERARCHY_SEARCH_DEPTH; i++) {
            if (AEMTemplateInfo.TEMPLATE_T03b.matchesTemplate(lookUpwardsPage)
                    || AEMTemplateInfo.TEMPLATE_T04a.matchesTemplate(lookUpwardsPage)) {
                return this.vehicleDataService.getRelatedModelOverviewPage(currentPage);
            } else {
                lookUpwardsPage = lookUpwardsPage.getParent();
                if (null == lookUpwardsPage) {
                    break;
                }
            }
        }
        return null;
    }

}