/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.components;

import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.wcm.api.Page;


/**
 * A poor-mans AOP approach to get things into components that are not their primary use. Implementing classes
 * have to be registered in AbstractComponent and will be applied if the concrete component class carries the
 * according annotation.
 * 
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public interface ComponentAspect {

    /**
     * Applies the aspect to the component.
     */
    void applyAspect();

    /**
     * Initializes this aspect.
     * 
     * @param slingRequest
     *            the request
     * @param currentPage
     *            the current page.
     * @param component
     *            the component instance.
     */
    void init(final SlingHttpServletRequest slingRequest,
              final Page currentPage,
              final AbstractComponent component);

}