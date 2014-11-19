/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aditya.gmwp.aem.services.vehicledata.data.Attribute;
import com.aditya.gmwp.aem.services.vehicledata.data.Bodystyle;
import com.aditya.gmwp.aem.services.vehicledata.data.Carline;
import com.aditya.gmwp.aem.services.vehicledata.data.Series;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class BodystyleImpl implements Bodystyle {

	public static class Builder {
	    private String baseballcardImgUrl;
	    private Carline carline;
	    private String code;
	    private String formattedFleetPrice;
	    private String formattedPrice;
	    private String formattedNetPrice;
	    private String title;
	    private String formattedMaxIncentive;
	    private String formattedMinIncentive;

	    public BodystyleImpl build() {
	    	return new BodystyleImpl(this);
	    }

        /**
         * @param baseballcardImgUrl the baseballcardImgUrl to set
         */
        public Builder baseballcardImgUrl(final String baseballcardImgUrl) {
        	this.baseballcardImgUrl = baseballcardImgUrl;
        	return this;
        }
		
        /**
         * @param carline the carline to set
         */
        public Builder carline(final Carline carline) {
        	this.carline = carline;
        	return this;
        }
		
        /**
         * @param code the code to set
         */
        public Builder code(final String code) {
        	this.code = code;
        	return this;
        }
		
        /**
         * @param formattedFleetPrice the formattedFleetPrice to set
         */
        public Builder formattedFleetPrice(final String formattedFleetPrice) {
        	this.formattedFleetPrice = formattedFleetPrice;
        	return this;
        }
		
        /**
         * @param formattedPrice the formattedPrice to set
         */
        public Builder formattedPrice(final String formattedPrice) {
        	this.formattedPrice = formattedPrice;
        	return this;
        }
		
        /**
         * @param formattedNetPrice the formattedNetPrice to set
         */
        public Builder formattedNetPrice(final String formattedNetPrice) {
        	this.formattedNetPrice = formattedNetPrice;
        	return this;
        }
		
        /**
         * @param title the title to set
         */
        public Builder title(final String title) {
        	this.title = title;
        	return this;
        }

        /**
         * @param formattedMaxIncentive the formattedMaxIncentive to set
         */
        public Builder formattedMaxIncentive(final String formattedMaxIncentive) {
        	this.formattedMaxIncentive = formattedMaxIncentive;
        	return this;
        }
		
        /**
         * @param formattedMinIncentive the formattedMinIncentive to set
         */
        public Builder formattedMinIncentive(final String formattedMinIncentive) {
        	this.formattedMinIncentive = formattedMinIncentive;
        	return this;
        }
	}

    private static final long serialVersionUID = 1L;
    private final String baseballcardImgUrl;
    private final Carline carline;
    private final String code;
    private final String formattedFleetPrice;
    private final String formattedPrice;
    private final String formattedNetPrice;
    private final String title;
    private String vcCarlineCode;
    private final String formattedMaxIncentive;
    private final String formattedMinIncentive;
    private final Map<String, String> appKeyToAppParams = new HashMap<String, String>();
    private final List<Series> series = new ArrayList<Series>();
    private Map<String, Attribute> attributes = new HashMap<String, Attribute>();

    /**
     * Creates a new instance.
     * 
     * @param carline
     *            the carline which this bodystyle belongs to.
     * @param code
     *            the code which technically identifies this carline.
     * @param title
     *            the title of this bodystyle.
     * @param formattedPrice
     *            the price of the bodystyle, formatted in the specific currency (e.g.
     *            "SEK 219 900,-")
     * @param formattedNetPrice
     *            the formatted net price
     * @param formattedFleetPrice
     *            the formatted fleet price
     * @param baseballcardImgUrl
     *            the URL of the image to be used in baseball-cards.
     * @param formattedMaxIncentive
     *            formatted maximum incentive
     * @param formattedMinIncentive
     *            formatted minimum incentive
     */
    BodystyleImpl(final Builder builder) {
        this.carline = builder.carline;
        this.code = builder.code;
        this.title = builder.title;
        this.formattedPrice = builder.formattedPrice;
        this.formattedNetPrice = builder.formattedNetPrice;
        this.formattedFleetPrice = builder.formattedFleetPrice;
        this.baseballcardImgUrl = builder.baseballcardImgUrl;
        this.formattedMaxIncentive = builder.formattedMaxIncentive;
        this.formattedMinIncentive = builder.formattedMinIncentive;
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

    /**
     * Adds a series.
     * 
     * @param series
     *            series to be added to internal list
     */
    public final void addSeries(final Series series) {
        this.series.add(series);
    }

    /**
     * Returns all different configurations of this bodystyle.
     * 
     * @return all different configurations of this bodystyle
     */
    @Override
    public final Map<String, String> getAllDifferentConfigurations() {
        final Map<String, String> configs = new HashMap<String, String>();
        for (Series seriesItem : this.series) {
            configs.put(seriesItem.getFormattedConfig(), seriesItem.getConfigCode());
        }
        return configs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getAppParams(final String appKey) {
        return this.appKeyToAppParams.get(appKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getBaseballCardImageUrl() {
        return this.baseballcardImgUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Carline getCarline() {
        return this.carline;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Series getCheapestAcrossConfiguration(final String configuration) {
        for (Series seriesItem : this.series) {
            if (configuration.equals(seriesItem.getConfigCode()) && seriesItem.isCheapestAcrossConfiguration()) {
                return seriesItem;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getCode() {
        return this.code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getFormattedConfigByCode(final String code) {
        String formattedConfig = null;

        if (null != code) {
            final Map<String, String> configs = getAllDifferentConfigurations();
            for (Map.Entry<String, String> config : configs.entrySet()) {
                final String value = config.getValue();
                if (code.equals(value)) {
                    formattedConfig = config.getKey();
                    break;
                }
            }
        }
        return formattedConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getFormattedFleetPrice() {
        return this.formattedFleetPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getFormattedPrice() {
        return this.formattedPrice;
    }

    @Override
    public final String getFormattedNetPrice() {
        return this.formattedNetPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Collection<Series> getSeries() {
        return this.series;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Series getSeries(final String seriesCode) {
        for (Series s : this.series) {
            if (s.getCode().equals(seriesCode)) {
                return s;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTitle() {
        return this.title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getVcCarlineCode() {
        return this.vcCarlineCode;
    }

    /**
     * Sets the pseudo carline code.
     * 
     * @param vcCarlineCode
     *            the code
     */
    public final void setVcCarlineCode(final String vcCarlineCode) {
        this.vcCarlineCode = vcCarlineCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.vehicledata.Bodystyle#getAttributes()
     */
    @Override
    public Map<String, Attribute> getAttributes() {
        return this.attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.vehicledata.Bodystyle#setAttributes(java.util.Map)
     */
    @Override
    public void setAttributes(final Map<String, Attribute> attributes) {
        this.attributes = attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.vehicledata.Bodystyle#getFormattedMaxIncentive()
     */
    @Override
    public String getFormattedMaxIncentive() {
        return this.formattedMaxIncentive;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.vehicledata.Bodystyle#getFormattedMinIncentive()
     */
    @Override
    public String getFormattedMinIncentive() {
        return this.formattedMinIncentive;
    }
}