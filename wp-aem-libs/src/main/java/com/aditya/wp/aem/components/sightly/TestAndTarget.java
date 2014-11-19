/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.components.sightly;

import com.aditya.gmwp.aem.services.tracking.TestAndTargetService;
import com.adobe.cq.sightly.WCMUse;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public class TestAndTarget extends WCMUse {
	private TestAndTargetService testAndTargetService;

	@Override
	public void activate() throws Exception {
		this.testAndTargetService = getSlingScriptHelper().getService(TestAndTargetService.class);
	}

	public boolean isEnabled() {
		return this.testAndTargetService.isEnabled(getCurrentPage());
	}

	public String getGlobalMboxCode() {
		return this.testAndTargetService.getGlobalMboxCode(getCurrentPage());
	}

	public String getEscapedGlobalMboxCode() {
		return this.testAndTargetService.getEscapedGlobalMboxCode(getCurrentPage());
	}
}
