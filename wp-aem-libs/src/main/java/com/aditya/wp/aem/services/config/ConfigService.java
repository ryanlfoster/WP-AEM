/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.config;

import java.text.ParseException;
import java.util.Locale;

import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.aditya.gmwp.aem.properties.ConfigProperties;
import com.aditya.gmwp.aem.services.vehicledata.data.Brand;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface ConfigService {
	 /**
     * Checks if the given contentPath contains minimal enough elements to reach the element with elementNo. Could be
     * used to avoid a ParseException when calling the {@link getElementFromContentPath} function.
     * 
     * @param contentPath
     *            a content path
     * @param elementNo
     *            the number of a element
     * @return true if the contentPath is deep enough, otherwise false.
     */
    boolean containsElementNo(final String contentPath,
                              final int elementNo);

    /**
     * @return the admin session.
     */
    Session getAdminSession();

    /**
     * Extracts the brand from the given content path.
     * 
     * @param contentPath
     *            a content path to a GMDS content page or component, that starts either with /content or the level
     *            below the /content-level.
     * @return the brand name
     * @throws ParseException
     *             when extracting the brand name fails due to an invalid or unsupported content path.
     */
    Brand getBrandNameFromPath(String contentPath) throws ParseException;

    /**
     * Extracts the brand from the given request.
     * 
     * @param request
     *            a request to a GMDS content page
     * @return the brand name
     * @throws JspException
     *             when extracting the brand from the request path fails
     */
    Brand getBrandNameFromRequest(HttpServletRequest request) throws JspException;

    /**
     * <p>
     * Get the configuration value from the properties of the current page or a parent page of the current page.
     * </p>
     * <p>
     * This function looks up the current properties for this value and returns it. If the current properties have not
     * this property, the function looks up the parent properties. Does no parent page have this property, the function
     * returns null.
     * </p>
     * 
     * @param currentPage
     *            the current page object of the request
     * @param property
     *            the request property (configuration in {@link ConfigProperties})
     * @return the value of the requested property. Does no parent page have this property, the function returns null.
     */
    String getConfigValue(Page currentPage,
                          ConfigProperties property);

    /**
     * Locates the element with the given number in the given content-path, by splitting the content-path at each
     * /-character. Example <code>getElementFromContentPath("/content/opel", 1);</code> will return "opel".
     * 
     * @param contentPath
     *            the path where the element shall be extracted from.
     * @param elementNo
     *            the number of the element to be extracted.
     * @return the element
     * @throws ParseException
     *             if the element cannot be extracted because the path does not contain enough elements.
     */
    String getElementFromContentPath(final String contentPath,
                                     final int elementNo) throws ParseException;

    /**
     * Reads and returns the property with the given name from the global configuration values of the config-service
     * that can be made in the felix management console.
     * 
     * @param propertyName
     *            the name of the property the be read from the global values.
     * @return the property value or null if the property is not configured.
     */
    String getGlobalConfigProperty(String propertyName);

    /**
     * Extracts the market name from the given content path.
     * 
     * @param contentPath
     *            a content path to a GMDS content page or component, that starts either with /content or the level
     *            below the /content-level.
     * @return the market name
     * @throws ParseException
     *             when extracting the market name fails due to an invalid or unsupported content path.
     */
    String getMarketNameFromPath(String contentPath) throws ParseException;

    /**
     * Returns the current pages locale. If no language has been maintained, the default locale en_US will be returned.
     * The locale will always contain a country and a language.
     * 
     * @param page
     *            the current page
     * @return a full locale with country and language
     */
    Locale getPageLocale(Page page);

    /**
     * Returns the current pages locale evaluated from the page path. Fallback is the page local from the page. If no
     * language has been maintained, the default locale en_EN will be returned. The locale will always contain a country
     * and a language.
     * 
     * @param page
     *            the current page
     * @return a full locale with country and language
     */
    Locale getPageLocaleFromPath(Page page);

    /**
     * Extracts the region name from the given content path.
     * 
     * @param contentPath
     *            a content path to a GMDS content page or component, that starts either with /content or the level
     *            below the /content-level.
     * @return the region name
     * @throws ParseException
     *             when extracting the region name fails due to an invalid or unsupported content path.
     */
    String getRegionNameFromPath(String contentPath) throws ParseException;
}
