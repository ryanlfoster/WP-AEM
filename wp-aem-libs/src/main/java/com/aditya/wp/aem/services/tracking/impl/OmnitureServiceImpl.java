/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.aditya.gmwp.aem.global.GmdsRequestAttribute;
import com.aditya.gmwp.aem.properties.CompanyConfigProperties;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.services.config.LevelService;
import com.aditya.gmwp.aem.services.core.AbstractService;
import com.aditya.gmwp.aem.services.tracking.OmnitureService;
import com.aditya.gmwp.aem.services.tracking.model.TrackingModel;
import com.aditya.gmwp.aem.services.tracking.util.PagePathAssembler;
import com.aditya.gmwp.aem.services.vehicledata.VehicleDataService;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(OmnitureService.class)
@Component(name="com.aditya.gmwp.aem.services.tracking.OmnitureService", label="GMWP Omniture Service", metatype = true)
public class OmnitureServiceImpl extends AbstractService<OmnitureServiceImpl> implements OmnitureService {

    private static final String S_CODE_PATH_SUFFIX = "omniture_config/s_code";

    @Reference
    private CompanyService companyService;
    @Reference
    private LanguageSLRService languageSlrService;
    @Reference
    private LevelService levelService;
    @Reference
    private VehicleDataService vehicleDataService;

    @Override
    public final TrackingModel getTrackingModel(final Page currentPage,
                                                final HttpServletRequest request) {
        TrackingModel trackingModel = (TrackingModel) GmdsRequestAttribute.TRACKING_MODEL.get(request);
        if (trackingModel == null) {
            trackingModel = new TrackingModel(currentPage, request, this.companyService, this.levelService, this.vehicleDataService, this.languageSlrService);
            // set into request so this is only created once per request
            GmdsRequestAttribute.TRACKING_MODEL.set(request, trackingModel);
        }
        return trackingModel;
    }

    @Override
    public final boolean isFullLinkTrackingEnabled(final Page page) {
        return this.companyService.getBooleanConfigValue(page, CompanyConfigProperties.OMNITURE_DO_FULL_LINKTAGGING, false);
    }

    @Override
    public final String getOmniturePageName(final Page page) {
        return new PagePathAssembler(page.getPath()).createOmniturePageName(this.languageSlrService.getPageLocaleFromPath(page));
    }

    @Override
    public final boolean isOmnitureEnabled(final Page page) {
        final String sAccount = this.companyService.getConfigValue(page, CompanyConfigProperties.OMNITURE_S_ACCOUNT);
        final Page companyPage = page.getAbsoluteParent(this.levelService.getCompanyLevel());

        return null != companyPage && null != companyPage.getContentResource(S_CODE_PATH_SUFFIX) && StringUtils.isNotEmpty(sAccount);
    }

    @Override
    public final boolean isEvar25And36And37Excluded(final Page page) {
        return isEnabled(page, CompanyConfigProperties.OMNITURE_EVARS_25_AND_36_AND_37_EXCLUDED);
    }

    @Override
    public final boolean isOmnitureMovieTrackingEnabled(final Page page) {
        return isEnabled(page, CompanyConfigProperties.OMNITURE_MOVIE_TRACKING);
    }

    @Override
    public final boolean isMultimediaParagraphTrackingEnabled(final Page page) {
        return isEnabled(page, CompanyConfigProperties.OMNITURE_MULTIMEDIA_PARAGRAPH_TRACKING);
    }

    /**
     * Returns whether omniture tracking feature is enabled.
     * 
     * @param page
     *            the page
     * @param property
     *            the company config property
     * @return enabled
     */
    private boolean isEnabled(final Page page,
                              final CompanyConfigProperties property) {
        return "true".equals(this.companyService.getConfigValue(page, property));
    }
}
