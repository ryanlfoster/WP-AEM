/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface ErrorService {
    /**
     * Returns the absolute resource path, to which a redirect should happen.
     * 
     * @param request
     *            the HttpServletRequest, needed for Resource Resolver mapping.
     * @param currentPage
     *            the currentPage
     * @param resource
     *            the current Resource
     * @param errorCode
     *            the HTTP error code
     * @throws ParseException
     *             when a suitable error page cannot be found.
     * @return the absolute resource path.
     */
    String getErrorPath(HttpServletRequest request,
                        Page currentPage,
                        Resource resource,
                        int errorCode) throws ParseException;

    /**
     * Returns {@code true}, if the user is "anonymous".
     * 
     * @param request
     *            HttpServletRequest
     * @return true, if user is "anonymous"
     */
    boolean isAnonymousUser(HttpServletRequest request);

    /**
     * Returns {@code true}, if the request happens on an author instance.
     * 
     * @param request
     *            HttpServletRequest
     * @return true, if the request happens on an author instance.
     */
    boolean isAuthorInstance(HttpServletRequest request);

    /**
     * Returns {@code true}, if the request seems to be a human request. A request is human, when.
     * <ul>
     * <li>request uri ends with ".html" OR</li>
     * <li>request uri does not begin with "/content/dam" or "/dam" or "/var/dam"</li>
     * </ul>
     * 
     * @param request
     *            HttpServletRequest
     * @return true, if the request seems to be a human request.
     */
    boolean isHumanRequest(HttpServletRequest request);

    /**
     * Returns {@code true} if the simple error page should be used. The simple error page is a light error page and
     * just signalizes the Webserver that the requested page is not present, the Webserver should return a nice looking
     * error page instead. If the value is set to {@code false} the CQ instance should send a redirect to the error page
     * itself (see also GMDSPLM-8239). The value can be set in Felix Console.
     * 
     * @return whether the simple error page should be used or not
     */
    boolean isUseSimpleErrorPage();
}
