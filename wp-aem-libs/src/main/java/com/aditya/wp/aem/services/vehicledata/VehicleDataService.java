/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.aditya.gmwp.aem.global.LegalPriceContext;
import com.aditya.gmwp.aem.services.vehicledata.data.BodystyleBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.Brand;
import com.aditya.gmwp.aem.services.vehicledata.data.CarlineBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.ConfigurationBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.VehicleData;
import com.aditya.gmwp.aem.utils.vi.VehiclePriceInformation;
import com.day.cq.wcm.api.Page;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface VehicleDataService {
    /**
     * This method triggers a flush of cached vehicle data. Please note that the data might still be used after flushing
     * when updating the data fails.
     * 
     * @param currentPage
     *            the current page
     */
    void flushVehicleDataCache(Page currentPage);

    /**
     * Gets all bbc bodystyle pages with same bodystyle.
     * 
     * @param currentBodyStylePage
     *            the current body style page
     * @param request
     *            the servlet request
     * @return the all bbc bodsytle pages with same bodystyle
     */
    List<Page> getAllBBCBodsytlePagesWithSameBodystyle(final Page currentBodyStylePage,
                                                       HttpServletRequest request);

    /**
     * Returns the baseballcard data.
     * 
     * @param bodystyleBaseballcardHandle
     *            the handle to the bodystyle-baseballcard-page from which data shall be loaded.
     * @param request
     *            the servlet request
     * @return the data from the baseball card.
     */
    BodystyleBaseballcardData getBaseballcardData(final String bodystyleBaseballcardHandle,
                                                  final HttpServletRequest request);

    /**
     * Gets the carline baseballcard data.
     * 
     * @param carlineBaseballcardHandle
     *            the carline baseballcard handle
     * @param request
     *            the request
     * @return the carline baseballcard data
     */
    CarlineBaseballcardData getCarlineBaseballcardData(final String carlineBaseballcardHandle,
                                                       final HttpServletRequest request);

    /**
     * Gets the configuration baseballcard data.
     * 
     * @param configurationBaseballcardHandle
     *            the configuration baseballcard handle
     * @param request
     *            the request
     * @return the configuration baseballcard data
     */
    ConfigurationBaseballcardData getConfigurationBaseballcardData(String configurationBaseballcardHandle,
                                                                   HttpServletRequest request);

    /**
     * Returns whether vehicle data should be flushed once upon activation of the service.
     * 
     * @return property value for flush vehicle data on activation
     */
    Boolean getCachedVehicleDataFlushOnActivation();

    /**
     * Returns the vehicle data max cache age.
     * 
     * @return property value for vehicle data max age
     */
    String getCachedVehicleDataMaxAge();

    /**
     * Returns the vehicle data reload retry interval.
     * 
     * @return property value for vehicle data reload retry interval
     */
    String getCachedVehicleDataReloadRetryInterval();

    /**
     * Returns a prefix that has to be inserted in front of the actual path of any SSI include directive. This
     * path-prefix can be used by the web-server to apply e.g. a proxy-configuration to fetch the actual files that are
     * included via SSI from an external server.
     * 
     * @return see method description
     */
    String getConfiguredSsiPathPrefix();

    /**
     * Returns whether persistent caching is enabled.
     * 
     * @return property value for persist/loading data to/from crx
     */
    boolean getDoPersistentCaching();

    /**
     * Returns the related Model Overview (T06) page of the current model.
     * 
     * @param currentVehiclePage
     *            a page below a vehicles folder
     * @return the related model overview page
     */
    Page getRelatedModelOverviewPage(final Page currentVehiclePage);

    /**
     * Returns the related Baseball Card Page of the current page.
     * 
     * @param currentPage
     *            the current page
     * @return the related baseball card page
     */
    Page getRelatedBaseballCardPage(final Page currentPage);

    /**
     * Returns the related Baseball Card Page of the current resource.
     * 
     * @param currentResource
     *            the current resource
     * @return the related baseball card page
     */
    Page getRelatedBaseballCardPage(final Resource currentResource);

    /**
     * Returns all vehicle data for the given market and the given brand.
     * 
     * @param currentPage
     *            the currently requested CQ page. The service will extract brand, country and language from this in
     *            order to determine which vehicle-data has to be loaded. Thus, the given page must not be further up in
     *            the content-tree than the language-level.
     * @param request
     *            the servlet request.
     * @return vehicle data specific for brand and market.
     */
    VehicleData getVehicleData(final Page currentPage,
                               final HttpServletRequest request);

    /**
     * Gets the vehicle data.
     * 
     * @param currentPage
     *            the current page
     * @return the vehicle data
     */
    VehicleData getVehicleData(final Page currentPage);

    /**
     * Returns the vehicle data base url.
     * 
     * @return property value for vehicle data base url
     */
    String getVehicleDataBaseUrl();

    /**
     * Returns the vehicle data host header.
     * 
     * @param currentPage
     *            the current page
     * @return the value that will be set as host-header when loading vehicle data via HTTP.
     */
    String getVehicleDataHostHeader(final Page currentPage);

    /**
     * This method constructs and returns the URL from which the market.xml file with the vehicle-data for the given
     * brand, country and language will be loaded. This method is mainly for internal use, but is exposed externally for
     * debugging and analysis purposes. In order to retrieve vehicle data, please do not use this low-level method but
     * <code>getVehicleData(Page)</code> directly.
     * 
     * @param brand
     *            the current brand
     * @param marketAndLanguage
     *            a locale containing country and language information.
     * @return the URL from where the market.xml file will be loaded.
     * @see #getVehicleData(Page)
     */
    String getVehicleDataUrl(Brand brand,
                             Locale marketAndLanguage);

    /**
     * This method constructs and returns the URL from which a market specific ddp file can be loaded.
     * 
     * @param brand
     *            the brand
     * @param marketAndLanguage
     *            the market and language
     * @param fileName
     *            the file name
     * @return the URL for ddp file.
     */
    String getVehicleDataUrl(Brand brand,
                             Locale marketAndLanguage,
                             String fileName);

    /**
     * Returns a http get object with a virtual host set. The virtual host acts like if setting host-header in core java
     * to any value.
     * 
     * @param baseUrl
     *            the base request url
     * @param virtualHost
     *            the virtual host
     * @return http get object
     * @throws UnknownHostException
     *             if a http host publish (virtual host) has not been maintained
     * @throws MalformedURLException
     *             if a base url has not been maintained
     */
    HttpMethod getHttpGetWithVirtualHost(String baseUrl,
                                         String virtualHost) throws UnknownHostException, MalformedURLException;

    /**
     * Gets the mSRP price.
     * 
     * @param slingRequest
     *            the sling request
     * @param bbcLink
     *            the bbc link
     * @param legalPriceContext
     *            the legal price context
     * @return the mSRP price
     */
    VehiclePriceInformation getMSRPPrice(SlingHttpServletRequest slingRequest,
                                         String bbcLink,
                                         LegalPriceContext legalPriceContext);
}
