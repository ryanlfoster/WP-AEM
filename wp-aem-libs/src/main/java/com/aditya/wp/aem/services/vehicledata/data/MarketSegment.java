/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum MarketSegment {

    LIGHT_COMMERCIAL_VEHICLE("Light Commercial Vehicle", "LCV"), //
    MEDIUM_DUTY_VEHICLE("Medium Duty Vehicle", "MDV"), //
    PASSENGER_CAR("Passenger Car", "CAR"), //
    SPORT_UTILITY_VEHICLE("Sport Utility Vehicle", "SUV");

    private String abbr;

    private String name;

    /**
     * Private constructor to create MArketSegments with attributes.
     * 
     * @param name
     *            a human readable name of the market segment.
     * @param abbr
     *            an abbreviation which identifies the market segment.
     */
    private MarketSegment(final String name, final String abbr) {
        this.name = name;
        this.abbr = abbr;
    }

    /**
     * Returns the abbreviation.
     * 
     * @return an abbreviation which describes this market segment.
     */
    public String getAbbreviation() {
        return this.abbr;
    }

    /**
     * Returns the name.
     * 
     * @return a human readable name of this market segment.
     */
    public String getName() {
        return this.name;
    }
}
