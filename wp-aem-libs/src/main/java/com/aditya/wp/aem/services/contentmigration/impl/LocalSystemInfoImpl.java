/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.contentmigration.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.aditya.gmwp.aem.services.contentmigration.SystemInfo;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
final class LocalSystemInfoImpl implements SystemInfo {

    private final String localSystemId;

    @Override
    public final SystemType getSystemType() {
        return SystemType.LOCAL;
    }

    @Override
    public final String getSystemId() {
        return this.localSystemId;
    }

    @Override
    public final String getSystemBaseUrl() {
        throw new UnsupportedOperationException("Getting URL not supported on local system.");
    }

    @Override
    public final SystemStatus getSystemStatus() {
        return SystemStatus.OK;
    }

    /**
     * Creates a new instance.
     * 
     * @param localSystemId
     *            the ID of the local system.
     */
    LocalSystemInfoImpl(final String localSystemId) {
        this.localSystemId = localSystemId;
    }

    /**
     * Reads and returns the host-name of the local system.
     * 
     * @return see above.
     */
    static String getLocalHostname() {
        try {
            final InetAddress localMachine = java.net.InetAddress.getLocalHost();
            return localMachine.getHostName();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Unable to determine host-name.", e);
        }
    }
}