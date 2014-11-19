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
public class VehicleDataException extends Exception {

    private static final long serialVersionUID = -5278360989901713160L;

    public VehicleDataException() {
        super();
    }

    public VehicleDataException(final String message) {
        super(message);
    }

    public VehicleDataException(final Throwable cause) {
        super(cause);
    }

    public VehicleDataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
