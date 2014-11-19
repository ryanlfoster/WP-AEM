/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration;

import java.util.Date;
import java.util.List;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface JobMetadata {
    /**
     * This enum contains all the different status a job can have.
     */
    public enum CurrentStatus {
        INACTIVE, PENDING, RUNNING, RUNNING_SIMULATE;
    };

    /**
     * Returns the job to which this meta-data belongs.
     * 
     * @return the job.
     */
    Job getJob();

    /**
     * Returns the current status of the job.
     * 
     * @return the status.
     */
    CurrentStatus getCurrentStatus();

    /**
     * Returns the date/time when this job has last been executed. May be null if the job has never been executed.
     * 
     * @return see above.
     */
    Date getLastExecutionTime();

    /**
     * Returns the date/time when this job has last been simulated. May be null if the job has never been simulated.
     * 
     * @return see above.
     */
    Date getLastSimulationTime();

    /**
     * Returns the number of occurrences for content-migration that have been fixed in the last run of this job. Will
     * return -1 if the job has not yet been executed.
     * 
     * @return see above.
     */
    int getFixedOccurrencesInLastRun();

    /**
     * Returns the number of occurrences for content-migration for which migration/fixing has failed during the last run
     * of this job. Will return -1 if the job has not yet been executed.
     * 
     * @return see above.
     */
    int getFailedOccurrencesInLastRun();

    /**
     * Returns the number of occurrences for content-migration that are still pending. A value of -1 indicates that this
     * value is currently unknown because the query for retrieving the occurrences has not yet been executed.
     * 
     * @return see above.
     */
    int getPendingOccurrences();

    /**
     * Returns a list of messages that deliver information about failed migration/fixing of occurrences for content
     * migration. If the job has not been executed so far, the list will be empty.
     * 
     * @return see above.
     */
    List<String> getErrorMessagesOfLastRun();

    /**
     * Returns a list of error messages that were collected during the last simulation run.
     * 
     * @return see above.
     */
    List<String> getErrorMessagesOfLastSimulation();

    /**
     * Returns information about the system on which the content migration job was executed.
     * 
     * @return see above.
     */
    SystemInfo getSystemInfo();

    /**
     * Returns the token for the current execution of this job. Will usually be null and only have a value if the job is
     * currently running or execution is pending.
     * 
     * @return see above.
     */
    String getToken();
}
