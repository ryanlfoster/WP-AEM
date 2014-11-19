/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking;

import javax.servlet.http.HttpServletRequest;

import com.aditya.wp.aem.services.tracking.model.TrackingModel;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface OmnitureService {
    /**
     * The returned tracking model contains the page specific tracking information.
     * 
     * @param currentPage
     *            the current page
     * @param request
     *            the HttpServletRequest
     * @return the tracking model including the information for the tracking. Will never be null.
     */
    TrackingModel getTrackingModel(Page currentPage, HttpServletRequest request);

    /**
     * This method checks evars 25, 36 and 37 are excluded for a page or not.
     * 
     * @param page
     *            The page to look at
     * @return true if evar variables 25, 36 and 27 are excluded
     */
    boolean isEvar25And36And37Excluded(Page page);

    /**
     * This method checks whether full link tracking for omniture is enabled.
     * 
     * @param page
     *            The page to look at
     * @return true if movie tracking is enabled
     */
    boolean isFullLinkTrackingEnabled(Page page);

    /**
     * This method checks whether omniture is enabled for a page or not.
     * 
     * @param page
     *            The page to look at
     * @return omnitureEnabled {@link Boolean}
     */
    boolean isOmnitureEnabled(Page page);

    /**
     * This method checks whether advanced movie tracking is enabled for a page or not.
     * 
     * @param page
     *            The page to look at
     * @return true if full link tracking tracking is enabled
     */
    boolean isOmnitureMovieTrackingEnabled(Page page);

    /**
     * This method checks whether multimedia paragraph tracking is enabled for a page.
     * 
     * @param page
     *            the page
     * @return enabled
     */
    boolean isMultimediaParagraphTrackingEnabled(Page page);

    /**
     * Assembles the "page name" for the given page. The page name is not a name in the usual sense,
     * but rather a concatenation of different attributes, mostly shortened to two-letter codes.
     * Example: for the german index page at http://www.opel.de/, this attribute evaluates to
     * "op:eu:de:de:index".
     * 
     * @param page
     *            the page to evaluate for.
     * @return the computed page name attribute
     */
    String getOmniturePageName(Page page);
}
