/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.aspects;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.components.AbstractComponent;
import com.aditya.wp.aem.components.ComponentAspect;
import com.aditya.wp.aem.global.GmdsRequestAttribute;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class UnsetPageAreaNameAspect implements ComponentAspect {

    private AbstractComponent component;

    private SlingHttpServletRequest request;

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.components.ComponentAspect#applyAspect()
	 */
	@Override
	public void applyAspect() {
        final int componentLevel = SetPageAreaNameAspect.determineComponentLevel(this.component.getResource().getPath());
        Integer pageAreaDefinedByLevel = (Integer) GmdsRequestAttribute.CURRENT_PAGE_AREA_DEFINED_BY_LEVEL.get(this.request);
        if (null == pageAreaDefinedByLevel) {
            pageAreaDefinedByLevel = 0;
        }
        if (componentLevel < pageAreaDefinedByLevel.intValue()
                || (componentLevel == pageAreaDefinedByLevel.intValue() && (componentLevel != 0 && pageAreaDefinedByLevel.intValue() != 0))) {
            String mainArea = (String) GmdsRequestAttribute.CURRENT_PAGE_MAIN_AREA.get(this.request);
            if (null == mainArea) {
                mainArea = "<unknown>";
            }
            GmdsRequestAttribute.CURRENT_PAGE_AREA.set(this.request, mainArea);
        }
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.components.ComponentAspect#init(org.apache.sling.api.SlingHttpServletRequest, com.day.cq.wcm.api.Page, com.aditya.wp.aem.components.AbstractComponent)
	 */
	@Override
	public void init(final SlingHttpServletRequest slingRequest,
	                 final Page currentPage,
	                 final AbstractComponent component) {
        this.request = slingRequest;
        this.component = component;
	}

}
