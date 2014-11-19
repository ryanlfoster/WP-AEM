/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.jcr.Session;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.contentmigration.Job;
import com.aditya.gmwp.aem.services.contentmigration.JobMetadata;
import com.aditya.gmwp.aem.services.contentmigration.SystemInfo;
import com.aditya.gmwp.aem.services.contentmigration.utils.JobTokenUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
final class LocalJobQueue {
    private static final Logger LOG = LoggerFactory.getLogger(LocalJobQueue.class);

    private static final long QUEUE_PROCESSING_INTERVAL = 5L;

    public static final long MAX_AGE_OF_RESULTS = 1000L * 60L * 30L; // 1/2 hour.

    private final Queue<JobExecutionData> queue = new LinkedList<JobExecutionData>();

    private final Map<String, JobResultMetadata> results = new HashMap<String, JobResultMetadata>();

    private JobExecutionData currentRunning;

    private final JobExecutor jobExecuter;

    private final SystemInfo systemInfo;

    /**
     * Creates a new instance.
     * 
     * @param session
     *            the crx session to work with.
     * @param scheduler
     *            the scheduler.
     * @param localSystemInfo
     *            info about the (local) system.
     */
    LocalJobQueue(final Session session, final Scheduler scheduler, final SystemInfo localSystemInfo) {
        this.jobExecuter = new JobExecutor(session);
        this.systemInfo = localSystemInfo;
        final QueueProcessor queueProcessor = new QueueProcessor();
        try {
        	final ScheduleOptions settings = scheduler.AT(new Date(), -1, QUEUE_PROCESSING_INTERVAL).name(queueProcessor.getClass().getName()).canRunConcurrently(false);
            scheduler.schedule(queueProcessor, settings);
        } catch (Exception e) {
            throw new IllegalStateException("QueueProcessor job could not be scheduled.");
        }
    }

    /**
     * Adds a job to the queue for execution.
     * 
     * @param job
     *            the job to be executed.
     * @param simulate
     *            whether to simulate or not.
     * @return a token which can be used to fetch the results of the asynchronous invocation later.
     */
    String addJob(final Job job,
                  final boolean simulate) {
        String token = null;
        synchronized (this.queue) {
            for (JobExecutionData data : this.queue) {
                if (data.getJob().equals(job) && data.getSimulate() == simulate) {
                    throw new IllegalStateException("Job '" + job.getClass().getName()
                            + "' is already in queue for execution.");
                }
            }
            if (null != this.currentRunning && this.currentRunning.job.equals(job)
                    && this.currentRunning.getSimulate() == simulate) {
                throw new IllegalStateException("Job '" + job.getClass().getName()
                        + "' is already in queue for execution.");
            }
            token = JobTokenUtil.getInstance().createToken(job, this.systemInfo, simulate);
            this.queue.offer(new JobExecutionData(job, simulate, token));
        }
        return token;
    }

    /**
     * Returns the result of the execution with the given token. If the token is unknown, illegal argument exception
     * will be thrown.
     * 
     * @param token
     *            the token.
     * @return results or null, if the execution has not yet finished.
     */
    JobMetadata getExecutionResults(final String token) {
        JobMetadata result = null;
        synchronized (this.results) {
            final JobResultMetadata jrmd = this.results.get(token);
            if (null != jrmd) {
                result = jrmd.getMetaData();
            }
        }
        if (null != result) {
            return result;
        }
        // Check if the job is pending or currently running:
        synchronized (this.queue) {
            Job pendingJob = null;
            for (JobExecutionData jobExecData : this.queue) {
                if (jobExecData.getToken().equals(token)) {
                    pendingJob = jobExecData.getJob();
                    break;
                }
            }
            if (null != pendingJob) {
                return new JobMetadataImpl(pendingJob, token);
            } else if (null != this.currentRunning && this.currentRunning.getToken().equals(token)) {
                return new JobMetadataImpl(this.currentRunning.getJob(), token);
            }
        }
        throw new IllegalArgumentException("Unknown job execution token '" + token + "'.");
    }

    /**
     * Simple bean to wrap information about execution of a job.
     */
    private static final class JobExecutionData {

        private final Job job;

        private final boolean simulate;

        private final String token;

        /**
         * Creates a new instance.
         * 
         * @param job
         *            the job
         * @param simulate
         *            whether to simulate.
         * @param token
         *            the token that identifies this execution.
         */
        private JobExecutionData(final Job job, final boolean simulate, final String token) {
            this.job = job;
            this.simulate = simulate;
            this.token = token;
        }

        /**
         * Returns the job.
         * 
         * @return see above.
         */
        public Job getJob() {
            return this.job;
        }

        /**
         * Returns whether to simulate or not.
         * 
         * @return see above.
         */
        public boolean getSimulate() {
            return this.simulate;
        }

        /**
         * Returns the token that identifies this execution.
         * 
         * @return see above.
         */
        public String getToken() {
            return this.token;
        }
    }

    /**
     * Simple wrapper class for data that is relevant after a job was finished.
     */
    public static final class JobResultMetadata {

        private final JobMetadata metaData;

        /**
         * Returns the meta data.
         * 
         * @return see above.
         */
        public JobMetadata getMetaData() {
            return this.metaData;
        }

        /**
         * Returns the time when the job was finished.
         * 
         * @return see above.
         */
        public Date getFinishDate() {
            if (this.finishDate == null) {
                return null;
            }
            return new Date(this.finishDate.getTime());
        }

        private final Date finishDate;

        /**
         * Creates a new instance.
         * 
         * @param metaData
         *            the meta data
         * @param finishDate
         *            the time when the job was finished.
         */
        public JobResultMetadata(final JobMetadata metaData, final Date finishDate) {
            super();
            this.metaData = metaData;
            this.finishDate = finishDate != null ? new Date(finishDate.getTime()) : null;
        }
    }

    /**
     * A thread-like thing that checks if the job execution queue contains entries and if it does, executes the first
     * entry.
     */
    private class QueueProcessor implements Runnable {

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            JobExecutionData jobExecData = null;
            synchronized (LocalJobQueue.this.queue) {
                if (null != LocalJobQueue.this.queue.peek()) {
                    jobExecData = LocalJobQueue.this.queue.poll();
                    LocalJobQueue.this.currentRunning = jobExecData;
                }
            }
            if (null != jobExecData) {
                JobMetadata metaData = null;
                try {
                    synchronized (LocalJobQueue.this.queue) {
                        LocalJobQueue.this.currentRunning = jobExecData;
                    }
                    metaData = LocalJobQueue.this.jobExecuter.executeJob(jobExecData.getJob(),
                            jobExecData.getSimulate());
                    ((JobMetadataImpl) metaData).setToken(jobExecData.getToken());
                    synchronized (LocalJobQueue.this.queue) {
                        LocalJobQueue.this.results.put(jobExecData.getToken(), new JobResultMetadata(metaData,
                                new Date()));
                    }
                } catch (Exception e) {
                    LOG.error("Error while executing job '" + jobExecData.getJob().getClass().getName() + "': ", e);
                } finally {
                    synchronized (LocalJobQueue.this.queue) {
                        LocalJobQueue.this.currentRunning = null;
                    }
                }
            }
            // Do some cleanup: Remove old entries from list.
            synchronized (LocalJobQueue.this.results) {
                final Date now = new Date();
                final List<String> doomedEntries = new ArrayList<String>();
                for (Map.Entry<String, JobResultMetadata> entry : LocalJobQueue.this.results.entrySet()) {
                    if (entry.getValue().getFinishDate().getTime() + MAX_AGE_OF_RESULTS < now.getTime()) {
                        doomedEntries.add(entry.getKey());
                    }
                }
            }
        }
    }

    /**
     * Gets the status of the current job.
     * 
     * @param job
     *            the job
     * @return meta data about the job. Only the attributes "job", "currentStatus" and "token" are set to useful values.
     */
    public final JobMetadata getJobStatus(final Job job) {
        final JobMetadataImpl metaData = new JobMetadataImpl(job);
        synchronized (this.queue) {
            if (null != this.currentRunning && this.currentRunning.getJob().equals(job)) {
                metaData.setToken(this.currentRunning.getToken());
                if (this.currentRunning.getSimulate()) {
                    metaData.setCurrentStatus(JobMetadata.CurrentStatus.RUNNING_SIMULATE);
                } else {
                    metaData.setCurrentStatus(JobMetadata.CurrentStatus.RUNNING);
                }
            } else {
                for (JobExecutionData jobExecData : this.queue) {
                    if (jobExecData.getJob().equals(job)) {
                        metaData.setCurrentStatus(JobMetadata.CurrentStatus.PENDING);
                        metaData.setToken(jobExecData.getToken());
                        break;
                    }
                }
            }
        }
        return metaData;
    }
}
