/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.wcm.api.WCMMode;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class WCMModeUtil {

    /**
     * Check if the current instance is the author instance.
     * 
     * @param request
     *            the SlingHttpServletRequest
     * @return true, if the current instance is the author instance.
     */
    public static boolean isAuthorInstance(final SlingHttpServletRequest request) {
        final Object isAuthorInstanceObject = request.getAttribute("isAuthorInstance");
        if (isAuthorInstanceObject instanceof Boolean) {
            return (Boolean) isAuthorInstanceObject;
        } else {
            return !WCMMode.DISABLED.equals(WCMMode.fromRequest(request));
        }
    }

    /**
     * Check if the current instance is the author instance.
     * 
     * @param request
     *            the HttpServletRequest
     * @return true, if the current instance is the author instance.
     */
    public static boolean isAuthorInstance(final HttpServletRequest request) {
    	return !WCMMode.DISABLED.equals(WCMMode.fromRequest(request));
    }

    /**
     * Check if the current wcm mode is the author mode.
     * 
     * @param request
     *            the SlingHttpServletRequest
     * @return true, if the current wcm mode is the author mode.
     */
    public static boolean isAuthorMode(final SlingHttpServletRequest request) {
        return WCMMode.EDIT.equals(WCMMode.fromRequest(request)) || WCMMode.DESIGN.equals(WCMMode.fromRequest(request));
    }

    /**
     * private constructor for util classes with static methods only.
     */
    private WCMModeUtil() {
        throw new AssertionError("This class is not meant to be instantiated.");
    }
}
