/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;

import java.io.Serializable;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface Carline extends Serializable {

    /**
     * A value indicating that a carline object does not have a valid model year.
     */
    int INVALID_MODEL_YEAR = -1;

    /**
     * Returns the bodystyle by bodystyle code.
     * 
     * @param bodystyleCode
     *            the bodystyle code
     * @return the bodystyle which is identified by the given bodystyleCode, or null if no such
     *         bodystyle exists.
     */
    Bodystyle getBodystyle(String bodystyleCode);

    /**
     * Returns an array of available bodystyles.
     * 
     * @return all bodystyles which are available for this carline.
     */
    Bodystyle[] getBodystyles();

    /**
     * Returns the brand.
     * 
     * @return the brand this carline belongs to.
     */
    Brand getBrand();

    /**
     * Returns the carline code.
     * 
     * @return the code which technically identifies this carline.
     */
    String getCode();

    /**
     * Returns the formatted fleet price.
     * 
     * @return the formatted fleet price (e.g. "SEK 219 900,-" in Sweden).
     */
    String getFormattedFleetPrice();

    /**
     * Returns the formatted price.
     * 
     * @return the formatted price (e.g. "SEK 219 900,-" in Sweden).
     */
    String getFormattedPrice();

    /**
     * Returns the formatted net price.
     * 
     * @return the formatted price (e.g. "SEK 219 900,-" in Sweden).
     */
    String getFormattedNetPrice();

    /**
     * Returns the market segment.
     * 
     * @return the market segment which this carline belongs to. May be null if carline is not
     *         categorized.
     */
    MarketSegment getMarketSegment();

    /**
     * Returns the model year.
     * 
     * @return the model year of this carline or {@link #INVALID_MODEL_YEAR} if the carline either
     *         has no model year or the model year information could not be loaded correctly.
     */
    int getModelYear();

    /**
     * Returns the model year suffix.
     * 
     * @return suffix
     */
    String getModelYearSuffix();

    /**
     * Returns the carline title.
     * 
     * @return the carline title, which should only be used for display in CMA.
     */
    String getTitle();

    /**
     * Returns vehicle data carline belongs to.
     * 
     * @return the vehicle data where this carline object belongs to.
     */
    VehicleData getVehicleData();

    /**
     * Returns the formatted minimum incentive
     * 
     * @return the formatted min incentive
     */
    String getFormattedMinIncentive();

    /**
     * Returns the formatted maximum incentive
     * 
     * @return the formatted max incentive
     */
    String getFormattedMaxIncentive();
}
