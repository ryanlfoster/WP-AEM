/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.batchjob;

import java.util.List;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface BatchJobExecuter {
    /**
     * Add jobs to the BatchJobExecuter.
     * 
     * @param jobs
     *            a list of jobs
     */
    void execute(final List<Runnable> jobs);

    /**
     * Add jobs to the BatchJobExecuter.
     * 
     * @param jobs
     *            a list of jobs
     */
    void execute(final Runnable... jobs);
}
