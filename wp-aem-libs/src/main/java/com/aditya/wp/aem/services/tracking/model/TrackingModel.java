/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.properties.CompanyConfigProperties;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.services.config.LevelService;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariableContainer;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.gmwp.aem.services.tracking.data.TrackingData;
import com.aditya.gmwp.aem.services.tracking.initializer.BrandInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.CarlineInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.ChannelInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.ConcatenationInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.CountryCodeInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.CountryRegionInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.EditableVarsInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.HierarchyInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.LanguageInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.PageNameInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.SectionLevelInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.SiteSearchInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.TrackingVarInitializer;
import com.aditya.gmwp.aem.services.tracking.initializer.VehicleTypeInitializer;
import com.aditya.gmwp.aem.services.tracking.util.PagePathAssembler;
import com.aditya.gmwp.aem.services.tracking.util.TrackingListJsonGenerator;
import com.aditya.gmwp.aem.services.tracking.util.TrackingUtil;
import com.aditya.gmwp.aem.services.vehicledata.VehicleDataService;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class TrackingModel {

    private static final String DEFAULT_SEO_PARAMETER_NAME = "seo";

    private static final Logger LOG = LoggerFactory.getLogger(TrackingModel.class);

    private static final String S_CODE_PATH_SUFFIX = "omniture_config/s_code";

    private static final String TRACKING_XML_PROP_NAME = "trackingxml";

    private CompanyService companyService;

    private Page currentPage;

    private LevelService levelService;

    private HttpServletRequest request;

    /** the s_account id. */
    private String sAccount;

    /** the path to the sCode file. */
    private String sCodePath;

    private SortedMap<OmnitureVariables, OmnitureVariableContainer> variables;

    private VehicleDataService vehicleDataService;

    private Locale lslrLocale;

    /**
     * Constructor for an empty TrackingModel. Use setters to fill this Object.
     */
    public TrackingModel() {
    }

    /**
     * Construct a new tracking model.
     * 
     * @param currentPage
     *            the current page
     * @param request
     *            the request
     * @param companyService
     *            the companyService
     * @param levelService
     *            the levelService
     * @param vehicleDataService
     *            the vehicleDataService
     * @param languageSlrService
     *            the languageSlrService
     */
    public TrackingModel(final Page currentPage, final HttpServletRequest request, final CompanyService companyService, final LevelService levelService,
            final VehicleDataService vehicleDataService, final LanguageSLRService languageSlrService) {
        if (currentPage == null || request == null) {
            throw new IllegalArgumentException("current page and request may not be null.");
        }
        this.levelService = levelService;
        this.companyService = companyService;
        this.vehicleDataService = vehicleDataService;
        this.currentPage = currentPage;
        this.request = request;

        this.sAccount = companyService.getConfigValue(currentPage, CompanyConfigProperties.OMNITURE_S_ACCOUNT);

        setSCodePath();

        this.lslrLocale = languageSlrService.getPageLocaleFromPath(this.currentPage);
        this.variables = TrackingUtil.initializeVariableMap(OmnitureVariables.values());

        List<TrackingVarInitializer> variableInitializers = createVariableInitializers();
        for (TrackingVarInitializer initializer : variableInitializers) {
            initializer.initializeVariables(this.currentPage, this.variables);

        }
    }

    /**
     * 
     */
    private void setSCodePath() {
        final Page companyPage = this.currentPage.getAbsoluteParent(this.levelService.getCompanyLevel());
        if (companyPage != null && companyPage.getContentResource(S_CODE_PATH_SUFFIX) != null) {
            this.sCodePath = companyPage.getPath() + ".s_code.js";
        }
    }

    /**
     * @return
     */
    private List<TrackingVarInitializer> createVariableInitializers() {
        List<TrackingVarInitializer> variableInitializers = new LinkedList<TrackingVarInitializer>();
        variableInitializers.add(new PageNameInitializer(this.lslrLocale));
        variableInitializers.add(new BrandInitializer(this.companyService));
        variableInitializers.add(new LanguageInitializer());
        variableInitializers.add(new ChannelInitializer(this.levelService, this.companyService));
        variableInitializers.add(new SectionLevelInitializer(this.levelService, this.companyService));
        variableInitializers.add(new HierarchyInitializer(this.levelService, this.companyService));
        variableInitializers.add(new VehicleTypeInitializer(this.levelService));
        variableInitializers.add(new CarlineInitializer(this.levelService, this.vehicleDataService, this.request));
        variableInitializers.add(new CountryRegionInitializer(this.levelService, this.companyService));
        variableInitializers.add(new SiteSearchInitializer(this.currentPage));
        variableInitializers.add(new CountryCodeInitializer(this.lslrLocale));

        // this initializer depends on previous results and must be added after the others
        variableInitializers.add(new ConcatenationInitializer());

        variableInitializers.add(new EditableVarsInitializer());

        return variableInitializers;
    }

    /**
     * Gets the country code.
     * 
     * @return the country code to be used for tracking.
     */
    public final String getCountryCode() {
        return this.lslrLocale != null ? this.lslrLocale.getCountry() : null;
    }

    /**
     * Gets the current page name.
     * 
     * @return the current pages name (not the Omniture pageName property).
     */
    public final String getCurrentPageName() {
        return this.currentPage.getName();
    }

    /**
     * Gets the page name.
     * 
     * @return the pageName property for omniture tracking.
     * @deprecated if possible, use {@link OmnitureService#getOmniturePageName(Page)} instead
     */
    @Deprecated
    // this method has nothing to do with the rest of the model and obviously doesn't belong here
    public final String getPageName() {
        return new PagePathAssembler(this.currentPage.getPath()).createOmniturePageName(this.lslrLocale);
    }

    /**
     * Gets the page name without defined content parts.
     * 
     * @param parts
     *            the content parts to exclude
     * @return page name
     */
    public final String getPageNameWithout(final ContentPart... parts) {
        PagePathAssembler assembler = new PagePathAssembler(this.currentPage.getPath());
        return assembler.createOmniturePageName(this.lslrLocale, ContentPart.BRAND.isNotPartOf(parts), ContentPart.REGION.isNotPartOf(parts),
                ContentPart.COUNTRY.isNotPartOf(parts), ContentPart.LANGUAGE.isNotPartOf(parts));
    }

    /**
     * Gets the seo paramater name.
     * 
     * @return the seo paramater name
     */
    public final String getPaidSearchParameterName() {
        final String seoParameterName = this.companyService.getConfigValue(this.currentPage, CompanyConfigProperties.OMNITURE_PAID_SEARCH_PARAMETER_NAME);
        if (StringUtils.isNotBlank(seoParameterName)) {
            return seoParameterName;
        } else {
            return DEFAULT_SEO_PARAMETER_NAME;
        }
    }

    /**
     * Gets the s account.
     * 
     * @return the sAccount identifier
     */
    public final String getSAccount() {
        return this.sAccount;
    }

    /**
     * Gets the s code path.
     * 
     * @return the sCodePath
     */
    public final String getSCodePath() {
        return this.sCodePath;
    }

    /**
     * Gets the variables.
     * 
     * @return the variables
     */
    public final SortedMap<OmnitureVariables, OmnitureVariableContainer> getVariables() {
        return this.variables;
    }

    /**
     * Checks for click tracking xml data.
     * 
     * @return whether click tracking xml data has been uploaded.
     */
    public final boolean hasClickTrackingXmlData() {
        final Resource trackingXmlResource = this.currentPage.getContentResource(TRACKING_XML_PROP_NAME);
        return null != trackingXmlResource;
    }

    /**
     * Checks if is paid search tracking enabled.
     * 
     * @return true, if is paid search tracking enabled
     */
    public final boolean isPaidSearchTrackingEnabled() {
        final String paidSearchTrackingEnabled = this.companyService.getConfigValue(this.currentPage, CompanyConfigProperties.OMNITURE_PAID_SEARCH_ENABLED);
        return "true".equals(paidSearchTrackingEnabled);
    }

    /**
     * Sets the s account.
     * 
     * @param account
     *            the sAccount to set
     */
    public final void setSAccount(final String account) {
        this.sAccount = account;
    }

    /**
     * Sets the s code path.
     * 
     * @param codePath
     *            the sCodePath to set
     */
    public final void setSCodePath(final String codePath) {
        this.sCodePath = codePath;
    }

    /**
     * Writes the tracking JSON to the JSP writer.
     * 
     * @param writer
     *            the JSP writer
     */
    public final void writeTrackingJson(final JspWriter writer) {

        List<TrackingData> trackingDataList = TrackingUtil.getTrackingDataListFrom(this.currentPage);

        if (null == trackingDataList) {
            return;
        }

        try {
            writer.flush();
            writer.write(new TrackingListJsonGenerator().generateTrackingJson(trackingDataList));
            writer.flush();
        } catch (IOException e) {
            LOG.error("Error while writing JSON tracking data into page: ", e);
        }
    }

    /**
     * Gets the current page.
     * 
     * @return the current page
     */
    public final Page getCurrentPage() {
        return this.currentPage;
    }
}
