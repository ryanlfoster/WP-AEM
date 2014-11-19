/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.vi;

import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.services.vehicledata.data.Carline;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class VehicleInformation {
    /**
     * Creates a new instance invoking copy constructor.
     * 
     * @param vehicleInfo
     *            the vehicle information
     * @return vehicle information
     */
    public static VehicleInformation newInstance(final VehicleInformation vehicleInfo) {
        return new VehicleInformation(vehicleInfo);
    }

    /** the bodystyle code. used when rendering price via ssi. */
    private String bodystyleCode;

    /** the carline code. used when rendering price via ssi. */
    private String carlineCode;

    /** the configuration code. used when rendering price via ssi. */
    private String configCode;

    /** use the manual price from bbc bodystyle if bbc is a fallback one. */
    private boolean isFallbackBaseballCard;

    /** the model year. used when rendering price via ssi. */
    private int modelYear = Carline.INVALID_MODEL_YEAR;

    /** the model year suffix used to differ between carlines with same code and year. */
    private String modelYearSuffix;

    /** the series code. used when rendering price via ssi. */
    private String seriesCode;

    /**
     * Constructor.
     */
    public VehicleInformation() {

    }

    /**
     * Copy constructor.
     * 
     * @param vehicleInfo
     *            the vehicle information
     */
    public VehicleInformation(final VehicleInformation vehicleInfo) {
        this.bodystyleCode = vehicleInfo.getBodystyleCode();
        this.carlineCode = vehicleInfo.getCarlineCode();
        this.configCode = vehicleInfo.getConfigCode();
        this.isFallbackBaseballCard = vehicleInfo.getIsFallbackBaseballCard();
        this.modelYear = vehicleInfo.getModelYear();
        this.modelYearSuffix = vehicleInfo.getModelYearSuffix();
        this.seriesCode = vehicleInfo.getSeriesCode();
    }

    /**
     * Returns the model year with suffix if available.
     * 
     * @return model year and suffix
     */
    public final String getModelYearWithSuffix() {
        if (this.modelYear != Carline.INVALID_MODEL_YEAR) {
            return this.modelYear + (StringUtils.isNotBlank(this.modelYearSuffix) ? this.modelYearSuffix : "");
        }

        return "";
    }

    /**
     * Returns the bodystyle code.
     * 
     * @return bodystyle code
     */
    public final String getBodystyleCode() {
        return this.bodystyleCode;
    }

    /**
     * Returns the carline code.
     * 
     * @return carline code
     */
    public final String getCarlineCode() {
        return this.carlineCode;
    }

    /**
     * Returns the configuration code.
     * 
     * @return configuration code
     */
    public final String getConfigCode() {
        return this.configCode;
    }

    /**
     * Returns the is fallback baseball card.
     * 
     * @return is fallback baseball card
     */
    public final boolean getIsFallbackBaseballCard() {
        return this.isFallbackBaseballCard;
    }

    /**
     * Returns the model year.
     * 
     * @return model year
     */
    public final int getModelYear() {
        return this.modelYear;
    }

    /**
     * Returns the model year suffix.
     * 
     * @return the modelYearSuffix
     */
    public final String getModelYearSuffix() {
        return this.modelYearSuffix;
    }

    /**
     * Returns the series code.
     * 
     * @return series code
     */
    public final String getSeriesCode() {
        return this.seriesCode;
    }

    /**
     * Returns the model year as string.
     * 
     * @return model year as string
     */
    public final String getStrModelYear() {
        return String.valueOf(this.modelYear);
    }

    /**
     * Sets the bodystyle code.
     * 
     * @param bodystyleCode
     *            the bodystyleCode to set
     */
    public final void setBodystyleCode(final String bodystyleCode) {
        this.bodystyleCode = bodystyleCode;
    }

    /**
     * Sets the carline code.
     * 
     * @param carlineCode
     *            the carlineCode to set
     */
    public final void setCarlineCode(final String carlineCode) {
        this.carlineCode = carlineCode;
    }

    /**
     * Sets the configuration code.
     * 
     * @param configCode
     *            the configCode to set
     */
    public final void setConfigCode(final String configCode) {
        this.configCode = configCode;
    }

    /**
     * Sets the is fallback baseball card.
     * 
     * @param isFallbackBaseballCard
     *            the isFallbackBaseballCard to set
     */
    public final void setIsFallbackBaseballCard(final boolean isFallbackBaseballCard) {
        this.isFallbackBaseballCard = isFallbackBaseballCard;
    }

    /**
     * Sets the model year.
     * 
     * @param modelYear
     *            the modelYear to set
     */
    public final void setModelYear(final int modelYear) {
        this.modelYear = modelYear;
    }

    /**
     * Sets the model year suffix.
     * 
     * @param modelYearSuffix
     *            the modelYearSuffix to set
     */
    public final void setModelYearSuffix(final String modelYearSuffix) {
        this.modelYearSuffix = modelYearSuffix;
    }

    /**
     * Sets the series code.
     * 
     * @param seriesCode
     *            the seriesCode to set
     */
    public final void setSeriesCode(final String seriesCode) {
        this.seriesCode = seriesCode;
    }

    /**
     * Sets the str model year.
     * 
     * @param strModelYear
     *            the str model year to set
     */
    public final void setStrModelYear(final String strModelYear) {
        try {
            this.modelYear = Integer.parseInt(strModelYear);
        } catch (NumberFormatException e) {
            this.modelYear = Carline.INVALID_MODEL_YEAR;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "VehicleInformation [bodystyleCode=" + this.bodystyleCode + ", carlineCode=" + this.carlineCode
                + ", configCode=" + this.configCode + ", isFallbackBaseballCard=" + this.isFallbackBaseballCard
                + ", modelYear=" + this.modelYear + ", seriesCode=" + this.seriesCode + "]";
    }
}
