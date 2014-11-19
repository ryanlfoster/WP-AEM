/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.ddp;

import java.text.ParseException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.exception.DdpSsiStatementException;
import com.aditya.gmwp.aem.services.config.ConfigService;
import com.aditya.gmwp.aem.services.vehicledata.VehicleDataService;
import com.aditya.gmwp.aem.services.vehicledata.data.Brand;
import com.aditya.gmwp.aem.services.vehicledata.data.Carline;
import com.aditya.gmwp.aem.utils.ConfigUtil;
import com.aditya.gmwp.aem.utils.StringUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class DdpSsiStatement {

    private static final String INCLUDE_VIRTUAL = "<!--#include virtual=\"";

    /**
     * The nested inner class used to build the ddp ssi statement object.
     * 
     * @author ewiesenhuetter, namics (deutschland) gmbh
     * @since GMDS Release 1.3
     */
    public static class Builder {

        private String bodystyleCode;
        private String carlineCode;
        private String configCode;
        private String modelYear;
        private String modelYearSuffix;
        private String seriesCode;
        private final HttpServletRequest request;
        private final DdpSsiIncludeType includeType;

        /**
         * Constructor.
         * 
         * @param includeType
         *            The include type
         * @param request
         *            The {@link HttpServletRequest} to get the current brand, locale and vehicle
         *            service from
         */
        public Builder(final DdpSsiIncludeType includeType, final HttpServletRequest request) {
            this.includeType = includeType;
            this.request = request;
        }

        /**
         * This method sets the bodystyle code.
         * 
         * @param bodystyleCode
         *            The bodystyle code to set
         * @return The {@link Builder}
         */
        public final Builder bodystyleCode(final String bodystyleCode) {
            this.bodystyleCode = bodystyleCode;
            return this;
        }

        /**
         * This method creates a new {@link DdpSsiStatement} object.
         * 
         * @return The {@link DdpSsiStatement} object
         */
        public final DdpSsiStatement build() {
            return new DdpSsiStatement(this);
        }

        /**
         * This method sets the carline code.
         * 
         * @param carlineCode
         *            The carline code to set
         * @return The {@link Builder}
         */
        public final Builder carlineCode(final String carlineCode) {
            this.carlineCode = carlineCode;
            return this;
        }

        /**
         * Config code.
         * 
         * @param configCode
         *            the config code
         * @return the builder
         */
        public final Builder configCode(final String configCode) {
            this.configCode = configCode;
            return this;
        }

        /**
         * This method sets the model year.
         * 
         * @param modelYear
         *            The model year to set
         * @return The {@link Builder}
         */
        public final Builder modelYear(final String modelYear) {
            this.modelYear = modelYear;
            return this;
        }

        /**
         * This method sets the model year suffix.
         * 
         * @param modelYearSuffix
         *            The model year suffix to set
         * @return The {@link Builder}
         */
        public final Builder modelYearSuffix(final String modelYearSuffix) {
            this.modelYearSuffix = modelYearSuffix;
            return this;
        }

        /**
         * This method sets the series code.
         * 
         * @param seriesCode
         *            The series code to set
         * @return The {@link Builder}
         */
        public final Builder seriesCode(final String seriesCode) {
            this.seriesCode = seriesCode;
            return this;
        }

    }

    /** The Constant DISTRIBUTION_CHANNEL. */
    public static final String DISTRIBUTION_CHANNEL = "b2c";

    private static final Logger LOG = LoggerFactory.getLogger(DdpSsiStatement.class);

    /**
     * Retrieves the config service from sling.
     * 
     * @param request
     *            the servlet request
     * @return the confis service.
     */
    private static VehicleDataService fetchVehicleDataService(final HttpServletRequest request) {
        final SlingBindings slingBindings = (SlingBindings) request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
        final SlingScriptHelper scriptHelper = slingBindings.getSling();
        return scriptHelper.getService(VehicleDataService.class);
    }

    private final String bodystyleCode;
    private final String carlineCode;
    private final String configCode;
    private final String modelYear;
    private final String modelYearSuffix;
    private final HttpServletRequest request;
    private final String seriesCode;
    private final DdpSsiIncludeType includeType;
    private String configuredPrefix;

    /**
     * Private constructor.
     * 
     * @param builder
     *            The {@link Builder}
     */
    private DdpSsiStatement(final Builder builder) {
        this.includeType = builder.includeType;
        this.bodystyleCode = builder.bodystyleCode;
        this.carlineCode = builder.carlineCode;
        this.modelYear = builder.modelYear;
        this.modelYearSuffix = builder.modelYearSuffix;
        this.seriesCode = builder.seriesCode;
        this.configCode = builder.configCode;
        this.request = builder.request;
    }

    /**
     * Gets the current brand.
     * 
     * @param request
     *            the servlet request
     * @return the brand, extracted from the current request path
     * @throws JspException
     *             when extracting the brand from the request path fails
     */
    private Brand getCurrentBrand(final HttpServletRequest request) throws JspException {
        final SlingBindings s = (SlingBindings) request.getAttribute("org.apache.sling.api.scripting.SlingBindings");
        final SlingScriptHelper sh = s.getSling();
        final ConfigService cfgService = sh.getService(ConfigService.class);
        Brand result = null;
        try {
            final Resource resouce = sh.getRequest().getResource();
            result = cfgService.getBrandNameFromPath(resouce.getPath());
        } catch (ParseException e) {
            throw new JspException("Unable to resolve current brand from content path.", e);
        }
        return result;
    }

    /**
     * This method gets the ssi directive string.
     * 
     * @return The ssi directive
     * @throws DdpSsiStatementException
     *             The exception is thrown if the carline code or bodystyle are missing while the
     *             associated include type is passed
     * @throws JspException
     *             The exception is thrown if retrieving of brand or locale goes wrong
     */
    public String getSsiDirective() throws DdpSsiStatementException, JspException {
        performSanityCheck();

        final StringBuilder ssiDirective = new StringBuilder();
        this.configuredPrefix = getVehicleDataService(this.request).getConfiguredSsiPathPrefix();
        final Brand brand = getCurrentBrand(this.request);
        final Locale locale = ConfigUtil.getLocaleFromRequest(this.request);

        ssiDirective.append(INCLUDE_VIRTUAL);
        ssiDirective.append(pathPrefix());
        ssiDirective.append(brand.getId().toLowerCase(Locale.ENGLISH));
        ssiDirective.append("/");
        ssiDirective.append(locale.getCountry().toUpperCase(Locale.ENGLISH));
        ssiDirective.append("/");
        ssiDirective.append(DISTRIBUTION_CHANNEL);
        ssiDirective.append("/");
        ssiDirective.append(locale.getLanguage().toLowerCase(Locale.ENGLISH));
        ssiDirective.append("/");
        ssiDirective.append(modelYear());
        ssiDirective.append(carlineCode());
        ssiDirective.append(bodystyleAndSeriesCodes());
        ssiDirective.append(configCode());
        ssiDirective.append(this.includeType.getName());
        ssiDirective.append(".");
        ssiDirective.append(this.includeType.getDataType());
        ssiDirective.append("\" -->");

        return ssiDirective.toString();
    }

    /**
     * Checks if carline and bodystyle code requirements are met.
     * 
     * @throws DdpSsiStatementException
     *             if required data is missing.
     */
    private void performSanityCheck() throws DdpSsiStatementException {
        if (this.includeType.requiresCarlineCode() && StringUtils.isEmpty(this.carlineCode)) {
            LOG.warn("No carline code has been specified for include-type '" + this.includeType + "'.");
            throw new DdpSsiStatementException("<!-- ERROR: Unable to write SSI directive because of missing carline-code. -->");
        }
        if (this.includeType.requiresBodystyleCode() && StringUtils.isEmpty(this.bodystyleCode)) {
            LOG.warn("No bodystyle code has been specified for include-type '" + this.includeType + "'.");
            throw new DdpSsiStatementException("<!-- ERROR: Unable to write SSI directive because of missing bodystyle-code. -->");
        }
    }

    /**
     * Creates the path prefix.
     * 
     * @return for example: "<!--#include virtual=\"/configurator/DDP/"
     */
    private String pathPrefix() {
        final StringBuilder directivePrefix = new StringBuilder();

        if (!StringUtil.startsWith(this.configuredPrefix, '/')) {
            directivePrefix.append("/");
        }
        directivePrefix.append(this.configuredPrefix);
        if (!StringUtil.endsWith(this.configuredPrefix, '/')) {
            directivePrefix.append("/");
        }
        return directivePrefix.toString();
    }

    /**
     * Returns the model year with optional suffix, if valid.
     * 
     * @return for example: "2014A"
     */
    private String modelYear() {
        String result = StringUtils.EMPTY;

        if (StringUtils.isNotBlank(this.modelYear) && !this.modelYear.equals(String.valueOf(Carline.INVALID_MODEL_YEAR))) {
            result += this.modelYear;
            if (StringUtils.isNotBlank(this.modelYearSuffix)) {
                result += this.modelYearSuffix;
            }
            result += "/";
        }
        return result;
    }

    /**
     * Returns the carline code with an added slash, if valid.
     * 
     * @return for example: "0P/"
     */
    private String carlineCode() {
        return StringUtils.isNotEmpty(this.carlineCode) ? this.carlineCode + "/" : StringUtils.EMPTY;
    }

    /**
     * Returns the bodystyle and series codes with added slashes, if valid.
     * 
     * @return for example: "0P 68/0PR68/"
     */
    private String bodystyleAndSeriesCodes() {
        String result = StringUtils.EMPTY;
        if (this.includeType.requiresBodystyleCode()) {
            result += this.bodystyleCode + "/";
            if (this.includeType.allowsSeriesCode() && StringUtils.isNotEmpty(this.seriesCode)) {
                result += this.seriesCode + "/";
            }
        }
        return result;
    }

    /**
     * Returns the config code with an added slash, if valid.
     * 
     * @return the config code.
     */
    private String configCode() {
        return StringUtils.isNotEmpty(this.configCode) ? this.configCode + "/" : StringUtils.EMPTY;
    }

    /**
     * Returns the vehicle-data-service by either fetching an instance that has been cached as
     * request-attribute or by getting the instance from sling.
     * 
     * @param request
     *            the servlet request
     * @return the vehicle data service
     */
    private VehicleDataService getVehicleDataService(final HttpServletRequest request) {
        VehicleDataService vds = (VehicleDataService) request.getAttribute("service.vehicleDataService");
        if (null == vds) {
            vds = fetchVehicleDataService(request);
            request.setAttribute("service.vehicleDataService", vds);
        }
        return vds;
    }
}