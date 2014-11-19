/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core;

import java.io.InputStream;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface ExportService {
    /**
     * Checks if a file exists on the remote machine.
     * 
     * @param path
     *            the file to check
     * @return true if the file exists, false if not
     */
    boolean fileExists(final String path);

    /**
     * Deploys a file to the remote machine.
     * 
     * @param fileStream
     *            the file
     * @param destination
     *            the destination
     */
    void putFile(final InputStream fileStream,
                 final String destination);

    /**
     * Removes a file from the remote machine.
     * 
     * @param filePath
     *            the path of the file to be deleted
     */
    void deleteFile(final String filePath);

    /**
     * Creates the given directory on the remote machine.
     * 
     * @param path
     *            the path of the directory to be created
     */
    void makeDirectory(final String path);

    /**
     * Method to find out of the service is properly configured or misconfigured.
     * 
     * @return true if misconfigured, false otherwise.
     */
    boolean isConfigured();
}
