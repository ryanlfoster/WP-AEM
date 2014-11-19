/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.vehicledata.data.Attribute;
import com.aditya.gmwp.aem.services.vehicledata.data.Brand;
import com.aditya.gmwp.aem.services.vehicledata.data.Carline;
import com.aditya.gmwp.aem.services.vehicledata.data.MarketSegment;
import com.aditya.gmwp.aem.services.vehicledata.data.VehicleData;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class VehicleDataImpl implements VehicleData {
    /**
     * A helper class which is used for parsing/finding carlines in a map when carline-code and model-year are used as
     * key.
     */
    private static final class CarlineKey implements Serializable {

        private static final long serialVersionUID = 2L;

        private final String carlineCode;

        private final int hashCode;

        private final String modelYearSuffix;

        private int modelYear = Carline.INVALID_MODEL_YEAR;

        /**
         * Creates a new instance.
         * 
         * @param carlineCode
         *            the code.
         * @param modelYear
         *            the model year.
         */
        private CarlineKey(final String carlineCode, final int modelYear) {
            this(carlineCode, modelYear, "N/A");
        }

        /**
         * Creates a new instance.
         * 
         * @param carlineCode
         *            the code.
         * @param modelYear
         *            the model year
         * @param modelYearSuffix
         *            the model year suffix
         */
        private CarlineKey(final String carlineCode, final int modelYear, final String modelYearSuffix) {
            this.carlineCode = carlineCode;
            this.modelYear = modelYear;
            this.modelYearSuffix = modelYearSuffix;
            // keep hashcode/equals contract
            if (!"N/A".equals(modelYearSuffix)) {
                this.hashCode = (carlineCode + "_" + modelYear + "_" + modelYearSuffix).hashCode();
            } else {
                this.hashCode = (carlineCode + "_" + modelYear).hashCode();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object o) {
            if (o instanceof CarlineKey) {
                final CarlineKey other = (CarlineKey) o;
                return this.carlineCode.equals(other.carlineCode) && this.modelYearSuffix.equals(other.modelYearSuffix)
                        && this.modelYear == other.modelYear;

            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return this.modelYear + " " + this.modelYearSuffix + " " + this.carlineCode;
        }
    }

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 2L;

    private final List<Carline> allCarlines = new ArrayList<Carline>();

    private static final Logger LOG = LoggerFactory.getLogger(VehicleDataImpl.class);

    private final Brand brand;

    private final Map<CarlineKey, Carline> carlinesByCarlineKey = new HashMap<CarlineKey, Carline>();

    private final Map<MarketSegment, List<Carline>> carlinesBySegment = new HashMap<MarketSegment, List<Carline>>();

    private String errorMessage;

    private String infoMessage;

    private final String language;

    private final String market;

    private final Set<Attribute> availableAttributes = new HashSet<Attribute>();

    /**
     * Constructor.
     * 
     * @param brand
     *            the brand
     * @param market
     *            the market as two-letter ISO code
     * @param language
     *            the language as two-letter ISO code
     */
    public VehicleDataImpl(final Brand brand, final String market, final String language) {
        this.brand = brand;
        this.market = market;
        this.language = language;
    }

    /**
     * Adds a carline to vehicle data.
     * 
     * @param carline
     *            the carline to be added.
     */
    public final void addCarline(final Carline carline) {
        this.allCarlines.add(carline);
        if (StringUtils.isEmpty(carline.getModelYearSuffix())) {
            this.carlinesByCarlineKey.put(new CarlineKey(carline.getCode(), carline.getModelYear()), carline);
        } else {
            this.carlinesByCarlineKey.put(
                    new CarlineKey(carline.getCode(), carline.getModelYear(), carline.getModelYearSuffix()), carline);
        }
        if (null != carline.getMarketSegment()) {
            List<Carline> segmentCarlines = this.carlinesBySegment.get(carline.getMarketSegment());
            if (null == segmentCarlines) {
                segmentCarlines = new ArrayList<Carline>();
                this.carlinesBySegment.put(carline.getMarketSegment(), segmentCarlines);
            }
            segmentCarlines.add(carline);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Carline[] getAllCarlines() {
        return this.allCarlines.toArray(new Carline[this.allCarlines.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Brand getBrand() {
        return this.brand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Carline getCarline(final String carlineCode,
                                    final int modelYear,
                                    final String suffix) {

        // Because of GMDSPLM-3134, model-year and model-year-suffix data is now available also in market.xml files for
        // Opel/Vauxhall markets. However, this information may not be present on the baseball-cards. Thus, we try to
        // find the matching carline by it's carline code and ignore missing model-year and model-year-suffix if there
        // is only one carline that matches the code.

        final List<Carline> candidates = new ArrayList<Carline>();
        final Collection<Carline> allCarlinesMethodLocal = this.carlinesByCarlineKey.values();
        for (Carline c : allCarlinesMethodLocal) {
            if (c.getCode().equals(carlineCode) && (StringUtils.isEmpty(suffix) || suffix.equals(c.getModelYearSuffix()))
                    && (Carline.INVALID_MODEL_YEAR == modelYear || c.getModelYear() == modelYear)) {
                candidates.add(c);
            }
        }
        if (candidates.size() == 1) {
            return candidates.get(0);
        } else {
            LOG.warn("Could not find a carline for [" + carlineCode + "|" + modelYear + "|" + suffix + "], there were "
                    + candidates.size() + " matching carlines.");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Carline[] getCarlines(final MarketSegment segment) {
        final Collection<Carline> carlines = this.carlinesBySegment.get(segment);
        if (null != carlines) {
            return carlines.toArray(new Carline[carlines.size()]);
        }
        return new Carline[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getInfoMessage() {
        return this.infoMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getLanguage() {
        return this.language;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getMarket() {
        return this.market;
    }

    /**
     * Sets the error message.
     * 
     * @param message
     *            the message
     */
    @Override
    public final void setErrorMessage(final String message) {
        this.errorMessage = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setInfoMessage(final String infoMessage) {
        this.infoMessage = infoMessage;
    }

    @Override
    public Set<Attribute> getAvailableAttributes() {
        return this.availableAttributes;
    }

    /**
     * Builds a Set of available Attributes (unique by code).
     * 
     * @param attribute
     *            the attribute
     */
    @Override
    public void addAttributeToAvailableAttributes(final Attribute attribute) {
        this.availableAttributes.add(attribute);
    }
}
