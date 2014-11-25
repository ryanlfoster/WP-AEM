/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.query.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang.time.StopWatch;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;

import com.aditya.wp.aem.exception.VehicleDataException;
import com.aditya.wp.aem.global.AEMTemplateInfo;
import com.aditya.wp.aem.global.GmdsRequestAttribute;
import com.aditya.wp.aem.global.LegalPriceContext;
import com.aditya.wp.aem.properties.BaseballcardBodystyleProperties;
import com.aditya.wp.aem.properties.BaseballcardCarlineProperties;
import com.aditya.wp.aem.properties.ConfigProperties;
import com.aditya.wp.aem.services.config.CompanyService;
import com.aditya.wp.aem.services.config.ConfigService;
import com.aditya.wp.aem.services.config.LanguageSLRService;
import com.aditya.wp.aem.services.config.LevelService;
import com.aditya.wp.aem.services.core.AbstractService;
import com.aditya.wp.aem.services.core.JcrService;
import com.aditya.wp.aem.services.core.ServiceProvider;
import com.aditya.wp.aem.services.vehicledata.VehicleDataService;
import com.aditya.wp.aem.services.vehicledata.data.Bodystyle;
import com.aditya.wp.aem.services.vehicledata.data.BodystyleBaseballcardData;
import com.aditya.wp.aem.services.vehicledata.data.Brand;
import com.aditya.wp.aem.services.vehicledata.data.Carline;
import com.aditya.wp.aem.services.vehicledata.data.CarlineBaseballcardData;
import com.aditya.wp.aem.services.vehicledata.data.ConfigurationBaseballcardData;
import com.aditya.wp.aem.services.vehicledata.data.Series;
import com.aditya.wp.aem.services.vehicledata.data.VehicleData;
import com.aditya.wp.aem.services.vehicledata.data.impl.BodystyleBaseballcardDataImpl;
import com.aditya.wp.aem.services.vehicledata.data.impl.CarlineBaseballcardDataImpl;
import com.aditya.wp.aem.services.vehicledata.data.impl.ConfigurationBaseballcardDataImpl;
import com.aditya.wp.aem.services.vehicledata.data.impl.VehicleDataImpl;
import com.aditya.wp.aem.utils.StringUtil;
import com.aditya.wp.aem.utils.ddp.DdpSsiStatement;
import com.aditya.wp.aem.utils.vi.VehiclePriceInformation;
import com.aditya.wp.aem.utils.vi.price.PriceConfigurationFactory;
import com.aditya.wp.aem.utils.vi.price.capi.PriceConfiguration;
import com.aditya.wp.aem.wrapper.GMResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.text.Text;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(value = VehicleDataService.class)
@Component(metatype = true, immediate = true, name = "com.aditya.wp.aem.services.vehicledata.VehicleDataService", label="WP Vehicle Data Service", description = "%vehicledataservice.description")
public class VehicleDataServiceImpl extends AbstractService<VehicleDataServiceImpl> implements VehicleDataService {

    private static final String LOAD_ATTRIBUTES = "loadAttributes";

    private static final String UNABLE_TO_RETRIEVE_A_BRAND_NAME = "Unable to retrieve a brand-name from given page ";

    private static final String ERROR_FINDING_THE_BASEBALL_CARD = "Error finding the baseball card page";

    private static final String WEBCLIPPING_BASEBALLCARD_PROPERTY = "webclipping_baseballcard";

    private static final String BBC_LINK_PROPERTY = "baseballCardLink";

    /**
     * A job that checks every VehicleData element in the VehcileDataCache for being up-to-date.
     * Each element that is not anymore up-to-date (or has never been loaded before) will be updated
     * by (re-)loading the VehicleData from the market.xml file.
     */
    class VehicleDataUpdaterJob implements Runnable {

        /**
         * Checks all VehicleData elements that are currently present in the VehcileDataCache for
         * being up-to-date and eventually re-loads the data.
         */
        @Override
        public void run() {
            VehicleDataServiceImpl.this.getLog(VehicleDataServiceImpl.this).debug("Checking whether VehicleData for all markets is up-to-date....");
            final List<VehicleDataCache.CacheKey> cacheKeys = VehicleDataServiceImpl.this.vehicleDataCache.getAllCacheKeys();
            VehicleDataServiceImpl.this.getLog(VehicleDataServiceImpl.this).debug("Will check " + cacheKeys.size() + " cached VehicleData elements.");
            for (final VehicleDataCache.CacheKey key : cacheKeys) {
                key.readLock().lock();
                try {
                    if (VehicleDataServiceImpl.this.vehicleDataCache.shouldBeUpdated(key)) {
                    	VehicleDataServiceImpl.this.getLog(VehicleDataServiceImpl.this).debug("Cached VehicleData [" + key.toString() + "] will be updated...");
                        final String lslrPath = key.internalKey(); // hmm,
                        // that's a
                        // bit
                        // dirty?!
                        VehicleData newData = null;
                        Exception loadingException = null;
                        try {
                            newData = loadMarketVehicleData(lslrPath);
                            newData.setInfoMessage(newData.getInfoMessage() + " (updated by updater-thread)");
                        } catch (final IOException e) {
                            loadingException = e;
                        } catch (final RepositoryException e) {
                            loadingException = e;
                        } catch (final VehicleDataException e) {
                            loadingException = e;
                        }
                        key.readLock().unlock(); // must unlock first to obtain
                        // write lock
                        key.writeLock().lock();
                        try {
                            if (null == loadingException) {
                                VehicleDataServiceImpl.this.vehicleDataCache.storeOrUpdate(key, newData);
                            } else {
                                VehicleDataServiceImpl.this.vehicleDataCache.notifyFailedUpdate(key, loadingException.toString());
                            }
                        } finally {
                            key.readLock().lock(); // re-acquire read without
                            // giving up write lock
                            key.writeLock().unlock(); // unlock write, still
                            // hold read
                        }
                    } else {
                    	VehicleDataServiceImpl.this.getLog(VehicleDataServiceImpl.this).debug("Cached VehicleData [" + key.toString() + "] is up-to-date.");
                    }
                } catch (Exception e) {
                	VehicleDataServiceImpl.this.getLog(VehicleDataServiceImpl.this).error("Update for VehicleData [" + key.toString() + "] failed.", e);
                } finally {
                    key.readLock().unlock();
                }
            }
            VehicleDataServiceImpl.this.getLog(VehicleDataServiceImpl.this).debug("Up-to-date-check for VehicleData finished.");
        }
    }

    private static final long A_SECOND = 1000L;

    /** The Constant BODYSTYLE_PLACEHOLDER. */
    private static final String BODYSTYLE_PLACEHOLDER = "${BODYSTYLE}";

    @Property(boolValue = false)
    public static final String CACHED_VEHICLE_DATA_FLUSH_ON_ACTIVATION = "vehicledataservice" + ".vehicledatacache.flushonactivation";

    @Property(boolValue = false)
    public static final String CACHED_VEHICLE_DATA_JOB_DISABLED = "vehicledataservice.vehicledatacache.disabled";

    @Property(value = "900")
    public static final String CACHED_VEHICLE_DATA_MAX_AGE = "vehicledataservice.vehicledatacache.maxage";

    @Property(value = "300")
    public static final String CACHED_VEHICLE_DATA_RETRY_INTERVAL = "vehicledataservice.vehicledatacache.retryinterval";

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.SHORT);

    private static final String MARKET_XML_FILENAME = "market.xml";

    private static final int MAX_LOOKAHEAD = 5;

    private static final String PATH_PLACEHOLDER = "${PATH}";

    private static final String QUERY_FOR_ALL_BBC_BODYTYLE_PAGES = "select * from nt:base " + //
            "where isdescendentnode('/content/') " + //
            "and [cq:template]='/apps/gmds/templates/ts_baseballcard_bodystyle'";

    private static final String QUERY_FOR_BBC_BODYTYLE_PAGES = "select * from [nt:base] " + //
            "where isdescendentnode('" + PATH_PLACEHOLDER + "/' " + //
            "and [sling:resourceType]='gmds/components/baseballcard/bbc_bodystyle_c1' " + //
            "and [bodystyle]='" + BODYSTYLE_PLACEHOLDER + "'";

    private static final String QUERY_LANGUAGE_NAME = Query.JCR_SQL2;

    @Property(value = "DEFINE PREFIX TO ACCESS SSI-SNIPPETS ON WEBSERVER!")
    public static final String SSI_PATH_PREFIX = "vehicledataservice.ssiPathPrefix";

    /**
     * Interval in which the update-job runs. The Job first only triggers an actual update if the
     * VehicleData is expired.
     */
    private static final int UPDATE_JOB_INTERVAL = 30;

    @Property(value = "http://localhost:7800/bin/marketXmlResolver?path=/configurator/DDP")
    public static final String VEHICLE_DATA_BASE_URL = "vehicledataservice.baseurl";

    private Boolean cachedVehicleDataFlushOnActivation;

    private String cachedVehicleDataMaxAge;

    private String cachedVehicleDataReloadRetryInterval;

    @Reference
    private CompanyService companyService;

    @Reference
    private ConfigService configService;

    @Reference
    private JcrService jcrService;

    private boolean doPersistentCaching = true;

    @Reference
    private LanguageSLRService languageSLRService;

    @Reference
    private LevelService levelService;

    @Reference
    private Scheduler scheduler;

    private String ssiPathPrefix = "UNDEFINED";

    private String vehicleDataBaseUrl = "UNDEFINED";

    private final VehicleDataCache vehicleDataCache = new VehicleDataCache();

    @Activate
    protected void activate(final Map<String, Object> config) {
    	getLog(this).info("Vehicle-data-service is being activated...");

        this.vehicleDataBaseUrl = PropertiesUtil.toString(VEHICLE_DATA_BASE_URL, null);
        this.ssiPathPrefix = PropertiesUtil.toString(SSI_PATH_PREFIX, null);
        final Boolean disabled = PropertiesUtil.toBoolean(CACHED_VEHICLE_DATA_JOB_DISABLED, false);

        String help = PropertiesUtil.toString(CACHED_VEHICLE_DATA_MAX_AGE, null);
        this.cachedVehicleDataMaxAge = help;
        try {
            long value = Long.parseLong(help);
            value = value * A_SECOND;
            this.vehicleDataCache.setCachedVehicleDataMaxAge(value);
        } catch (final Exception e) {
            getLog(this).error("Unable to set max-age of cached vehicle data from configuration, " + "falling back to default-value of "
                    + VehicleDataCache.DEFAULT_CACHED_VEHICLE_DATA_MAX_AGE + "ms. Cause for failure: " + e.toString());
            this.vehicleDataCache.setCachedVehicleDataMaxAge(VehicleDataCache.DEFAULT_CACHED_VEHICLE_DATA_MAX_AGE);
        }

        help = PropertiesUtil.toString(CACHED_VEHICLE_DATA_RETRY_INTERVAL, null);
        this.cachedVehicleDataReloadRetryInterval = help;
        try {
            long value = Long.parseLong(help);
            value = value * A_SECOND;
            this.vehicleDataCache.setCachedVehicleDataReloadRetryInterval(value);
        } catch (final Exception e) {
            getLog(this).error("Unable to set reload-retry-interval of cached vehicle data from configuration, " + "falling back to default-value of "
                    + VehicleDataCache.DEFAULT_CACHED_VEHICLE_DATA_RETRY_INTERVAL + "ms. Cause for failure: " + e.toString());
            this.vehicleDataCache.setCachedVehicleDataReloadRetryInterval(//
                    VehicleDataCache.DEFAULT_CACHED_VEHICLE_DATA_RETRY_INTERVAL);
        }
        // Fill vehicle-data-cache with persisted data:
        if (this.doPersistentCaching) {
            this.vehicleDataCache.loadPersisted(this.jcrService.getAdminSession());
        }
        // Eventually invalidate all the data in the cache:
        final Boolean bool = PropertiesUtil.toBoolean(CACHED_VEHICLE_DATA_FLUSH_ON_ACTIVATION, false);
        this.cachedVehicleDataFlushOnActivation = bool;
        if (null != bool && bool.booleanValue()) {
            this.vehicleDataCache.invalidateAll();
            getLog(this).info("All cached vehicle-data invalidated.");
        }

        if (!disabled.booleanValue()) {
            getLog(this).info("Pre-loading all vehicle data...");
            try {
                preLoadVehicleData();
            } catch (final Exception e) {
                getLog(this).info("Error during pre-loading vehicle-data: " + e);
            }
            getLog(this).info("Pre-loading all vehicle data done.");

            try {
            	final ScheduleOptions options = this.scheduler.AT(new Date(), -1, UPDATE_JOB_INTERVAL).canRunConcurrently(false).name(VehicleDataUpdaterJob.class.getName());
                this.scheduler.schedule(new VehicleDataUpdaterJob(), options);
                getLog(this).info("Activation of Vehicle-data-service done.");
            } catch (final Exception e) {
                throw new IllegalStateException("Unable to register periodic job for updating VehicleData in the scheduler service. "
                        + "Original error-message: " + e.toString(), e);
            }
        } else {
            getLog(this).info("Vehicle-data-service is disabled! (See OSGi Configuration of " + "com.gm.gssm.gmds.cq.services.vehicledata.impl.VehicleDataServiceImpl)");
        }
    }

    @Deactivate
    protected final void deactivate() {
        getLog(this).info("Vehicle-data-service is being deactivated...");

        // persist all cached vehicle data to crx:
        if (this.doPersistentCaching) {
            this.vehicleDataCache.persist(this.jcrService.getAdminSession());
        }

        try {
        	this.scheduler.unschedule(VehicleDataUpdaterJob.class.getName());
        } catch (final NoSuchElementException e) {
            getLog(this).trace("scheduler.removeJob(), Ignoring NoSuchElementException " + "because it's only for ensuring that the job is removed.");
        }

        getLog(this).info("Deactivation of Vehicle-data-service done.");
    }

    /**
     * Disable persistent caching, right now this is not configurable externally and only used for
     * testing.
     */
    public final void disablePersistentCaching() {
        this.doPersistentCaching = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * flushVehicleDataCache(com.day.cq.wcm.api.Page)
     */
    @Override
    public final void flushVehicleDataCache(final Page currentPage) {

        final String lslrHandle = Text.getAbsoluteParent(currentPage.getPath(), this.levelService.getLanguageLevel());
        if (StringUtils.isBlank(lslrHandle)) {
            throw new IllegalArgumentException("Unable to determine path to LSLR from current page path " + currentPage.getPath());
        }

        final VehicleDataCache.CacheKey cacheKey = this.vehicleDataCache.getCacheKey(lslrHandle);
        cacheKey.writeLock().lock();
        try {
            if (null != cacheKey && this.vehicleDataCache.hasVehicleData(cacheKey)) {
                this.vehicleDataCache.invalidate(cacheKey);
            }
        } finally {
            cacheKey.writeLock().unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getAllBBCBodsytlePagesWithSameBodystyle(com.day.cq .wcm.api.Page,
     * javax.servlet.http.HttpServletRequest)
     */
    @Override
    public final List<Page> getAllBBCBodsytlePagesWithSameBodystyle(final Page currentBodyStylePage,
                                                                    final HttpServletRequest request) {
        final List<Page> bbcPageList = new ArrayList<Page>();
        final BodystyleBaseballcardData baseballcardData = getBaseballcardData(currentBodyStylePage.getPath(), request);
        final String currentBodystyleCode = baseballcardData.getBaseballcardProperty(BaseballcardBodystyleProperties.BODYSTYLE_CODE);
        // search the vehicle folder/page above the BBC Node
        Page vehiclePage = null;
        Page iPage = currentBodyStylePage;
        boolean found = false;
        int i = 0;
        while (!found && i < MAX_LOOKAHEAD && iPage != null) {
            if (AEMTemplateInfo.TEMPLATE_BASEBALLCARD_NODE.matchesTemplate(iPage)) {
                if (!AEMTemplateInfo.TEMPLATE_BASEBALLCARD_NODE.matchesTemplate(iPage.getParent())) {
                    vehiclePage = iPage.getParent();
                    found = true;
                } else {
                    iPage = iPage.getParent();
                }
            } else {
                iPage = iPage.getParent();
            }
            i += 1;
        }
        if (found && currentBodystyleCode != null && vehiclePage != null) {
            final String query = QUERY_FOR_BBC_BODYTYLE_PAGES.replace(BODYSTYLE_PLACEHOLDER, currentBodystyleCode).replace(PATH_PLACEHOLDER,
                    vehiclePage.getPath());
            final Iterator<Resource> bbcIter = this.jcrService.getResourceResolver().findResources(query, QUERY_LANGUAGE_NAME);
            while (bbcIter.hasNext()) {
                final Resource bbcRes = bbcIter.next();
                final PageManager pageManager = currentBodyStylePage.getContentResource().getResourceResolver().adaptTo(PageManager.class);
                final Page bbcPage = pageManager.getContainingPage(bbcRes);
                if (bbcPage != null) {
                    bbcPageList.add(bbcPage);
                }
            }
        }
        return bbcPageList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getBaseballcardData(java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public final BodystyleBaseballcardData getBaseballcardData(final String bodystyleBaseballcardHandle,
                                                               final HttpServletRequest request) {

        BodystyleBaseballcardDataImpl baseballcardData = null;
        final Resource bbcResource = this.jcrService.getResourceResolver().getResource(bodystyleBaseballcardHandle);
        if (null != bbcResource) {
            final String resourceType = bbcResource.getResourceType();
            if (!com.day.cq.wcm.api.NameConstants.NT_PAGE.equals(resourceType)) {
                if (getLog(this).isDebugEnabled()) {
                    getLog(this).debug("Request to load baseballcard-bodystyle data from '" + bodystyleBaseballcardHandle
                            + "', but this path does not lead to a resource of type '" + com.day.cq.wcm.api.NameConstants.NT_PAGE + "'.");
                }
                return null;
            }
            final Page baseballcardPage = bbcResource.adaptTo(Page.class);
            if (!AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE.matchesTemplate(baseballcardPage)
                    && !AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE_MANUAL.matchesTemplate(baseballcardPage)) {
                if (getLog(this).isDebugEnabled()) {
                    getLog(this).debug("Request to load baseballcard-bodystyle data from '" + bodystyleBaseballcardHandle
                            + "', but this page is not an instance of template '" + AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE.getTemplateName() + ", "
                            + AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE_MANUAL.getTemplateName() + "'.");
                }
                return null;
            }
            baseballcardData = new BodystyleBaseballcardDataImpl(baseballcardPage, this.languageSLRService, this.companyService);

            final String bsCode = baseballcardData.getBaseballcardProperty(BaseballcardBodystyleProperties.BODYSTYLE_CODE);
            if (StringUtils.isNotEmpty(bsCode)) {
                // When a bodystyle-code is maintained, we can set a reference
                // to the bodystyle-object:
                final String clCode = baseballcardData.getBaseballcardProperty(BaseballcardCarlineProperties.CARLINE_CODE);
                final String suffix = baseballcardData.getBaseballcardProperty(BaseballcardCarlineProperties.MODEL_YEAR_SUFFIX);
                final int modelYear = baseballcardData.getModelYear();
                final VehicleData vehicleData = getVehicleData(baseballcardPage, request);
                if (null != vehicleData) {
                    final Carline carline = vehicleData.getCarline(clCode, modelYear, suffix);
                    if (null != carline) {
                        baseballcardData.setRelatedBodystyle(carline.getBodystyle(bsCode));
                    }
                }
            }
        }
        return baseballcardData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getCarlineBaseballcardData(java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public final CarlineBaseballcardData getCarlineBaseballcardData(final String carlineBaseballcardHandle,
                                                                    final HttpServletRequest request) {

        final Resource carlineResource = this.jcrService.getResourceResolver().getResource(carlineBaseballcardHandle);
        if (carlineResource == null) {
            return null;
        }

        final Page baseballcardPage = carlineResource.adaptTo(Page.class);

        return new CarlineBaseballcardDataImpl(baseballcardPage);
    }

    @Override
    public final ConfigurationBaseballcardData getConfigurationBaseballcardData(final String configurationBaseballcardHandle,
                                                                                final HttpServletRequest request) {

        ConfigurationBaseballcardData data = null;
        final Resource r = this.jcrService.getResourceResolver().getResource(configurationBaseballcardHandle);

        if (null != r) {
            final String resourceType = r.getResourceType();
            if (!NameConstants.NT_PAGE.equals(resourceType)) {
                if (getLog(this).isDebugEnabled()) {
                    getLog(this).debug("Request to load baseballcard-configuration data from '" + configurationBaseballcardHandle
                            + "', but this path does not lead to a resource of type '" + NameConstants.NT_PAGE + "'.");
                }
                return data;
            }

            final Page p = r.adaptTo(Page.class);
            if (!AEMTemplateInfo.TEMPLATE_BASEBALLCARD_CONFIGURATION.matchesTemplate(p)) {
                if (getLog(this).isDebugEnabled()) {
                    getLog(this).debug("Request to load baseballcard-configuration data from '" + configurationBaseballcardHandle
                            + "', but this page is not an instance of template '" + AEMTemplateInfo.TEMPLATE_BASEBALLCARD_CONFIGURATION.getTemplateName() + "'.");
                }
                return data;
            }
            data = new ConfigurationBaseballcardDataImpl(p, this.languageSLRService, this.companyService);
        }

        return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getCachedVehicleDataFlushOnActivation()
     */
    @Override
    public final Boolean getCachedVehicleDataFlushOnActivation() {
        return this.cachedVehicleDataFlushOnActivation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getCachedVehicleDataMaxAge()
     */
    @Override
    public final String getCachedVehicleDataMaxAge() {
        return this.cachedVehicleDataMaxAge;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getCachedVehicleDataReloadRetryInterval()
     */
    @Override
    public final String getCachedVehicleDataReloadRetryInterval() {
        return this.cachedVehicleDataReloadRetryInterval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getConfiguredSsiPathPrefix()
     */
    @Override
    public final String getConfiguredSsiPathPrefix() {
        return this.ssiPathPrefix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService# getDoPersistentCaching()
     */
    @Override
    public final boolean getDoPersistentCaching() {
        return this.doPersistentCaching;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getRelatedModelOverviewPage(com.day.cq.wcm.api.Page)
     */
    @Override
    public final Page getRelatedModelOverviewPage(final Page currentPage) {
        if (currentPage != null) {
            Page tmpPage = currentPage;
            for (int i = 0; i < MAX_LOOKAHEAD && tmpPage != null; i++) {
                // check if the page is a Model Overview (T06)
                if (isModelOverviewPage(tmpPage)) {
                    return tmpPage;
                } else if (AEMTemplateInfo.TEMPLATE_FOLDER.matchesTemplate(tmpPage)) {
                    // check if the page is the (model) folder and get the T06
                    final Iterator<Page> childPages = tmpPage.listChildren();
                    while (childPages.hasNext()) {
                        final Page childPage = childPages.next();
                        if (isModelOverviewPage(childPage)) {
                            return childPage;
                        }
                    }
                } else {
                    tmpPage = tmpPage.getParent();
                }
            }
            getLog(this).warn("No related Model Overview page found for page " + currentPage.getPath());
        } else {
            getLog(this).warn("Method getRelatedModelOverviewPage called with 'null' current-page argument.");
        }
        // return null if no Model Overview page was found
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getRelatedBaseballCardPage(com.day.cq.wcm.api.Page)
     */
    @Override
    public final Page getRelatedBaseballCardPage(final Resource currentResource) {
        Page result = null;
        final GMResource gmResource = new GMResource(currentResource);
        if (gmResource != null && gmResource.isExisting()) {
            final PageManager pageManager = gmResource.getResourceResolver().adaptTo(PageManager.class);
            final String baseballCardRef = gmResource.getPropertyAsString(WEBCLIPPING_BASEBALLCARD_PROPERTY);
            if (StringUtils.isNotEmpty(baseballCardRef)) {
                result = pageManager.getPage(baseballCardRef);
            } else {
                result = getRelatedBaseballCardPage(pageManager.getContainingPage(currentResource));
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getRelatedBaseballCardPage(com.day.cq.wcm.api.Page)
     */
    @Override
    public final Page getRelatedBaseballCardPage(final Page currentPage) {

        // The search uses the following algorithm:
        // Go up until you find a folder template. Check if there is a baseball
        // card link attached
        // If not, then look for an overview page in the children of the folder
        // template page
        // Check there for a baseball card link
        // if a link is found, return the page for this link
        try {

            // find the higher folder template page.
            Page folderTemplatePage = null;
            Page tmpPage = currentPage;
            for (int i = 0; i < MAX_LOOKAHEAD; i++) {
                if (AEMTemplateInfo.TEMPLATE_FOLDER.matchesTemplate(tmpPage)) {
                    folderTemplatePage = tmpPage;
                    break;
                } else {
                    tmpPage = tmpPage.getParent();
                }
            }

            if (folderTemplatePage == null) {
                return null;
            }

            final Node jcrContent = folderTemplatePage.adaptTo(Node.class).getNode("jcr:content");

            String bodystyleOrConfigruationBaseballcardLink = null;

            // checks if there is a baseball card link at the folder template
            // page
            if (jcrContent.hasProperty(BBC_LINK_PROPERTY)) {
                bodystyleOrConfigruationBaseballcardLink = jcrContent.getProperty(BBC_LINK_PROPERTY).getString();
            } else {
                // find the overview page
                final Iterator<Page> childPages = folderTemplatePage.listChildren();
                while (childPages.hasNext()) {
                    final Page childPage = childPages.next();
                    if (isModelOverviewPage(childPage)) {
                        final Node jcrContentOverview = childPage.adaptTo(Node.class).getNode("jcr:content");
                        // checks if there is a baseball card link at the
                        // overview page
                        if (jcrContentOverview.hasProperty(BBC_LINK_PROPERTY)) {
                            bodystyleOrConfigruationBaseballcardLink = jcrContentOverview.getProperty(BBC_LINK_PROPERTY).getString();
                            break;
                        }
                    }
                }
            }

            if (bodystyleOrConfigruationBaseballcardLink != null) {
                final PageManager pageManager = currentPage.getPageManager();
                return pageManager.getPage(bodystyleOrConfigruationBaseballcardLink);
            }
        } catch (javax.jcr.PathNotFoundException e) {
            getLog(this).error(ERROR_FINDING_THE_BASEBALL_CARD, e);
        } catch (ValueFormatException e) {
            getLog(this).error(ERROR_FINDING_THE_BASEBALL_CARD, e);
        } catch (RepositoryException e) {
            getLog(this).error(ERROR_FINDING_THE_BASEBALL_CARD, e);
        }

        return null;
    }

    /**
     * Checks if the page is a (t06) model overview page.
     * 
     * @param page
     *            the page
     * @return true, if it is a model overview page
     */
    private boolean isModelOverviewPage(final Page page) {
        return AEMTemplateInfo.TEMPLATE_T06.matchesTemplate(page) || AEMTemplateInfo.TEMPLATE_T06b.matchesTemplate(page)
                || AEMTemplateInfo.TEMPLATE_T06c.matchesTemplate(page) || AEMTemplateInfo.TEMPLATE_T06e.matchesTemplate(page);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#getVehicleData
     * (com.day.cq.wcm.api.Page)
     */
    @Override
    public VehicleData getVehicleData(final Page currentPage) {
        return getVehicleData(currentPage, null);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#getVehicleData
     * (com.day.cq.wcm.api.Page, javax.servlet.http.HttpServletRequest)
     */
    @SuppressWarnings("unchecked")
    @Override
    public VehicleData getVehicleData(final Page currentPage,
                                      final HttpServletRequest request) {
        final String lslrHandle = Text.getAbsoluteParent(currentPage.getPath(), this.levelService.getLanguageLevel());
        if (StringUtils.isEmpty(lslrHandle)) {
            throw new IllegalArgumentException("Unable to determine path to LSLR from current page path " + currentPage.getPath());
        }
        Map<String, VehicleData> dataMapFromRequest = null;
        if (request != null) {
            dataMapFromRequest = (Map<String, VehicleData>) GmdsRequestAttribute.VEHICLE_DATA_LSLR_MAPPING.get(request);
        }
        if (dataMapFromRequest != null) {
            final VehicleData dataFromMap = dataMapFromRequest.get(lslrHandle);
            if (dataFromMap != null) {
                if (getLog(this).isDebugEnabled()) {
                    getLog(this).debug("Found VehicleData for " + currentPage.getPath() + " in global vehicle-data map.");
                }
                return dataFromMap;
            }
        }

        final Brand brand = getBrand(currentPage);
        final Locale marketAndLanguage = getMarketAndLanguage(currentPage);

        final VehicleData data = loadVehicleDataFromCache(lslrHandle, brand, marketAndLanguage);

        storeAsRequestObject(request, lslrHandle, dataMapFromRequest, data);

        return data;
    }

    /**
     * Loads the Vehicle Data from the cache.
     * 
     * @param lslrHandle
     *            the handle to the LSLR page
     * @param brand
     *            the brand
     * @param marketAndLanguage
     *            the locale
     * @return the found VehicleData or null
     */
    private VehicleData loadVehicleDataFromCache(final String lslrHandle,
                                                 final Brand brand,
                                                 final Locale marketAndLanguage) {
        VehicleData data = null;
        final VehicleDataCache.CacheKey cacheKey = this.vehicleDataCache.getCacheKey(lslrHandle);
        cacheKey.readLock().lock();
        try {
            data = this.vehicleDataCache.getVehicleData(cacheKey, false);
            if (null == data) {
                if (!this.vehicleDataCache.hasVehicleData(cacheKey)) {
                    // No data present for this market. This should only happen
                    // if the markets content has
                    // just been created (author) or activated (publish).
                    // We do not attempt to load new data here because that
                    // might block the request thread.
                    // The requester has to live with empty data for now, but
                    // data should be loaded by the
                    // updated thread soon.
                    data = new VehicleDataImpl(brand, marketAndLanguage.getCountry(), marketAndLanguage.getLanguage());
                    data.setErrorMessage("Temporary empty vehicle-data. Actual data should be " + "loaded by scheduled vehicleData-updating job soon.");

                    cacheKey.readLock().unlock();
                    cacheKey.writeLock().lock();
                    try {
                        this.vehicleDataCache.storeOrUpdate(cacheKey, data);
                        this.vehicleDataCache.invalidate(cacheKey);
                    } finally {
                        cacheKey.readLock().lock();
                        cacheKey.writeLock().unlock();
                    }
                } else {
                    // accept out-dated vehicle data and hope that the updater
                    // job will fix this soon.
                    data = this.vehicleDataCache.getVehicleData(cacheKey, true);
                }
            }
        } finally {
            cacheKey.readLock().unlock();
        }
        return data;
    }

    /**
     * Gets the market and language.
     * 
     * @param currentPage
     *            the current page
     * @return the market and language
     */
    private Locale getMarketAndLanguage(final Page currentPage) {
        final Locale marketAndLanguage = this.configService.getPageLocale(currentPage);
        if (StringUtils.isEmpty(marketAndLanguage.getCountry())) {
            throw new IllegalArgumentException("Cannot retrieve vehicle-data for locale '" + marketAndLanguage + "', no country is present!");
        }
        if (StringUtils.isEmpty(marketAndLanguage.getLanguage())) {
            throw new IllegalArgumentException("Cannot retrieve vehicle-data for locale '" + marketAndLanguage + "', no language is present!");
        }
        return marketAndLanguage;
    }

    /**
     * Gets the brand.
     * 
     * @param currentPage
     *            the current page
     * @return the brand
     */
    private Brand getBrand(final Page currentPage) {
        Brand brand = null;
        try {
            brand = this.configService.getBrandNameFromPath(currentPage.getPath());
        } catch (final ParseException e) {
            getLog(this).error(UNABLE_TO_RETRIEVE_A_BRAND_NAME + currentPage.getPath());
            throw new IllegalArgumentException(UNABLE_TO_RETRIEVE_A_BRAND_NAME + currentPage.getPath(), e);
        }
        return brand;
    }

    /**
     * Store as request object.
     * 
     * @param request
     *            the request
     * @param lslrHandle
     *            the lslr handle
     * @param dataMapFromRequest
     *            the data map from request
     * @param data
     *            the data
     */
    private void storeAsRequestObject(final HttpServletRequest request,
                                      final String lslrHandle,
                                      final Map<String, VehicleData> dataMapFromRequest,
                                      final VehicleData data) {
        if (null != data) {
            // Store as request-object for later use by other components on the
            // same page.
            if (dataMapFromRequest != null) {
                dataMapFromRequest.put(lslrHandle, data);
            } else {
                if (request != null) {
                    final Map<String, VehicleData> vehicleDataMap = new HashMap<String, VehicleData>();
                    vehicleDataMap.put(lslrHandle, data);
                    GmdsRequestAttribute.VEHICLE_DATA_LSLR_MAPPING.set(request, vehicleDataMap);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService# getVehicleDataBaseUrl()
     */
    @Override
    public final String getVehicleDataBaseUrl() {
        return this.vehicleDataBaseUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#
     * getVehicleDataHostHeader(com.day.cq.wcm.api.Page)
     */
    @Override
    public final String getVehicleDataHostHeader(final Page currentPage) {
        String httpHostPublish = this.configService.getConfigValue(currentPage, ConfigProperties.HTTP_HOST_PUBLISH);
        if (StringUtils.isNotEmpty(httpHostPublish) && httpHostPublish.contains("http://")) {
            httpHostPublish = httpHostPublish.substring(("http://").length(), httpHostPublish.length());
        }
        return httpHostPublish;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#getVehicleDataUrl
     * (com.gm.gssm.gmds.cq.vehicledata .Brand, java.util.Locale)
     */
    @Override
    public final String getVehicleDataUrl(final Brand brand,
                                          final Locale marketAndLanguage) {

        return getVehicleDataUrl(brand, marketAndLanguage, MARKET_XML_FILENAME);

    }

    /**
     * Gets the vehicle data url.
     * 
     * @param brand
     *            the brand
     * @param marketAndLanguage
     *            the market and language
     * @param fileName
     *            the file name
     * @return the vehicle data url
     */
    @Override
    public final String getVehicleDataUrl(final Brand brand,
                                          final Locale marketAndLanguage,
                                          final String fileName) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.vehicleDataBaseUrl);
        if (!StringUtil.endsWith(sb.toString(), '/')) {
            sb.append("/");
        }
        sb.append(brand.getId().toLowerCase(marketAndLanguage));
        sb.append("/");
        sb.append(marketAndLanguage.getCountry().toUpperCase(marketAndLanguage));
        sb.append("/");
        sb.append(DdpSsiStatement.DISTRIBUTION_CHANNEL);
        sb.append("/");
        sb.append(marketAndLanguage.getLanguage().toLowerCase(marketAndLanguage));
        sb.append("/");
        sb.append(fileName);
        return sb.toString();
    }

    /**
     * Only for unit-testing! Sets the config-service to be used.
     * 
     * @param cfgSrv
     *            the service.
     */
    public final void injectConfigService(final ConfigService cfgSrv) {
        this.configService = cfgSrv;
    }

    /**
     * Only for unit-testing! Sets the level-service to be used.
     * 
     * @param levelSrv
     *            the service.
     */
    public final void injectLevelService(final LevelService levelSrv) {
        this.levelService = levelSrv;
    }

    /**
     * Only for unit-testing! Sets the scheduler to be used.
     * 
     * @param scheduler
     *            the scheduler.
     */
    public final void injectScheduler(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Determines, whether for the given market and language the market.xml data and optionally
     * bodystyle attrbutes.xml files should be loaded via http or from the CRX and dispatched to the
     * according method.
     * 
     * @param currentPage
     *            the current page
     * @param brand
     *            the brand to load data for
     * @param lslrHandle
     *            the handle to the LSLR page
     * @param marketAndLanguage
     *            market (country) and language to load data for
     * @return vehicle data
     * @throws RepositoryException
     *             the repository exception
     * @throws VehicleDataException
     *             when loading market vehicle data fails. The error will be logged in the method
     *             but then thrown to the caller.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private VehicleData loadMarketVehicleData(final Page currentPage,
                                              final Brand brand,
                                              final String lslrHandle,
                                              final Locale marketAndLanguage) throws RepositoryException, VehicleDataException, IOException {
        final Page languagePage = currentPage.getAbsoluteParent(this.levelService.getLanguageLevel());
        if (null == languagePage) {
            throw new IllegalArgumentException("Cannot find LSLR-page for current page " + currentPage.getPath());
        }
        final Resource languagePageRes = languagePage.getContentResource();
        Node node = null != languagePageRes ? languagePageRes.adaptTo(Node.class) : null;
        if (null != node && node.hasNode("vehicledata_config_c1")) {
            final Node configComponentNode = node.getNode("vehicledata_config_c1");

            final boolean loadAttributes = configComponentNode.hasProperty(LOAD_ATTRIBUTES)
                    && StringUtils.equals(configComponentNode.getProperty(LOAD_ATTRIBUTES).getString(), "yes");

            if (configComponentNode.hasProperty("loadfrom") && "crx".equals(configComponentNode.getProperty("loadfrom").getString())) {
                if (configComponentNode.hasNode("marketXmlFile")) {
                    node = configComponentNode.getNode("marketXmlFile");
                    InputStream stream;
                    if (node.hasNode(JcrConstants.JCR_CONTENT)) {
                        stream = node.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
                    } else if (node.hasProperty(JcrConstants.JCR_DATA)) {
                        stream = node.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
                    } else {
                        throw new VehicleDataException("No market.xml-file has been uploaded or " + "uploaded file broken! (Unexpected structure in CRX)");
                    }
                    final XmlVehicleDataLoader xmlLoader = new XmlVehicleDataLoader(brand, marketAndLanguage, lslrHandle, stream);
                    VehicleData vd = xmlLoader.load();

                    if (loadAttributes && configComponentNode.hasNode("attributesXmlFile")) {
                        node = configComponentNode.getNode("attributesXmlFile");
                        InputStream attributesStream;
                        if (node.hasNode(JcrConstants.JCR_CONTENT)) {
                            attributesStream = node.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
                        } else if (node.hasProperty(JcrConstants.JCR_DATA)) {
                            attributesStream = node.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
                        } else {
                            throw new VehicleDataException("No attributes.xml-file has been uploaded or "
                                    + "uploaded file broken! (Unexpected structure in CRX)");
                        }
                        vd = xmlLoader.loadAttributes(vd, attributesStream);
                    }
                    vd.setInfoMessage("Vehicle-data loaded from CRX at " + VehicleDataServiceImpl.DATE_FORMAT.format(System.currentTimeMillis()));
                    return vd;
                } else {
                    throw new VehicleDataException("No market.xml-file has been uploaded!");
                }
            } else {
                final VehicleData vd = loadMarketVehicleDataViaHttp(brand, marketAndLanguage, lslrHandle, currentPage, loadAttributes);
                vd.setInfoMessage("Vehicle-data loaded via HTTP at " + VehicleDataServiceImpl.DATE_FORMAT.format(System.currentTimeMillis()));

                return vd;
            }
        } else {
            final VehicleData vd = loadMarketVehicleDataViaHttp(brand, marketAndLanguage, lslrHandle, currentPage, false);

            vd.setInfoMessage("Vehicle-data loaded via HTTP at " + VehicleDataServiceImpl.DATE_FORMAT.format(System.currentTimeMillis()));

            return vd;
        }
    }

    /**
     * Loads the VehicleData from the market.xml like it as been configured on the LSLR to which the
     * provided path points. This specific method will only extract brand and locale from the LSLR
     * and load a page-object and then delegate the call to @link
     * VehicleDataServiceImpl#loadMarketVehicleData(Page, Brand, String, Locale).
     * 
     * @param pathToLslr
     *            path to the LSLR page that defines which market.xml should be loaded and how.
     * @return a VehicleData object or null if loading fails.
     * @throws VehicleDataException
     *             when loading vehicle data fails.
     * @throws RepositoryException
     *             RepositoryException
     * @throws IOException
     *             IOException
     */
    private VehicleData loadMarketVehicleData(final String pathToLslr) throws VehicleDataException, RepositoryException, IOException {
        final Page lslrPage = this.jcrService.getResourceResolver().adaptTo(PageManager.class).getPage(pathToLslr);
        if (null == lslrPage) {
            getLog(this).error("Unable to get page-object for path " + pathToLslr);
            return null;
        }

        Brand brand = null;
        try {
            brand = this.configService.getBrandNameFromPath(pathToLslr);
        } catch (final ParseException e) {
            final String message = UNABLE_TO_RETRIEVE_A_BRAND_NAME + pathToLslr + ", cannot pre-load vehicle-data.";
            getLog(this).error(message);
            throw new VehicleDataException(message, e);
        }
        final Locale marketAndLanguage = this.configService.getPageLocale(lslrPage);
        if (StringUtils.isEmpty(marketAndLanguage.getCountry())) {
            final String message = "Cannot retrieve vehicle-data for path " + pathToLslr + ", no county is present in configured locale!";
            getLog(this).error(message);
            throw new VehicleDataException(message);
        }
        if (StringUtils.isEmpty(marketAndLanguage.getLanguage())) {
            final String message = "Cannot retrieve vehicle-data for path " + pathToLslr + ", no language is present in configured locale!";
            getLog(this).error(message);
            throw new VehicleDataException(message);
        }
        VehicleData data = null;
        try {

            data = loadMarketVehicleData(lslrPage, brand, pathToLslr, marketAndLanguage);

        } catch (final javax.jcr.PathNotFoundException e) {
            getLog(this).warn("Unable to load vehicle-data for " + pathToLslr + " because of: ",
                    (e.getMessage().contains(LOAD_ATTRIBUTES)) ? "loadAttributes is not set in marketing config component" : e);
            throw new VehicleDataException(e);
        } catch (final VehicleDataException e) {

            getLog(this).debug("Unable to load vehicle-data for " + pathToLslr + " because of: ", e.getMessage());
            throw new VehicleDataException(e);
        }
        return data;
    }

    /**
     * Returns a http response status code from a request made with the default host, market.xml
     * path and virtual host.
     * 
     * @param method
     *            the method object
     * @return status code
     * @throws IOException
     *             the io exception
     */
    private int makeRequest(final HttpMethod method) throws IOException {
        final HttpClient c = new HttpClient();
        return c.executeMethod(method);
    }

    /**
     * Loads market specific vehicle xml-data via HTTP (with optional attributes.xml files per
     * bodystyle) and returns this as a vehicle-data object.
     * 
     * @param brand
     *            the brand to load data for
     * @param marketAndLanguage
     *            market (country) and language to load data for
     * @param lslrHandle
     *            the handle to the LSLR for the current request
     * @param currentPage
     *            the currently requested CQ page to get the configService properties.
     * @param loadAttributes
     *            the load attributes
     * @return vehicle data
     * @throws IOException
     *             when loading market vehicle data fails. The error will be logged in the method
     *             but then thrown to the caller.
     * @throws VehicleDataException
     *             the vehicle data exception
     */
    private VehicleData loadMarketVehicleDataViaHttp(final Brand brand,
                                                     final Locale marketAndLanguage,
                                                     final String lslrHandle,
                                                     final Page currentPage,
                                                     final boolean loadAttributes) throws IOException, VehicleDataException {
        final String baseUrl = getVehicleDataUrl(brand, marketAndLanguage);
        final String virtualHost = getVehicleDataHostHeader(currentPage);
        final HttpMethod method = getHttpGetWithVirtualHost(baseUrl, virtualHost);

        try {
            if (getLog(this).isDebugEnabled()) {
                getLog(this).debug("Loading market.xml for " + lslrHandle + "/" + marketAndLanguage.getLanguage() + " from URL " + baseUrl);
            }

            if (HttpStatus.SC_OK != makeRequest(method)) {
                throw new HttpException(method.getStatusLine().toString());
            }
            final XmlVehicleDataLoader xmlLd = new XmlVehicleDataLoader(brand, marketAndLanguage, lslrHandle, method.getResponseBodyAsStream(), loadAttributes, new XmlHttpLoader(
                    baseUrl.replace("/" + VehicleDataServiceImpl.MARKET_XML_FILENAME, "/"), virtualHost));

            final VehicleData vd = xmlLd.load();

            return vd;

        } catch (final VehicleDataException e) {
            getLog(this).warn("Unable to load market.xml-file with vehicle data from URL \"" + baseUrl + "\" with host header \"" + virtualHost + "\". Exception was: "
                    + e.toString());
            throw e;
        } finally {
            if (null != method) {
                method.releaseConnection();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#getHttpGetWithVirtualHost(java
     * .lang.String, java.lang.String)
     */
    @Override
    public HttpMethod getHttpGetWithVirtualHost(final String baseUrl,
                                                final String virtualHost) throws UnknownHostException, MalformedURLException {
        final String baseRequestUrl = StringUtils.stripToNull(baseUrl);
        if (baseRequestUrl == null || !baseRequestUrl.startsWith("http")) {
            if (getLog(this).isDebugEnabled()) {
                getLog(this).debug("Unable to load market.xml-file with vehicle data, \"" + baseRequestUrl
                        + "\" is not a valid URL. Please configure correct base-URL in VehicleDataService configuration!");
            }
            throw new MalformedURLException("No base-URL in VehicleDataService configuration has been set.");
        }

        final GetMethod get = new GetMethod(baseRequestUrl);

        if (null == virtualHost) {
            if (getLog(this).isDebugEnabled()) {
                getLog(this).debug("Unable to load market.xml-file with vehicle data, \"" + baseRequestUrl
                        + "\" is not a valid URL. Please configure a correct httpHostPublish-URL!");
            }
            throw new UnknownHostException("No httpHostPublish-URL has been set.");
        }
        get.getParams().setVirtualHost(virtualHost);

        return get;
    }

    /**
     * Loads the vehicle data for all markets. This is called during activation of the service.
     * NOTE: For now, this is only for testing purposes.
     */
    private void preLoadVehicleData() {

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Try to find all markets combinations for which the market.xml has to
        // be loaded. That should be all
        // markets that have at least on data-drive baseball-card page.
        final Set<String> lslrPaths = new TreeSet<String>();
        final int lslrLevel = this.levelService.getLanguageLevel();
        final Iterator<Resource> dataDrivenBBCs = this.jcrService.getResourceResolver().findResources(QUERY_FOR_ALL_BBC_BODYTYLE_PAGES, QUERY_LANGUAGE_NAME);
        while (dataDrivenBBCs.hasNext()) {
            final Resource res = dataDrivenBBCs.next();
            final String lslrPath = Text.getAbsoluteParent(res.getPath(), lslrLevel);
            lslrPaths.add(lslrPath);
        }

        // For each relevant market, load the market.xml (via http or from CRX).
        for (final String path : lslrPaths) {
            getLog(this).info("Pre-loading vehicle data for " + path + "...");

            final VehicleDataCache.CacheKey cacheKey = this.vehicleDataCache.getCacheKey(path);

            // Fetch the existing data, we need this later in case of failure...
            VehicleData existingData = null;
            cacheKey.readLock().lock();
            try {
                existingData = this.vehicleDataCache.getVehicleData(cacheKey, true);
            } finally {
                cacheKey.readLock().unlock();
            }

            // Try to load data:
            try {
                final VehicleData data = loadMarketVehicleData(path);
                data.setInfoMessage(data.getInfoMessage() + " (pre-loaded on service startup)");
                cacheKey.writeLock().lock();
                try {
                    this.vehicleDataCache.storeOrUpdate(cacheKey, data);
                } finally {
                    cacheKey.writeLock().unlock();
                }
            } catch (final RepositoryException e) {
                cleanUpFailedLoad(path, cacheKey, existingData, e);
            } catch (final IOException e) {
                cleanUpFailedLoad(path, cacheKey, existingData, e);
            } catch (final VehicleDataException e) {
                cleanUpFailedLoad(path, cacheKey, existingData, e);
            }
        }

        stopWatch.stop();
        getLog(this).info("Pre-loading all market.xml files took " + stopWatch.getTime() + "ms.");
    }

    /**
     * Clean up failed load.
     * 
     * @param path
     *            the path
     * @param cacheKey
     *            the cache key
     * @param existingData
     *            the existing data
     * @param e
     *            the e
     */
    private void cleanUpFailedLoad(final String path,
                                   final VehicleDataCache.CacheKey cacheKey,
                                   final VehicleData existingData,
                                   final Exception e) {
        if (getLog(this).isDebugEnabled()) {
            getLog(this).error("Loading of market.xml failed for path " + path + ", cachekey " + cacheKey, e);
        } else {
            getLog(this).error("Loading of market.xml failed for path " + path + ", cachekey " + cacheKey + ". Turn on debug loglevel for getting stacktrace.");
        }
        if (null == existingData) {
            // There was no data existing before, add an empty one and hope that
            // the updater thread
            // manages to read correct data sooner or later:
            cacheKey.writeLock().lock();
            try {
                // Insert a temporary empty vehicle-data:
                this.vehicleDataCache.storeOrUpdate(cacheKey, new VehicleDataImpl(this.configService.getBrandNameFromPath(path), "x", "y"));
                // Invalidate right away to get an update asap:
                this.vehicleDataCache.notifyFailedUpdate(cacheKey, e.toString());
            } catch (final ParseException pe) {
                getLog(this).error("Unable to determine brand from path " + path);
            } finally {
                cacheKey.writeLock().unlock();
            }
        } else {
            // There was already some data present, must have been from
            // persisted vehicle-data. That's
            // the best we have right now, keep it until an update succeeds.
            getLog(this).info("Keeping existing vehicle-data for " + path + " which was probably loaded from persisted data.");
            this.vehicleDataCache.notifyFailedUpdate(cacheKey, e.toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.VehicleDataService#getMSRPPrice
     * (org.apache.sling.api.SlingHttpServletRequest , java.lang.String)
     */
    @Override
    public final VehiclePriceInformation getMSRPPrice(final SlingHttpServletRequest request,
                                                      final String bbcLink,
                                                      final LegalPriceContext legalPriceContext) {

        final GMResource component = new GMResource(request.getResource());
        final Page p = component.getContainingPage();

        final BodystyleBaseballcardData bodystyleData = getBaseballcardData(bbcLink, request);
        final PriceConfiguration priceConf = PriceConfigurationFactory.getInstance().getConfiguration(ServiceProvider.INSTANCE.fromSling(request), p);
        final VehiclePriceInformation info = new VehiclePriceInformation();
        info.setIsFallbackBaseballCard(bodystyleData.isFallbackBaseballCard());
        info.setLegalPriceSuffix(bodystyleData.getLegalPriceSuffix(legalPriceContext));

        info.setOverwriteSsi(priceConf.getOverwriteSsi());
        info.setPriceLabel(priceConf.getPriceLabel());
        info.setPriceLabelRightAligned(priceConf.getPriceLabelRightAligned());
        info.setShowPrice(priceConf.getShowPrice());
        info.setUseJsSsiInclude(true);

        if (!info.getIsFallbackBaseballCard()) {
            info.setCarlineCode(bodystyleData.getBaseballcardProperty(BaseballcardCarlineProperties.CARLINE_CODE));
            info.setModelYear(bodystyleData.getModelYear());
            info.setModelYearSuffix(bodystyleData.getBaseballcardProperty(BaseballcardCarlineProperties.MODEL_YEAR_SUFFIX));
            info.setBodystyleCode(bodystyleData.getBaseballcardProperty(BaseballcardBodystyleProperties.BODYSTYLE_CODE));
            info.setOverwrittenDdpPrice(bodystyleData.getBaseballcardProperty(BaseballcardBodystyleProperties.DDP_PRICE_OVERWRITE));
            if ("no_selection".equals(info.getBodystyleCode())) {
                info.setOverwrittenPrice(true);
            }

            if (priceConf.getOverwriteSsi()) {
                final VehicleData vehicleData = getVehicleData(p, request);
                final Carline carline = vehicleData.getCarline(info.getCarlineCode(), info.getModelYear(), info.getModelYearSuffix());
                if (carline != null) {
                    final Bodystyle bs = carline.getBodystyle(info.getBodystyleCode());
                    if (bs != null) {
                        info.setFormattedPrice(bs.getFormattedPrice());
                        info.setFormattedIncentivePrice(bs.getFormattedMaxIncentive());
                        for (final Series s : bs.getSeries()) {
                            if (StringUtils.equals(s.getFormattedMaxIncentive(), bs.getFormattedMaxIncentive())) {
                                info.setFormattedDeliveryDate(s.getIncentiveEnd());
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            info.setManualPrice(bodystyleData.getBaseballcardProperty(BaseballcardBodystyleProperties.MANUAL_PRICE));
        }

        return info;
    }
}
