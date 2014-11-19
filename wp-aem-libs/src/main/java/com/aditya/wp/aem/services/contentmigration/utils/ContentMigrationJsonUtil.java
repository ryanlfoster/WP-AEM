/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.contentmigration.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import com.aditya.gmwp.aem.services.contentmigration.Job;
import com.aditya.gmwp.aem.services.contentmigration.JobMetadata;
import com.aditya.gmwp.aem.services.contentmigration.SystemInfo;
import com.aditya.gmwp.aem.services.contentmigration.impl.JobMetadataImpl;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ContentMigrationJsonUtil {
    /**
     * Private constructor, this class contains only static methods.
     */
    private ContentMigrationJsonUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }

    /**
     * Creates a list of job meta data objects from a given string with JSON data.
     * 
     * @param jsonString
     *            the string with json data
     * @param system
     *            the system on which the jobs are running.
     * @param allKnownJobs
     *            a list of all known jobs.
     * @return list of job meta data.
     */
    static List<JobMetadata> jsonStringToJobMetadataList(final String jsonString,
                                                         final SystemInfo system,
                                                         final List<Job> allKnownJobs) {
        final List<JobMetadata> metaDataList = new ArrayList<JobMetadata>();
        try {
            final JSONObject json = new JSONObject(jsonString);
            final Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                final String key = keys.next();
                final JSONObject metadataJson = json.getJSONObject(key);
                final JobMetadataImpl jobMetaData = new JobMetadataBuilder(metadataJson, allKnownJobs).build();
                jobMetaData.setSystemInfo(system);
                metaDataList.add(jobMetaData);
            }
        } catch (JSONException e) {
            throw new IllegalStateException("Unable to load JSON data.", e);
        }
        return metaDataList;
    }

    /**
     * Returns a string from the given JSON data. The method assumes that the string is found on the top-level of the
     * data structure.
     * 
     * @param json
     *            the JSON data
     * @param key
     *            the key to return from the JSON
     * @return see above.
     * @throws JSONException
     *             when the given JSON data does not have valid JSON format
     */
    public static String getStringFromJson(final String json,
                                           final String key) throws JSONException {
        String value = null;
        if (null != json) {
            final JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(key)) {
                value = jsonObject.getString(key);
            }
        }
        return value;
    }

    /**
     * Class for building JobMetadata objects from JSON data.
     */
    private static final class JobMetadataBuilder {

        private final JSONObject json;

        private final List<Job> allKnownJobs;

        /**
         * Creates a new instance.
         * 
         * @param json
         *            the json data.
         * @param allKnownJobs
         *            all known jobs
         */
        private JobMetadataBuilder(final JSONObject json, final List<Job> allKnownJobs) {
            this.json = json;
            this.allKnownJobs = allKnownJobs;
        }

        /**
         * Builds the object.
         * 
         * @return job meta data
         * @throws JSONException
         *             when reading JSON data fails.
         */
        JobMetadataImpl build() throws JSONException {
            final JobMetadataImpl jobMetaData = new JobMetadataImpl(determineJob(this.json.getString("jobId")));
            jobMetaData.setLastExecutionTime(saveGetDate(this.json.getString("lastExecutionTime")));
            jobMetaData.setLastSimulationTime(saveGetDate(this.json.getString("lastSimulationTime")));
            jobMetaData.setCurrentStatus(JobMetadata.CurrentStatus.valueOf(this.json.getString("currentStatus")));
            jobMetaData.setFixedOccurrencesInLastRun(saveGetInt(this.json.getString("fixedOccurrences")));
            jobMetaData.setFailedOccurrencesInLastRun(saveGetInt(this.json.getString("failedOccurrences")));
            jobMetaData.setPendingOccurrences(saveGetInt(this.json.getString("pendingOccurrences")));
            jobMetaData.setErrorMessages(saveGetStringList(this.json.getJSONArray("logMessages")));
            jobMetaData.setErrorMessagesSimulation(saveGetStringList(this.json.getJSONArray("simulationLogMessages")));
            if (this.json.has("token")) {
                jobMetaData.setToken(this.json.getString("token"));
            }
            return jobMetaData;
        }

        /**
         * Creates a list of strings from the given JSON array, with checking for null.
         * 
         * @param jsonArray
         *            the json array
         * @return list of strings
         * @throws JSONException
         *             when reading from the JSOn array fails.
         */
        private List<String> saveGetStringList(final JSONArray jsonArray) throws JSONException {
            if (null != jsonArray && jsonArray.length() > 0) {
                final List<String> list = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
                return list;
            }
            return Collections.emptyList();
        }

        /**
         * Looks-up the job with the given ID.
         * 
         * @param jobId
         *            the job ID
         * @return the job
         */
        Job determineJob(final String jobId) {
            for (Job job : this.allKnownJobs) {
                if (job.getClass().getName().equals(jobId)) {
                    return job;
                }
            }
            throw new IllegalStateException("Unknown Job '" + jobId + "'.");
        }

        /**
         * Creates a date object from the given string with checking for null.
         * 
         * @param dateString
         *            the date string.
         * @return date object or null.
         */
        private Date saveGetDate(final String dateString) {
            if (StringUtils.isNotEmpty(dateString)) {
                try {
                    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(dateString);
                } catch (ParseException e) {
                    throw new IllegalStateException("Invalid date '" + dateString + "'.");
                }
            }
            return null;
        }

        /**
         * Creates an int from the given string with checking for null.
         * 
         * @param intString
         *            the int string.
         * @return int value from the string or -1
         */
        private int saveGetInt(final String intString) {
            if (StringUtils.isNotEmpty(intString)) {
                return Integer.parseInt(intString);
            }
            return -1;
        }

    }
}
