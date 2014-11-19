/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.contentmigration;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface ContentMigrationService {
    /**
     * Returns a list of all jobs that can be executed.
     * 
     * @return list of all available job.
     */
    List<Job> getAllJobs();

    /**
     * Executes the given job. The execution will happen asynchronously.
     * 
     * @param job
     *            the job to be executed.
     * @param system
     *            the system on which the job has to be executed.
     * @param simulate
     *            whether the actual changes to the content should only be simulated. The system will still iterate over
     *            all hits that are found by the migration jobs query. Can be used to determine the number of hits that
     *            would be affected.
     * @return a token that can be used to fetch the data/information that is produced in this asynchronous call.
     */
    String executeJob(final Job job,
                      final SystemInfo system,
                      boolean simulate);

    /**
     * Delivers meta-data for the given content migration job that has been executed on a given system. The number of
     * pending occurrences will always be -1 if this (synchronous) method is used.
     * 
     * @param job
     *            the job for which meta data shall be retrieved.
     * @param system
     *            the system on which the job was executed.
     * @return see above.
     * @throws RepositoryException
     *             when reading meta-data from the repository fails.
     */
    JobMetadata getMetaDataForJob(Job job,
                                  SystemInfo system) throws RepositoryException;

    /**
     * Returns meta data for all content migration jobs on all relevant systems. The number of pending occurrences will
     * always be -1 if this (synchronous) method is used.
     * 
     * @return see above.
     * @throws RepositoryException
     *             when reading from the CRX fails.
     */
    Map<Job, List<JobMetadata>> getAllMetaData() throws RepositoryException;

    /**
     * This method returns the result of an asynchronous call. The token identifies the data to be fetched. If the token
     * is unknown, IllegalArgumentException will be thrown.
     * 
     * @param token
     *            the token that was previously delivered by one of the asynchronous methods.
     * @return the result if the asynchronous call was finished or null if the call was not yet finished.
     */
    JobMetadata getResult(String token);

    /**
     * Returns a list of all systems on which content migration jobs can be executed.
     * 
     * @return see above.
     */
    List<SystemInfo> getAllSystems();
}
