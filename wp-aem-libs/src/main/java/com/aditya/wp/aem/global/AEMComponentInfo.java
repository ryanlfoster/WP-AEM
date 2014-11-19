/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.global;



/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum AEMComponentInfo {

	COMPONENT_CNT_WELL_C1("gmwp/components/content/cnt_well_c1"),
	COMPONENT_CNT_WELL_C2("gmwp/components/content/cnt_well_c2"),
	COMPONENT_CNT_GLOSSARY_ITEM("gmwp/components/content/cnt_glossary_item_c1");

	private static final String COMPONENT_PATH = "/apps/";

	private final String componentName;
	private final boolean isContainer;

	private AEMComponentInfo(final String componentName) {
		this.componentName = componentName;
		this.isContainer = false;
	}

	private AEMComponentInfo(final boolean isContainer, final String componentName) {
		this.componentName = componentName;
		this.isContainer = isContainer;
	}

	public String getComponentName() {
		return this.componentName;
	}
	
	public boolean getIsContainer() {
		return this.isContainer;
	}

	public String getComponentPath() {
		return COMPONENT_PATH + this.componentName;
	}
}
