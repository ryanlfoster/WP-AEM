/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.exception.ContentMigrationException;
import com.aditya.gmwp.aem.services.contentmigration.Job;
import com.aditya.gmwp.aem.services.contentmigration.JobMetadata;
import com.aditya.gmwp.aem.services.contentmigration.utils.ContentMigrationCrxUtil;
import com.aditya.gmwp.aem.utils.NodeUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class JobExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutor.class);

    private final Session session;

    /**
     * Creates a new instance.
     * 
     * @param session
     *            the crx session to work with.
     */
    public JobExecutor(final Session session) {
        this.session = session;
    }

    /**
     * This method will execute the job synchronously. The invoker should deal with the asynchronous aspect of executing
     * jobs.
     * 
     * @param job
     *            the job
     * @param simulate
     *            whether the execution should only be simulated.
     * @return job meta data describing the outcome of the job.
     */
    public final JobMetadata executeJob(final Job job,
                                        final boolean simulate) {
        JobMetadataImpl metaData = new JobMetadataImpl(job);
        try {
            final StopWatch stopwatch = new StopWatch();
            job.init(this.session);
            final Query query = job.getQueryForMigrationNodes();
            query.setLimit(Long.MAX_VALUE);
            stopwatch.start();
            final QueryResult result = query.execute();
            stopwatch.split();
            LOG.info("Execution of query for content-migration-job '" + job.getClass().getName() + "' took "
                    + stopwatch.getSplitTime() + "ms.");
            final RowIterator rowIter = result.getRows();
            int totalCount = 0;
            int successCount = 0;
            int failedCount = 0;
            final List<String> messages = new ArrayList<String>();
            final Map<Lock, String> lockPathMap = new HashMap<Lock, String>();
            final LockManager adminLocker = this.session.getWorkspace().getLockManager();
            while (rowIter.hasNext()) {
                final Row row = rowIter.nextRow();
                try {
                    if (!simulate) {
                        final String nodePath = NodeUtil.getPageNode(row.getNode()).getPath();
                        if (adminLocker.isLocked(nodePath)) {
                            final String lockOwner = NodeUtil.getLockOwner(row.getNode());
                            Session impersonatedSession = getUserSession(lockOwner);
                            final LockManager userLocker = impersonatedSession.getWorkspace().getLockManager();
                            final Lock lock = userLocker.getLock(nodePath);

                            if (lock != null && lock.isLive()) {
                                userLocker.unlock(lock.getNode().getPath());
                                lockPathMap.put(lock, nodePath);
                                impersonatedSession.logout();
                                impersonatedSession = null;
                            }
                        }
                        job.fixOccurrence(row.getNode());
                    }
                    successCount++;

                } catch (final ContentMigrationException e) {
                    final String message = "Error while migrating occurrence " + totalCount + " / "
                            + e.getContentPath() + ":" + e.getMessage();
                    LOG.error(message);
                    messages.add(message);
                    failedCount++;
                } catch (final Exception e) {
                    final String message = "Unforseen error while migrating occurrence " + totalCount + " / "
                            + row.getNode().getPath() + ":" + e.getMessage()
                            + " (this might be a bug or missing exception handling in the migration script.)";
                    LOG.error(message);
                    messages.add(message);
                    failedCount++;
                } finally {
                    totalCount++;
                }

                // save every 1000 items
                if (!simulate && totalCount % 1000 == 0) {
                    LOG.info("Saving changes to CRX...");
                    this.session.save();

                }

                if (totalCount % 10 == 0) {
                    stopwatch.split();
                    LOG.info(totalCount + " occurrences have been processed (time used: " + stopwatch.getSplitTime() + ")");
                }
            }
            if (!simulate) {
                this.session.save();
                LOG.info("Saving changes to CRX...");
                reLockNodes(lockPathMap);
            }

            stopwatch.stop();
            if (simulate) {
                metaData.setLastSimulationTime(new Date());
                metaData.setErrorMessagesSimulation(messages);
            } else {
                metaData.setLastExecutionTime(new Date());
                metaData.setErrorMessages(messages);
                metaData.setFixedOccurrencesInLastRun(successCount);
                metaData.setFailedOccurrencesInLastRun(failedCount);
            }

            LOG.info("Processing all occurrences (" + totalCount + ") for content-migration-job '" + job.getClass().getName() + "' took " + stopwatch.getTime() + "ms.");

            // Store the data produced in this run...
            ContentMigrationCrxUtil.storeMetaDataForJob(this.session, metaData, simulate);
            // Load again to get data produced in previous runs:
            metaData = ContentMigrationCrxUtil.getMetaDataForJob(this.session, job);
            // Since pendingOccurrences is not persisted, fill it in again.
            if (simulate) {
                metaData.setPendingOccurrences(totalCount);
            }
        } catch (final RepositoryException e) {
            final String message = "RepositoryException was thrown while executing content-migration-job '"
                    + job.getClass().getName() + "': " + e.toString();
            LOG.error(message);
            final List<String> messages = new ArrayList<String>();
            messages.add(message);
            if (simulate) {
                metaData.setErrorMessagesSimulation(messages);
            } else {
                metaData.setErrorMessages(messages);
            }
        }
        return metaData;
    }

    /**
     * locks the Nodes which were unlocked for migrating.
     * 
     * @param simulate
     * @param unlocked
     * @throws RepositoryException
     */
    private void reLockNodes(final Map<Lock, String> unlocked) throws RepositoryException {
        for (final Map.Entry<Lock, String> lock : unlocked.entrySet()) {
            final String lockOwner = lock.getKey().getLockOwner();
            Session impersonatedSession = getUserSession(lockOwner);
            final LockManager userlocker = impersonatedSession.getWorkspace().getLockManager();
            userlocker.lock(lock.getValue(), lock.getKey().isDeep(), false, Long.MAX_VALUE, lock.getKey().getLockOwner());
            impersonatedSession.logout();
            impersonatedSession = null;
        }
        unlocked.clear();
    }

    /**
     * Gets the parent node of the given Node.
     * 
     * @param lockOwner
     *            as String
     * @return Session
     * @throws RepositoryException
     */
    private Session getUserSession(final String lockOwner) throws RepositoryException {
        return this.session.impersonate(new SimpleCredentials(lockOwner, "".toCharArray()));
    }
}