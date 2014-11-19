/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.util;

import java.util.List;

import com.aditya.gmwp.aem.services.tracking.data.TrackingData;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class TrackingListJsonGenerator {

    /**
     * Writes the tracking JSON to the JSP writer.
     * 
     * @param writer
     *            the JSP writer
     */
    public final String generateTrackingJson(final List<TrackingData> trackingDataList) {

        StringBuilder output = new StringBuilder();

        final TrackingData omnitureData = findInTrackingDataList(trackingDataList, TrackingData.OMNITURE_PROPERTY);
        if (null != omnitureData) {
            output.append("var pageTrackJSON = ");
            output.append(omnitureData.toJson());
            output.append(";\n\t\t\t\t");
        }

        TrackingData floodlightData = findInTrackingDataList(trackingDataList, TrackingData.FLOODLIGHT_PROPERTY);
        TrackingData spotlightData = findInTrackingDataList(trackingDataList, TrackingData.SPOTLIGHT_PROPERTY);
        if (null != floodlightData || null != spotlightData) {
            if (null == floodlightData) {
                floodlightData = new TrackingData();
            }
            if (null == spotlightData) {
                spotlightData = new TrackingData();
            }
            output.append("var dartTrackJSON = [{");
            output.append("\"flood\": ");
            output.append(floodlightData.toJson());
            output.append(",\"spot\": ");
            output.append(spotlightData.toJson());
            output.append("}];\n\t\t\t\t");
        }
        return output.toString();
    }

    /**
     * Finds the tracking data with the given name in the list of available tracking data.
     * 
     * @param trackingDataList
     *            the list
     * @param name
     *            the name to look for
     * @return the tracking data for the given name or null if no such data exists.
     */
    private TrackingData findInTrackingDataList(final List<TrackingData> trackingDataList,
                                                final String name) {
        for (TrackingData td : trackingDataList) {
            if (td.getTrackingName().equals(name)) {
                return td;
            }
        }
        return null;
    }
}
