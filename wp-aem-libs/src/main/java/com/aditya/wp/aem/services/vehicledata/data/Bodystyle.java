/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface Bodystyle extends Serializable {

    /**
     * Returns all different configurations for the bodystyle.
     * 
     * @return all different configurations for this bodystyle
     */
    Map<String, String> getAllDifferentConfigurations();

    /**
     * Returns the parameters which have to be passed to the application with the given key, when
     * shall be initialized for this body-style. The parameters will be returned as one string that
     * may contain several parameters.
     * 
     * @param appKey
     *            the key identifying the application
     * @return parameter query-string for the application when called for this bodystyle
     */
    String getAppParams(String appKey);

    /**
     * Returns the baseballcard image url.
     * 
     * @return the URL of the image which is shown on a baseball-card. May be null.
     */
    String getBaseballCardImageUrl();

    /**
     * Returns the carline.
     * 
     * @return the carline which this bodystyle belongs to.
     */
    Carline getCarline();

    /**
     * Returns the configuration for which cheapest series.
     * 
     * @param configuration
     *            the configuration for which the cheapest series should be returned
     * @return the cheapest series and drive-type across given configuration
     */
    Series getCheapestAcrossConfiguration(String configuration);

    /**
     * Returns the bodystyle code.
     * 
     * @return the code which technically identifies this bodystyle. The code also contains the
     *         information to identify the carline this bodystyle belongs to.
     */
    String getCode();

    /**
     * This method gets a formatted config by config code.
     * 
     * @param code
     *            the code to get formatted config for
     * @return formatted config or <code>null</code> if no formatted config found
     */
    String getFormattedConfigByCode(String code);

    /**
     * Gets the formatted fleet price.
     * 
     * @return the formatted fleet price
     */
    String getFormattedFleetPrice();

    /**
     * Returns the formatted price.
     * 
     * @return the formatted price of this bodystyle. (e.g. "SEK 210 190,-").
     */
    String getFormattedPrice();

    /**
     * Returns the formatted net price.
     * 
     * @return the formatted net price of this bodystyle. (e.g. "SEK 210 190,-").
     */
    String getFormattedNetPrice();

    /**
     * Returns a collection of series.
     * 
     * @return returns all available series for the bodystyle.
     */
    Collection<Series> getSeries();

    /**
     * Returns a series by series code.
     * 
     * @param seriesCode
     *            the technical code defining the series
     * @return returns the series which is identified by the given series-code, or null if no such
     *         series exists.
     */
    Series getSeries(String seriesCode);

    /**
     * Returns the bodystyle title.
     * 
     * @return the title of this bodystyle, which should only be used for display in CMA.
     */
    String getTitle();

    /**
     * Returns the vc carline code.
     * 
     * @return the "pseudo"-carline code for this bodystyle, that may be different from the actual
     *         code of the carline to which this bodystyle belongs.
     */
    String getVcCarlineCode();

    /**
     * Returns attributes Map with id as key.
     * 
     * @return attributes Map with id as key
     */
    Map<String, Attribute> getAttributes();

    /**
     * Sets attributes Map with id as key.
     * 
     * @param attributes
     *            attributes Map with id as key.
     */
    void setAttributes(final Map<String, Attribute> attributes);

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
