/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.exception;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ContentMigrationException extends Exception {
    private static final Logger LOG = LoggerFactory.getLogger(ContentMigrationException.class);

    private static final long serialVersionUID = 1L;

    private final Node node;

    /**
     * Creates a new instance.
     * 
     * @param errorMessage
     *            the error message.
     */
    public ContentMigrationException(final String errorMessage) {
        super(errorMessage);
        this.node = null;
    }

    /**
     * Creates a new instance.
     * 
     * @param errorMessage
     *            the error message.
     * @param cause
     *            the root cause of the exception
     */
    public ContentMigrationException(final String errorMessage, final Throwable cause) {
        super(errorMessage, cause);
        this.node = null;
    }

    /**
     * Creates a new instance.
     * 
     * @param node
     *            the node on which the content migration failed
     * @param errorMessage
     *            the error message.
     */
    public ContentMigrationException(final Node node, final String errorMessage) {
        super(errorMessage);
        this.node = node;
    }

    /**
     * Creates a new instance.
     * 
     * @param node
     *            the node on which the content migration failed
     * @param errorMessage
     *            the error message.
     * @param cause
     *            the root cause of the exception
     */
    public ContentMigrationException(final Node node, final String errorMessage, final Throwable cause) {
        super(errorMessage, cause);
        this.node = node;
    }

    /**
     * Null-save method to retrieve the content path of the node where the exception occurred.
     * 
     * @return see above.
     */
    public final String getContentPath() {
        String path = "(none)";
        if (null != this.node) {
            this.node.toString();
            try {
                path = this.node.getPath();
            } catch (RepositoryException e) {
                LOG.warn(e.getMessage());
            }
        }
        return path;
    }
}
