/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.components.global;

import org.apache.commons.lang.ArrayUtils;

import com.aditya.wp.aem.components.AbstractComponent;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class PageModifier extends AbstractComponent {

	private boolean isEmbedded;
	private boolean isContentOnly;

	@Override
	public void init() {
		String[] selectors = getRequest().getRequestPathInfo().getSelectors();
		this.isContentOnly = ArrayUtils.contains(selectors, "contentOnly");
		this.isEmbedded = ArrayUtils.contains(selectors, "embedded");
	}

	public boolean isEmbedded() {
		return this.isEmbedded;
	}

	public boolean isContentOnly() {
		return this.isContentOnly;
	}
	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.components.AbstractComponent#getResourceType()
	 */
	@Override
	public String getResourceType() {
		// TODO Auto-generated method stub
		return null;
	}
}
