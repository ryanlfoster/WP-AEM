/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;

import com.aditya.gmwp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface JcrService {
    /**
     * Login to the repository as administrator.
     * 
     * @return An administrative Session
     */
    Session getAdminSession();

    /**
     * Gets a ResourceResolver instance.
     * 
     * @return A ResourceResolver instance
     */
    ResourceResolver getResourceResolver();

    /**
     * Get a GMResource using the given path.
     * 
     * @param path
     *            the path
     * @return the gM resource
     */
    GMResource getGMResource(final String path);

    /**
     * Get the page manager.
     * 
     * @return the page manager.
     */
    PageManager getPageManager();
}
