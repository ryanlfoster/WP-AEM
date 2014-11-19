/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aditya.gmwp.aem.services.vehicledata.data.Attribute;
import com.aditya.gmwp.aem.services.vehicledata.data.Series;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class SeriesImpl implements Series {

	public static class Builder {
	    private boolean cheapestAcrossConfiguration;
	    private boolean cheapestDriveTypeAcrossConfigurationAndSeries;
	    private String code;
	    private String configCode;
	    private String formattedConfig;
	    private String formattedDrive;
	    private String formattedFleetPrice;
	    private String formattedPrice;
	    private String formattedNetPrice;
	    private String price;
	    private String netPrice;
	    private String title;
	    private String formattedMaxIncentive;
	    private String incentiveStart;
	    private String incentiveEnd;
	    private String alternateId;

	    /**
	     * Builds the series.
	     * @return the {@link Series}
	     */
	    public SeriesImpl build() {
	    	return new SeriesImpl(this);
	    }

	    /**
	     * Sets teh cheapest across all configurations.
	     * @param cheapestAcrossConfiguration the cheapest
	     * @return the {@link Builder}
	     */
        public Builder cheapestAcrossConfiguration(final boolean cheapestAcrossConfiguration) {
        	this.cheapestAcrossConfiguration = cheapestAcrossConfiguration;
        	return this;
        }

        /**
         * Sets the cheapest drive type
         * @param cheapestDriveTypeAcrossConfigurationAndSeries the cheapest drive type
         * @return the {@link Builder}
         */
        public Builder cheapestDriveTypeAcrossConfigurationAndSeries(final boolean cheapestDriveTypeAcrossConfigurationAndSeries) {
        	this.cheapestDriveTypeAcrossConfigurationAndSeries = cheapestDriveTypeAcrossConfigurationAndSeries;
        	return this;
        }

        /**
         * Sets the series code.
         * @param code the code
         * @return the {@link Builder}
         */
        public Builder code(final String code) {
        	this.code = code;
        	return this;
        }

        /**
         * Sets the config code.
         * @param configCode the config code
         * @return the {@link Builder}
         */
        public Builder configCode(final String configCode) {
        	this.configCode = configCode;
        	return this;
        }

        /**
         * Sets the formatted config
         * @param formattedConfig the formatted config
         * @return the {@link Builder}
         */
        public Builder formattedConfig(final String formattedConfig) {
        	this.formattedConfig = formattedConfig;
        	return this;
        }

        /**
         * Sets the formatted drive.
         * @param formattedDrive the formatted drive
         * @return the {@link Builder}
         */
        public Builder formattedDrive(final String formattedDrive) {
        	this.formattedDrive = formattedDrive;
        	return this;
        }

        /**
         * Sets the formatted fleet price.
         * @param formattedFleetPrice the formatted fleet price
         * @return the {@link Builder}
         */
        public Builder formattedFleetPrice(final String formattedFleetPrice) {
        	this.formattedFleetPrice = formattedFleetPrice;
        	return this;
        }

        /**
         * Sets the formatted price.
         * @param formattedPrice the formatted price
         * @return the {@link Builder}
         */
        public Builder formattedPrice(final String formattedPrice) {
        	this.formattedPrice = formattedPrice;
        	return this;
        }

        /**
         * Sets the formatted net price.
         * @param formattedNetPrice the formatted net price
         * @return the {@link Builder}
         */
        public Builder formattedNetPrice(final String formattedNetPrice) {
        	this.formattedNetPrice = formattedNetPrice;
        	return this;
        }

        /**
         * Sets the price.
         * @param price the price
         * @return the {@link Builder}
         */
        public Builder price(final String price) {
        	this.price = price;
        	return this;
        }

        /**
         * Sets the net price.
         * @param netPrice the net price
         * @return the {@link Builder}
         */
        public Builder netPrice(final String netPrice) {
        	this.netPrice = netPrice;
        	return this;
        }

        /**
         * Sets the title.
         * @param title the title
         * @return the {@link Builder}
         */
        public Builder title(final String title) {
        	this.title = title;
        	return this;
        }

        /**
         * Sets the formatted max incentive price.
         * @param formattedMaxIncentive the formatted max incentive price
         * @return the {@link Builder}
         */
        public Builder formattedMaxIncentive(final String formattedMaxIncentive) {
        	this.formattedMaxIncentive = formattedMaxIncentive;
        	return this;
        }

        /**
         * Sets the incentive start date.
         * @param incentiveStart the incentive start date
         * @return the {@link Builder}
         */
        public Builder incentiveStart(final String incentiveStart) {
        	this.incentiveStart = incentiveStart;
        	return this;
        }

        /**
         * Sets the incentive end date.
         * @param incentiveEnd the incentive end date
         * @return the {@link Builder}
         */
        public Builder incentiveEnd(final String incentiveEnd) {
        	this.incentiveEnd = incentiveEnd;
        	return this;
        }

        /**
         * Sets the alternate id.
         * @param alternateId the alternate id.
         * @return the {@link Builder}
         */
        public Builder alternateId(final String alternateId) {
        	this.alternateId = alternateId;
        	return this;
        }
	}

    private static final long serialVersionUID = 1L;
    private boolean cheapestAcrossConfiguration;
    private boolean cheapestDriveTypeAcrossConfigurationAndSeries;
    private String code;
    private String configCode;
    private String formattedConfig;
    private String formattedDrive;
    private String formattedFleetPrice;
    private String formattedPrice;
    private String formattedNetPrice;
    private String price;
    private String netPrice;
    private String title;
    private String formattedMaxIncentive;
    private String incentiveStart;
    private String incentiveEnd;
    private String alternateId;
    private Map<String, Attribute> attributes = new HashMap<String, Attribute>();
    private final Map<String, String> appKeyToAppParams = new HashMap<String, String>();

    /**
     * Creates a new instance.
     * @param builder the {@link Builder}
     */
    SeriesImpl(final Builder builder) {
        this.code = builder.code;
        this.title = builder.title;
        this.formattedConfig = builder.formattedConfig;
        this.formattedDrive = builder.formattedDrive;
        this.formattedPrice = builder.formattedPrice;
        this.formattedNetPrice = builder.formattedNetPrice;
        this.formattedFleetPrice = builder.formattedFleetPrice;
        this.cheapestAcrossConfiguration = builder.cheapestAcrossConfiguration;
        this.cheapestDriveTypeAcrossConfigurationAndSeries = builder.cheapestDriveTypeAcrossConfigurationAndSeries;
        this.configCode = builder.configCode;
        this.price = builder.price;
        this.netPrice = builder.netPrice;
        this.formattedMaxIncentive = builder.formattedMaxIncentive;
        this.incentiveStart = builder.incentiveStart;
        this.incentiveEnd = builder.incentiveEnd;
        this.alternateId = builder.alternateId;
    }

    /**
     * Adds an app parameter.
     * 
     * @param appKey
     *            the application key
     * @param appParams
     *            the parameters for this application
     */
    public final void addAppParams(final String appKey,
                            final String appParams) {
        this.appKeyToAppParams.put(appKey, appParams);
    }

    @Override
    public final String getAppParams(final String appKey) {
        return this.appKeyToAppParams.get(appKey);
    }

    @Override
    public final String getCode() {
        return this.code;
    }

    @Override
    public final String getConfigCode() {
        return this.configCode;
    }

    @Override
    public String getDescription() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.title);
        final List<String> descrElems = new ArrayList<String>();
        if (null != this.formattedConfig && 0 < this.formattedConfig.length()) {
            descrElems.add(this.formattedConfig);
        }
        if (null != this.formattedDrive && 0 < this.formattedDrive.length()) {
            descrElems.add(this.formattedDrive);
        }
        if (null != this.formattedPrice && 0 < this.formattedPrice.length()) {
            descrElems.add(this.formattedPrice);
        }
        if (descrElems.size() > 0) {
            builder.append(" (");
            for (int i = 0; i < descrElems.size(); i++) {
                builder.append(descrElems.get(i));
                if (i < descrElems.size() - 1) {
                    builder.append(" / ");
                }
            }
            builder.append(")");
        }
        return builder.toString();
    }

    @Override
    public final String getFormattedConfig() {
        return this.formattedConfig;
    }

    @Override
    public final String getFormattedDrive() {
        return this.formattedDrive;
    }

    @Override
    public final String getFormattedFleetPrice() {
        return this.formattedFleetPrice;
    }

    @Override
    public final String getFormattedPrice() {
        return this.formattedPrice;
    }

    @Override
    public final String getFormattedNetPrice() {
        return this.formattedNetPrice;
    }

    @Override
    public final String getPrice() {
        return this.price;
    }

    @Override
    public final String getNetPrice() {
        return this.netPrice;
    }

    @Override
    public final String getTitle() {
        return this.title;
    }

    @Override
    public final Boolean isCheapestAcrossConfiguration() {
        return this.cheapestAcrossConfiguration;
    }

    @Override
    public final Boolean isCheapestDriveTypeAcrossConfigurationAndSeries() {
        return this.cheapestDriveTypeAcrossConfigurationAndSeries;
    }

    @Override
    public Map<String, Attribute> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(final Map<String, Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getFormattedMaxIncentive() {
        return this.formattedMaxIncentive;
    }

    @Override
    public String getIncentiveStart() {
        return this.incentiveStart;
    }

    @Override
    public String getIncentiveEnd() {
        return this.incentiveEnd;
    }

    @Override
    public String getAlternateId() {
        return this.alternateId;
    }
}