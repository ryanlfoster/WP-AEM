/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data;

import java.util.Set;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface VehicleData {

    /**
     * Returns all carlines for the market.
     * 
     * @return all carlines which are available in this market. May be empty but will never be null.
     */
    Carline[] getAllCarlines();

    /**
     * Returns the brand for which vehicle data has been retrieved.
     * 
     * @return the brand for which this vehicle data has been retrieved.
     */
    Brand getBrand();

    /**
     * Returns the carline with the given code.
     * 
     * @param carlineCode
     *            the model code of the vehicle, e.g. '0T' for Opel Astra.
     * @param modelYear
     *            the model year
     * @param suffix
     *            the model year suffix
     * @return the matching carline object or null, if carline code is unknown.
     */
    Carline getCarline(String carlineCode,
                       int modelYear,
                       String suffix);

    /**
     * Returns all carlines for the current market which belong to the given market segment.
     * 
     * @param segment
     *            the market segment for which carlines shall be retrieved
     * @return an array of vehicles, which may be empty if there are no carlines for the given market segment, but never
     *         null.
     */
    Carline[] getCarlines(MarketSegment segment);

    /**
     * For debugging purposes.
     * 
     * @return a message describing an error that may have happened with vehicle-data was loaded. Null or an empty
     *         string if no error happened.
     */
    String getErrorMessage();

    /**
     * Returns an additional info message.
     * 
     * @return an informational message about how and when this vehicle data was loaded, for debugging and analysis
     *         purposes.
     */
    String getInfoMessage();

    /**
     * Returns the language for which vehicle data has been retrieved.
     * 
     * @return the language for which the vehicle data has been retrieved, as two-letter ISO-code.
     */
    String getLanguage();

    /**
     * Returns the market.
     * 
     * @return the market (country) for which this vehicle data has been retrieved, as two-letter ISO-code.
     */
    String getMarket();

    /**
     * Sets an additional error message.
     * 
     * @param errorMessage
     *            a message that informs about an error that might have happened while this vehicle-data was loaded or
     *            created.
     */
    void setErrorMessage(final String errorMessage);

    /**
     * Sets an additional info message.
     * 
     * @param infoMessage
     *            an informational message about how and when this vehicle data was loaded, for debugging and analysis
     *            purposes.
     */
    void setInfoMessage(final String infoMessage);

    /**
     * Gets all available attributes.
     */
    Set<Attribute> getAvailableAttributes();

    /**
     * Add an attribute.
     */
    void addAttributeToAvailableAttributes(Attribute attribute);
}
