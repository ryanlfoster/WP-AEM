/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.config;

import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface CookiePrivacyService {
    /**
     * Returns a JSON String with the names of all tracking providers with a certain status. The string contains the
     * names of all tracking providers that were selected in the Cookie Privacy section of the company template.
     * 
     * @param currentPage
     *            the current page
     * @param status
     *            the selection status
     * @return String JSON String of enabled tracking cookies
     */
    String getTrackingProvidersAsJSONString(final Page currentPage, final String status);

    /**
     * Checks if the inclusion of the tracking provider is always allowed.
     * 
     * @param currentPage
     *            the current page
     * @param trackingProvider
     *            the tracking provider
     * @return true, if is inclusion always allowed
     */
    boolean isInclusionAlwaysAllowed(final Page currentPage, final String trackingProvider);
}
