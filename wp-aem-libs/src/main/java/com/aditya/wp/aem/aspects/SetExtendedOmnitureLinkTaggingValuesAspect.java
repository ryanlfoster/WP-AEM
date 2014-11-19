/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.aspects;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.components.AbstractComponent;
import com.aditya.gmwp.aem.components.ComponentAspect;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class SetExtendedOmnitureLinkTaggingValuesAspect implements ComponentAspect {

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.components.ComponentAspect#applyAspect()
	 */
	@Override
	public void applyAspect() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.components.ComponentAspect#init(org.apache.sling.api.SlingHttpServletRequest, com.day.cq.wcm.api.Page, com.aditya.gmwp.aem.components.AbstractComponent)
	 */
	@Override
	public void init(SlingHttpServletRequest slingRequest,
	                 Page currentPage,
	                 AbstractComponent component) {
		// TODO Auto-generated method stub

	}

}
