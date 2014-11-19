/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface Series extends Serializable {

	/**
	 * Helper class for comparing (sorting) Series objects.
	 */
	public static class SeriesComparator implements Comparator<Series>, Serializable {

	    private static final long serialVersionUID = 1L;

	    /**
	     * {@inheritDoc}
	     */
	    @Override
	    public int compare(final Series series1,
	                       final Series series2) {

	        int comp = series1.getTitle().compareTo(series2.getTitle());
	        if (0 == comp) {
	            comp = series1.getFormattedPrice().compareTo(series2.getFormattedPrice());
	        }
	        return comp;
	    }
	}

    /**
     * Returns the parameters which have to be passed to the application with the given key, when
     * shall be initialized for this series. The parameters will be returned as one string that may
     * contain several parameters.
     * 
     * @param appKey
     *            the key identifying the application
     * @return parameter query-string for the application when called for this series
     */
    String getAppParams(String appKey);

    /**
     * Returns the series code.
     * 
     * @return the code which technically identifies this series.
     */
    String getCode();

    /**
     * Returns the configuration code.
     * 
     * @return the config code.
     */
    String getConfigCode();

    /**
     * Returns a human readable description of the series. The description consists of the series
     * title and - if applicable - the formatted-config, the formatted-drive and the
     * formatted-price.
     * 
     * @return a human readable description of this series.
     * @return
     */
    String getDescription();

    /**
     * Returns the formatted configuration.
     * 
     * @return the formattd config of this series.
     */
    String getFormattedConfig();

    /**
     * Returns the formatted drive.
     * 
     * @return the formatted drive of this series.
     */
    String getFormattedDrive();

    /**
     * Returns the formatted fleet price.
     * 
     * @return the formatted fleet price of this series.
     */
    String getFormattedFleetPrice();

    /**
     * Returns the formatted price.
     * 
     * @return the formatted price of this series.
     */
    String getFormattedPrice();

    /**
     * Returns the formatted net price.
     * 
     * @return the formatted net price of this series.
     */
    String getFormattedNetPrice();

    /**
     * Returns the unformatted price.
     * 
     * @return theun formatted price of this series.
     */
    String getPrice();

    /**
     * Returns the unformatted net price.
     * 
     * @return theun formatted net price of this series.
     */
    String getNetPrice();

    /**
     * Returns the series title.
     * 
     * @return a human readable title of the series.
     */
    String getTitle();

    /**
     * Returns the cheapest configuration.
     * 
     * @return true if is cheapest configuration false otherwise
     */
    Boolean isCheapestAcrossConfiguration();

    /**
     * Returns whether is cheapest drive type.
     * 
     * @return true if is cheapest drive type false otherwise
     */
    Boolean isCheapestDriveTypeAcrossConfigurationAndSeries();

    /**
     * Returns Array of Vehicle Attributes.
     */
    Map<String, Attribute> getAttributes();

    /**
     * Sets the Array of Vehicle Attributes.
     */
    void setAttributes(final Map<String, Attribute> attributes);

    /**
     * Returns the formatted maximum incentive
     * 
     * @return the formatted max incentive
     */
    String getFormattedMaxIncentive();

    /**
     * Returns the incentive start date
     * 
     * @return the incentive start date
     */
    String getIncentiveStart();

    /**
     * Returns the incentive end date
     * 
     * @return the incentive end date
     */
    String getIncentiveEnd();

    /**
     * Returns the alternate id
     * 
     * @return
     */
    String getAlternateId();
}
