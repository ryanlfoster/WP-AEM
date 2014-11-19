/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.contentmigration;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import com.aditya.gmwp.aem.exception.ContentMigrationException;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface Job {
    /**
     * Possibilities, how a job can be executed.
     */
    public enum ExecutionType {
        /**
         * This is the usual mode for a content migration. Should run once, when a new Job has been made available and
         * should fix the whole content.
         */
        AUTO_ONCE, //
        /**
         * Jobs with this execution type will be executed each time the content-migration service is started. Should
         * only be used for task that do not take a long time, e.g. for small consistency checks with automatic
         * corrections.
         */
        AUTO_EVERY_TIME, //
        /**
         * Manual execution mode. Since jobs with auto execution mode can also be executed manually, this is more or
         * less for testing.
         */
        MANUAL;
    };

    /**
     * Returns the execution type of this job.
     * 
     * @return the execution type.
     */
    ExecutionType getExecutionType();

    /**
     * Delivers the CRX query that identifies all content nodes that require migration.
     * 
     * @return the CRX query.
     * @throws RepositoryException
     *             when creating the query fails.
     */
    Query getQueryForMigrationNodes() throws RepositoryException;

    /**
     * Fixes the content in this node. This method will be called for each node that was found when the query that was
     * returned by {@link #getQueryForMigrationNodes()} was executed.
     * 
     * @param node
     *            the node to be fixed/migrated.
     * @throws ContentMigrationException
     *             when fixing/migrating the content fails.
     */
    void fixOccurrence(final Node node) throws ContentMigrationException;

    /**
     * Initializes this job.
     * 
     * @param session
     *            the CRX session.
     */
    void init(final Session session);
}
