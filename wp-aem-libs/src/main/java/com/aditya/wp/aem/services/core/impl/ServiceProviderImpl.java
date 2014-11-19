/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.aditya.wp.aem.services.config.CompanyService;
import com.aditya.wp.aem.services.config.ConfigService;
import com.aditya.wp.aem.services.config.LanguageSLRService;
import com.aditya.wp.aem.services.config.LevelService;
import com.aditya.wp.aem.services.core.LinkWriterService;
import com.aditya.wp.aem.services.core.QueryService;
import com.aditya.wp.aem.services.core.ServiceProvider;
import com.aditya.wp.aem.services.imports.ImageImportService;
import com.aditya.wp.aem.services.sharing.RobotsCheckClientCacheService;
import com.aditya.wp.aem.services.sharing.SharingService;
import com.aditya.wp.aem.services.vehicledata.ColorsJSONService;
import com.aditya.wp.aem.services.vehicledata.ItemAttributeService;
import com.aditya.wp.aem.services.vehicledata.VehicleDataService;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(ServiceProvider.class)
@Component(name = "com.gm.gssm.gmds.cq.services.core.ServiceProvider")
public class ServiceProviderImpl implements ServiceProvider {
    @Reference
    private ColorsJSONService colorsJsonService;

    @Reference
    private CompanyService companyService;

    @Reference
    private ConfigService configService;

    @Reference
    private ImageImportService imageImportService;

    @Reference
    private ItemAttributeService itemAttributeService;

    @Reference
    private LanguageSLRService languageSLRService;

    @Reference
    private LevelService levelService;

    @Reference
    private LinkWriterService linkWriterService;

    @Reference
    private QueryService queryService;

    @Reference
    private RobotsCheckClientCacheService robotsCheckClientCacheService;

    @Reference
    private SharingService sharingService;

    @Reference
    private VehicleDataService vehicleDataService;

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getColorsJSONService()
     */
    @Override
    public final ColorsJSONService getColorsJSONService() {
        return this.colorsJsonService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getCompanyService()
     */
    @Override
    public final CompanyService getCompanyService() {
        return this.companyService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getConfigService()
     */
    @Override
    public final ConfigService getConfigService() {
        return this.configService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getImageImportService()
     */
    @Override
    public final ImageImportService getImageImportService() {
        return this.imageImportService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getItemAttributeService()
     */
    @Override
    public final ItemAttributeService getItemAttributeService() {
        return this.itemAttributeService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getLanguageSLRService()
     */
    @Override
    public final LanguageSLRService getLanguageSLRService() {
        return this.languageSLRService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getLevelService()
     */
    @Override
    public final LevelService getLevelService() {
        return this.levelService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getLinkWriterService()
     */
    @Override
    public final LinkWriterService getLinkWriterService() {
        return this.linkWriterService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getQueryService()
     */
    @Override
    public final QueryService getQueryService() {
        return this.queryService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getRobotsCheckClientCacheService()
     */
    @Override
    public final RobotsCheckClientCacheService getRobotsCheckClientCacheService() {
        return this.robotsCheckClientCacheService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getSharingService()
     */
    @Override
    public SharingService getSharingService() {
        return this.sharingService;
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.wp.aem.services.core.ServiceProvider#getVehicleDataService()
     */
    @Override
    public final VehicleDataService getVehicleDataService() {
        return this.vehicleDataService;
    }
}
