/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.aspects;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.annotations.PageAreaComponent;
import com.aditya.wp.aem.components.AbstractComponent;
import com.aditya.wp.aem.components.ComponentAspect;
import com.aditya.wp.aem.global.GmdsRequestAttribute;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class SetPageAreaNameAspect implements ComponentAspect {

    /**
     * This method determines how deep the current component is below the page-level in the content-structure.
     * 
     * @param path
     *            the crx-path.
     * @return see method description.
     */
    static int determineComponentLevel(final String path) {
        int index = path.indexOf(JcrConstants.JCR_CONTENT);
        if (index != -1) {
            index += (JcrConstants.JCR_CONTENT + "/").length();
            if (path.length() > index) {
                return path.substring(index).split("/").length;
            } else {
                return 0;
            }
        }
        throw new ComponentAspectException("Unable to determine the level of the current component in CRX path '"//
                + path + "'.");
    }

    private AbstractComponent component;

    private SlingHttpServletRequest request;

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.components.ComponentAspect#applyAspect()
	 */
	@Override
	public void applyAspect() {
        final int componentLevel = determineComponentLevel(this.component.getResource().getPath());
        GmdsRequestAttribute.CURRENT_PAGE_AREA.set(this.request, this.component.getClass().getAnnotation(PageAreaComponent.class).area());
        GmdsRequestAttribute.CURRENT_PAGE_AREA_DEFINED_BY_LEVEL.set(this.request, componentLevel);
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
