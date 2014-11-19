/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.exception;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class DBCException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with null as its detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to Throwable.initCause(java.lang.Throwable)
     * 
     * @see java.lang.Throwable#initCause(Throwable)
     */
    public DBCException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified cause and a detail message of (cause==null ? null :
     * cause.toString()) (which typically contains the class and detail message of cause). This constructor is useful
     * for runtime exceptions that are little more than wrappers for other throwables.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the <code>java.lang.Throwable.getCause()</code>
     *            method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DBCException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to <code>java.lang.Throwable.initCause(java.lang.Throwable)<(code>.
     * 
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the
     *            <code>java.lang.Throwable.getMessage()</code> method.
     */
    public DBCException(final String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause. Note that the detail message
     * associated with cause is not automatically incorporated in this runtime exception's detail message.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the
     *            <code>java.lang.Throwable.getMessage()</code> method).
     * @param cause
     *            the cause (which is saved for later retrieval by the <code>java.lang.Throwable.getCause()</code>
     *            method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DBCException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
