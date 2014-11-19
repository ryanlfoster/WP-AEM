/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.exception;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class DdpSsiStatementException extends Exception {
    private static final long serialVersionUID = -9111434960465153870L;

    /**
     * Constructs a <code>DdpSsiStatementException</code> with no detail message.
     */
    public DdpSsiStatementException() {
        super();
    }

    /**
     * Constructs a <code>DdpSsiStatementException</code> with a detail message.
     * 
     * @param msg
     *            the detail message
     */
    public DdpSsiStatementException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>DdpSsiStatementException</code> with a detail message and the cause
     * {@link Throwable} object.
     * 
     * @param msg
     *            the detail message
     * @param t
     *            the cause {@link Throwable}
     */
    public DdpSsiStatementException(final String msg, final Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs a <code>DdpSsiStatementException</code> with the cause {@link Throwable} object.
     * 
     * @param t
     *            the cause {@link Throwable}
     */
    public DdpSsiStatementException(final Throwable t) {
        super(t);
    }
}
