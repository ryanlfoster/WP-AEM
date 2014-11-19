/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.aditya.wp.aem.services.config.CompanyService;
import com.aditya.wp.aem.services.config.ConfigService;
import com.aditya.wp.aem.services.config.LanguageSLRService;
import com.aditya.wp.aem.services.config.LevelService;
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
public interface ServiceProvider {

    /**
     * Returns the {@link ColorsJSONService}.
     * 
     * @return the {@link ColorsJSONService}
     */
    ColorsJSONService getColorsJSONService();

    /**
     * Returns the {@link CompanyService}.
     * 
     * @return the {@link CompanyService}
     */
    CompanyService getCompanyService();

    /**
     * Returns the {@link ConfigService}.
     * 
     * @return the {@link ConfigService}
     */
    ConfigService getConfigService();

    /**
     * Returns the {@link ImageImportService}.
     * 
     * @return the {@link ImageImportService}
     */
    ImageImportService getImageImportService();

    /**
     * Returns the {@link ItemAttributeService}.
     * 
     * @return the {@link ItemAttributeService}
     */
    ItemAttributeService getItemAttributeService();

    /**
     * Returns the {@link LanguageSLRService}.
     * 
     * @return the {@link LanguageSLRService}
     */
    LanguageSLRService getLanguageSLRService();

    /**
     * Returns the {@link LevelService}.
     * 
     * @return the {@link LevelService}
     */
    LevelService getLevelService();

    /**
     * Returns the {@link LinkWriterService}.
     * 
     * @return the {@link LinkWriterService}
     */
    LinkWriterService getLinkWriterService();

    /**
     * Returns the {@link QueryService}.
     * 
     * @return the {@link QueryService}
     */
    QueryService getQueryService();

    /**
     * Returns the {@link RobotsCheckClientCacheService}.
     * 
     * @return the {@link RobotsCheckClientCacheService}
     */
    RobotsCheckClientCacheService getRobotsCheckClientCacheService();

    /**
     * Returns the {@link SharingService}.
     * 
     * @return the {@link SharingService}
     */
    SharingService getSharingService();

    /**
     * Returns the {@link VehicleDataService}.
     * 
     * @return the {@link VehicleDataService}
     */
    VehicleDataService getVehicleDataService();

    /**
     * Instance to access helper methods getting {@link ServiceProvider} with a {@link SlingHttpServletRequest} or
     * {@link SlingScriptHelper}.
     */
    final class INSTANCE {

        /**
         * Constructor.
         */
        private INSTANCE() {
        }

        /**
         * Returns the service object matching the provided class.
         * 
         * @param clazz
         *            the class to get the service for
         * @param <T>
         *            the type of the service to get
         * @return service or null, if the service could not be obtained.
         */
        public static <T> T getService(final Class<T> clazz) {
            return getServiceInternal(clazz);
        }

        /**
         * Returns the service object matching the provided class.
         * 
         * @param clazz
         *            the class to get the service for
         * @param <T>
         *            the type of the service to get
         * @return service or null, if the service could not be obtained
         */
        private static <T> T getServiceInternal(final Class<T> clazz) {
            T service = null;
            final Bundle b = FrameworkUtil.getBundle(clazz);
            if (null != b && null != b.getBundleContext()) {
                final BundleContext bc = b.getBundleContext();
                final ServiceReference sr = bc.getServiceReference(clazz.getName());
                service = clazz.cast(bc.getService(sr));
            }
            return service;
        }

        /**
         * Returns a {@link ServiceProvider} with {@link SlingHttpServletRequest}. May throw an
         * {@link IllegalArgumentException} if passed parameter is <code>null</code>.
         * 
         * @param request
         *            the {@link SlingHttpServletRequest} to get {@link SlingScriptHelper} with
         * @return {@link ServiceProvider} or <code>null</code> if service not available.
         */
        public static ServiceProvider fromSling(final SlingHttpServletRequest request) {
            return fromSling(getSlingFromRequest(request));
        }

        /**
         * Returns a {@link ServiceProvider} with {@link HttpServletRequest}. May throw an
         * {@link IllegalArgumentException} if passed parameter is <code>null</code>.
         * 
         * @param request
         *            the {@link HttpServletRequest} to get {@link SlingScriptHelper} with
         * @return {@link ServiceProvider} or <code>null</code> if service not available.
         */
        public static ServiceProvider fromSling(final HttpServletRequest request) {
            return fromSling(getSlingFromRequest(request));
        }

        /**
         * Returns a {@link ServiceProvider} with {@link ServletRequest}. May throw an {@link IllegalArgumentException}
         * if passed parameter is <code>null</code>.
         * 
         * @param request
         *            the {@link ServletRequest} to get {@link SlingScriptHelper} with
         * @return {@link ServiceProvider} or <code>null</code> if service not available.
         */
        public static ServiceProvider fromSling(final ServletRequest request) {
            return fromSling(getSlingFromRequest(request));
        }

        /**
         * Returns a {@link ServiceProvider} with {@link SlingScriptHelper}. May throw an
         * {@link IllegalArgumentException} if passed parameter is <code>null</code>.
         * 
         * @param sling
         *            the {@link SlingScriptHelper} to get service with
         * @return {@link ServiceProvider} or <code>null</code> if service not available.
         */
        public static ServiceProvider fromSling(final SlingScriptHelper sling) {
            if (null == sling) {
                throw new IllegalArgumentException("Parameter 'sling' cannot be null.");
            }

            return sling.getService(ServiceProvider.class);
        }

        /**
         * Returns the {@link SlingScriptHelper} from current request.
         * 
         * @param request
         *            the request to get {@link SlingScriptHelper}
         * @return {@link SlingScriptHelper}
         */
        private static SlingScriptHelper getSlingFromRequest(final ServletRequest request) {
            if (null == request) {
                throw new IllegalArgumentException("Parameter 'request' cannot be null.");
            }
            final SlingBindings sb = (SlingBindings) request
                    .getAttribute("org.apache.sling.api.scripting.SlingBindings");
            final SlingScriptHelper s = sb.getSling();

            return s;
        }
    }
}
