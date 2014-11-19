/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;

import com.aditya.gmwp.aem.services.config.ConfigService;
import com.aditya.gmwp.aem.services.vehicledata.data.Brand;
import com.day.cq.wcm.api.components.ComponentContext;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class ConfigUtil {

    /**
     * Gets the {@link Brand} via the {@link ConfigService} from the request.
     * 
     * @param request
     *            the HttpServletRequest
     * @return the Brand
     * @throws JspException
     *             the exception is thrown if the retrieving of the Brand goes wrong
     */
    public static Brand getBrandFromRequest(final HttpServletRequest request) throws JspException {
        final ConfigService configService = getConfigService(request);
        return configService.getBrandNameFromRequest(request);
    }

    /**
     * Gets the {@link Locale} matching to the current request.
     * 
     * @param request
     *            the HttpServletRequest
     * @return the Locale which is determined from the page properties
     */
    public static Locale getLocaleFromRequest(final HttpServletRequest request) {
        final ConfigService configService = getConfigService(request);
        final ComponentContext compContext = (ComponentContext) request.getAttribute(ComponentContext.CONTEXT_ATTR_NAME);
        if (compContext == null) {
            throw new IllegalStateException("No ComponentContext object could be found in request.");
        }

        final Locale locale = configService.getPageLocale(compContext.getPage());
        if (StringUtils.isEmpty(locale.getCountry()) || StringUtils.isEmpty(locale.getLanguage())) {
            throw new IllegalStateException("The locale which was retrieved from CQ page object does not "
                    + "contain country-information. Page-language has to be maintained "
                    + "in page-properties on language level!");
        }
        return locale;
    }

    /**
     * Gets the {@link ConfigService} from the request.
     * 
     * @param request
     *            the HttpServletRequest
     * @return the ConfigService or null
     */
    private static ConfigService getConfigService(final HttpServletRequest request) {
        final SlingBindings s = (SlingBindings) request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
        final SlingScriptHelper sh = s.getSling();
        return sh.getService(ConfigService.class);
    }

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ConfigUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}