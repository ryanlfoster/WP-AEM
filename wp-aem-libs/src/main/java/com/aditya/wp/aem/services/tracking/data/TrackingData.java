/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class TrackingData {

    public static final String FLOODLIGHT_PROPERTY = "floodlight";

    public static final String SPOTLIGHT_PROPERTY = "spotlight";

    public static final String OMNITURE_PROPERTY = "omniture";

    private final List<TrackingDataEntry> entries = new ArrayList<TrackingDataEntry>();

    /**
     * The name of the tracking data.
     */
    private String trackingName;

    /**
     * Adds the entry.
     * 
     * @param entry
     *            the entry
     */
    public void addEntry(final TrackingDataEntry entry) {
        this.entries.add(entry);
    }

    /**
     * Gets the entries.
     * 
     * @return the entries
     */
    public List<TrackingDataEntry> getEntries() {
        return this.entries;
    }

    /**
     * Returns the tracking name.
     * 
     * @return the trackingName
     */
    public String getTrackingName() {
        return this.trackingName;
    }

    /**
     * Sets the tracking name.
     * 
     * @param trackingName
     *            the trackingName to set
     */
    public void setTrackingName(final String trackingName) {
        this.trackingName = trackingName;
    }

    /**
     * Serializes this object to JSON. The data field is javascript escaped, so that special characters do not lead a
     * malformed JSON.
     * 
     * @return json representation.
     */
    public String toJson() {

        final StringBuilder jsonRepresentation = new StringBuilder();

        jsonRepresentation.append("[");

        final Iterator<TrackingDataEntry> entriesIter = getEntries().iterator();
        while (entriesIter.hasNext()) {

            final TrackingDataEntry entry = entriesIter.next();

            serializeTrackingDataVariables(jsonRepresentation, entry.getVars());

            if (null != entry.getName()) {
                jsonRepresentation.append(",\"name\":\"").append(entry.getName()).append("\"");
            }
            if (null != entry.getId()) {
                jsonRepresentation.append(",\"id\":\"").append(entry.getId()).append("\"");
            }
            if (null != entry.getType()) {
                jsonRepresentation.append(",\"type\":\"").append(entry.getType()).append("\"");
            }
            if (null != entry.getUrl()) {
                jsonRepresentation.append(",\"url\":\"").append(entry.getUrl()).append("\"");
            }
            jsonRepresentation.append("}");
            if (entriesIter.hasNext()) {
                jsonRepresentation.append(",");
            }
        }

        jsonRepresentation.append("]");

        return jsonRepresentation.toString();
    }

    /**
     * Appends the JSON representation of the given variables to a given StringBuilder.
     * 
     * @param stringBuilder
     *            The String Builder to append to.
     * @param variables
     *            The variables to serialize.
     */
    private static void serializeTrackingDataVariables(final StringBuilder stringBuilder,
                                                       final List<TrackingDataVar> variables) {
        // open JSON object
        stringBuilder.append("{\"vars\":{");

        final Iterator<TrackingDataVar> varsIter = variables.iterator();
        while (varsIter.hasNext()) {

            final TrackingDataVar var = varsIter.next();

            // double quotes must be escaped!
            stringBuilder.append("\"").append(var.getName()).append("\":\"")
                    .append(StringEscapeUtils.escapeJavaScript(var.getData())).append("\"");

            if (varsIter.hasNext()) {
                stringBuilder.append(",");
            }
        }

        // close JSON object
        stringBuilder.append("}");

    }
}
