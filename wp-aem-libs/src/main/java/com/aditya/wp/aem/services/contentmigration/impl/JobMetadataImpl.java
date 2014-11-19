/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.aditya.wp.aem.services.contentmigration.Job;
import com.aditya.wp.aem.services.contentmigration.JobMetadata;
import com.aditya.wp.aem.services.contentmigration.SystemInfo;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class JobMetadataImpl implements JobMetadata {
    private final Job job;
    private CurrentStatus currentStatus = CurrentStatus.INACTIVE;
    private Date lastExecutionTime;
    private Date lastSimulationTime;
    private int fixedOccurrencesInLastRun = -1;
    private int failedOccurrencesInLastRun = -1;
    private int pendingOccurrences = -1;
    private List<String> errorMessages = Collections.emptyList();
    private List<String> errorMessagesSimulation = Collections.emptyList();
    private SystemInfo systemInfo;
    private String token;

    /**
     * Sets the system info.
     * 
     * @param systemInfo
     *            the system info
     */
    public final void setSystemInfo(final SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    /**
     * Sets the current status.
     * 
     * @param currentStatus
     *            the current status
     */
    public void setCurrentStatus(final CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    /**
     * Sets the time of the last execution.
     * 
     * @param lastExecutionTime
     *            last execution time.
     */
    public void setLastExecutionTime(final Date lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    /**
     * Sets the time of the last simulation.
     * 
     * @param lastSimulationTime
     *            last simulation time.
     */
    public void setLastSimulationTime(final Date lastSimulationTime) {
        this.lastSimulationTime = lastSimulationTime;
    }

    /**
     * Sets the number of occurrences that have been fixed in the last run.
     * 
     * @param fixedOccurrencesInLastRun
     *            see above.
     */
    public void setFixedOccurrencesInLastRun(final int fixedOccurrencesInLastRun) {
        this.fixedOccurrencesInLastRun = fixedOccurrencesInLastRun;
    }

    /**
     * Sets the number of occurrences that have failed in the last run.
     * 
     * @param failedOccurrencesInLastRun
     *            see above.
     */
    public void setFailedOccurrencesInLastRun(final int failedOccurrencesInLastRun) {
        this.failedOccurrencesInLastRun = failedOccurrencesInLastRun;
    }

    /**
     * Sets the number of occurrences that are pending to be fixed.
     * 
     * @param pendingOccurrences
     *            see above.
     */
    public void setPendingOccurrences(final int pendingOccurrences) {
        this.pendingOccurrences = pendingOccurrences;
    }

    /**
     * Sets the error messages that were collected during the last execution.
     * 
     * @param errorMessages
     *            the error messages
     */
    public void setErrorMessages(final List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    /**
     * Sets the error messages that were collected during the last simulation.
     * 
     * @param errorMessages
     *            the error messages
     */
    public void setErrorMessagesSimulation(final List<String> errorMessages) {
        this.errorMessagesSimulation = errorMessages;
    }

    /**
     * Sets the token.
     * 
     * @param token
     *            the token.
     */
    public void setToken(final String token) {
        this.token = token;
    }

    /**
     * Creates a new instance.
     * 
     * @param job
     *            the job.
     */
    public JobMetadataImpl(final Job job) {
        this.job = job;
    }

    /**
     * Creates a new instance.
     * 
     * @param job
     *            the job
     * @param token
     *            the token
     */
    public JobMetadataImpl(final Job job, final String token) {
        this(job);
        this.token = token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Job getJob() {
        return this.job;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CurrentStatus getCurrentStatus() {
        return this.currentStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Date getLastExecutionTime() {
        return this.lastExecutionTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getFixedOccurrencesInLastRun() {
        return this.fixedOccurrencesInLastRun;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getFailedOccurrencesInLastRun() {
        return this.failedOccurrencesInLastRun;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getPendingOccurrences() {
        return this.pendingOccurrences;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> getErrorMessagesOfLastRun() {
        return this.errorMessages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SystemInfo getSystemInfo() {
        return this.systemInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastSimulationTime() {
        return this.lastSimulationTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getErrorMessagesOfLastSimulation() {
        return this.errorMessagesSimulation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToken() {
        return this.token;
    }
}
