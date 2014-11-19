/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.contentmigration.utils;

import com.aditya.gmwp.aem.services.contentmigration.Job;
import com.aditya.gmwp.aem.services.contentmigration.SystemInfo;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class JobTokenUtil {

    private static JobTokenUtil mySelf;

    private final SystemInfo[] systems;

    private final Job[] jobs;

    private String lastTimestamp;

    /**
     * Creates the singleton instance.
     * 
     * @param jobs
     *            all known jobs.
     * @param systems
     *            all known systems.
     */
    public static void createInstance(final Job[] jobs,
                               final SystemInfo[] systems) {
        mySelf = new JobTokenUtil(jobs.clone(), systems.clone());
    }

    /**
     * Returns the instance.
     * 
     * @return the instance.
     */
    public static JobTokenUtil getInstance() {
        return mySelf;
    }

    /**
     * Private constructor, this is a singleton.
     * 
     * @param jobs
     *            the jobs
     * @param systems
     *            the systems
     */
    private JobTokenUtil(final Job[] jobs, final SystemInfo[] systems) {
        this.jobs = jobs.clone();
        this.systems = systems.clone();
    }

    /**
     * Generates a unique token for a job.
     * 
     * @param job
     *            the job
     * @param systemInfo
     *            the system
     * @param simulate
     *            whether the execution is only simulated.
     * @return a token.
     */
    public String createToken(final Job job,
                       final SystemInfo systemInfo,
                       final boolean simulate) {
        final StringBuilder builder = new StringBuilder();
        builder.append(job.getClass().getName());

        builder.append("@");
        builder.append(systemInfo.getSystemId());
        if (simulate) {
            builder.append("(simulate)");
        }
        String timestamp = "" + System.currentTimeMillis();
        synchronized (this) {
            if (timestamp.equals(this.lastTimestamp)) {
                timestamp = timestamp + "x";
            }
            this.lastTimestamp = timestamp;
        }
        builder.append(timestamp);
        return builder.toString();
    }

    /**
     * Determines the system from the token.
     * 
     * @param token
     *            the token
     * @return the system on which the token was created.
     */
    public SystemInfo getSystemFromToken(final String token) {
        for (SystemInfo system : this.systems) {
            if (token.contains("@" + system.getSystemId())) {
                return system;
            }
        }
        throw new IllegalArgumentException("The system which is specified in token '" + token + "' is unkown!");
    }

    /**
     * Determines the job from the token.
     * 
     * @param token
     *            the token
     * @return the job for which the token was created.
     */
    public Job getJobFromToken(final String token) {
        for (Job job : this.jobs) {
            if (token.startsWith(job.getClass().getName() + "")) {
                return job;
            }
        }
        throw new IllegalArgumentException("The job which is specified in token '" + token + "' is unkown!");
    }
}