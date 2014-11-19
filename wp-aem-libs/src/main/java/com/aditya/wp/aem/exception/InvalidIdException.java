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
public class InvalidIdException extends Exception {

    /**
     * generated uid.
     */
    private static final long serialVersionUID = 5085645158680480025L;

    /**
     * Constructor.
     */
    public InvalidIdException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     *            the error message
     * @param cause
     *            the cause
     */
    public InvalidIdException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param message
     *            the error message
     */
    public InvalidIdException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param cause
     *            the cause
     */
    public InvalidIdException(final Throwable cause) {
        super(cause);
    }
}
