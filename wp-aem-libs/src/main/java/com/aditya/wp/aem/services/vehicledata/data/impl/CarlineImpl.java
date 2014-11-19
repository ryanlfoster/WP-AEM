/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data.impl;

import java.util.ArrayList;
import java.util.List;

import com.aditya.wp.aem.services.vehicledata.data.Bodystyle;
import com.aditya.wp.aem.services.vehicledata.data.Brand;
import com.aditya.wp.aem.services.vehicledata.data.Carline;
import com.aditya.wp.aem.services.vehicledata.data.MarketSegment;
import com.aditya.wp.aem.services.vehicledata.data.VehicleData;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class CarlineImpl implements Carline {
    private static final long serialVersionUID = 1559057654334437938L;

	private final List<Bodystyle> bodystyles = new ArrayList<Bodystyle>();
    private final String code;
    private final String formattedFleetPrice;
    private final String formattedPrice;
    private final String formattedNetPrice;
    private final MarketSegment marketSegment;
    private final int modelYear;
    private final String title;
    private final VehicleData vehicleData;
    private final String modelYearSuffix;
    private final String formattedMaxIncentive;
    private final String formattedMinIncentive;

    public static class Builder {
        private String code;
        private String formattedFleetPrice;
        private String formattedPrice;
        private String formattedNetPrice;
        private MarketSegment marketSegment;
        private int modelYear;
        private String title;
        private VehicleData vehicleData;
        private String modelYearSuffix;
        private String formattedMaxIncentive;
        private String formattedMinIncentive;

        public CarlineImpl build() {
        	return new CarlineImpl(this);
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
         * @param marketSegment the marketSegment to set
         */
        public Builder marketSegment(final MarketSegment marketSegment) {
        	this.marketSegment = marketSegment;
        	return this;
        }
		
        /**
         * @param modelYear the modelYear to set
         */
        public Builder modelYear(final int modelYear) {
        	this.modelYear = modelYear;
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
         * @param vehicleData the vehicleData to set
         */
        public Builder vehicleData(final VehicleData vehicleData) {
        	this.vehicleData = vehicleData;
        	return this;
        }
		
        /**
         * @param modelYearSuffix the modelYearSuffix to set
         */
        public Builder modelYearSuffix(String modelYearSuffix) {
        	this.modelYearSuffix = modelYearSuffix;
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

    /**
     * Creates a new instance.
     * 
     * @param builder the {@link Builder}
     */
    CarlineImpl(final Builder builder) {
        this.vehicleData = builder.vehicleData;
        this.code = builder.code;
        this.title = builder.title;
        this.formattedPrice = builder.formattedPrice;
        this.formattedNetPrice = builder.formattedNetPrice;
        this.formattedFleetPrice = builder.formattedFleetPrice;
        this.marketSegment = builder.marketSegment;
        this.modelYear = builder.modelYear;
        this.modelYearSuffix = builder.modelYearSuffix;
        this.formattedMaxIncentive = builder.formattedMaxIncentive;
        this.formattedMinIncentive = builder.formattedMinIncentive;
    }

    /**
     * Adds a new bodystyle.
     * 
     * @param bodystyle
     *            the bodystyle to be added.
     */
    public final void addBodystyle(final Bodystyle bodystyle) {
        this.bodystyles.add(bodystyle);
    }

    @Override
    public final Bodystyle getBodystyle(final String bodystyleCode) {
        for (Bodystyle bs : this.bodystyles) {
            if (bs.getCode().equals(bodystyleCode)) {
                return bs;
            }
        }
        return null;
    }

    @Override
    public final Bodystyle[] getBodystyles() {
        return this.bodystyles.toArray(new Bodystyle[this.bodystyles.size()]);
    }

    @Override
    public final Brand getBrand() {
        return this.vehicleData.getBrand();
    }

    @Override
    public final String getCode() {
        return this.code;
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
    public final MarketSegment getMarketSegment() {
        return this.marketSegment;
    }

    @Override
    public final int getModelYear() {
        return this.modelYear;
    }

    @Override
    public final String getTitle() {
        return this.title;
    }

    @Override
    public final VehicleData getVehicleData() {
        return this.vehicleData;
    }

    @Override
    public final String getModelYearSuffix() {
        return this.modelYearSuffix;
    }

    @Override
    public String getFormattedMaxIncentive() {
        return this.formattedMaxIncentive;
    }

    @Override
    public String getFormattedMinIncentive() {
        return this.formattedMinIncentive;
    }
}