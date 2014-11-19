/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface SystemInfo {

    /**
     * The different supported system types.
     */
    enum SystemType {
        LOCAL, REMOTE;
    }

    /**
     * The different status a system can have.
     */
    enum SystemStatus {
        OK, ERROR;
    }

    /**
     * Returns the type of the system.
     * 
     * @return see above.
     */
    SystemType getSystemType();

    /**
     * Returns an ID that identifies and describes the system.
     * 
     * @return see above.
     */
    String getSystemId();

    /**
     * Returns the base URL with which connections to the system can be created.
     * 
     * @return see above.
     */
    String getSystemBaseUrl();

    /**
     * Returns the status of the system.
     * 
     * @return see above.
     */
    SystemStatus getSystemStatus();
}
