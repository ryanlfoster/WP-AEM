/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.data;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum OmnitureVariables {
	CHANNEL("channel", "Channel"), //
	EVAR01("eVar1", "Internal Promotion, set by Author"), //
	EVAR02("eVar2", "Lead type, set by Author"), //
	EVAR04("eVar4", "Selected Language"), //
	EVAR05("eVar5", "Model"), //
	EVAR06("eVar6", "Bodystyle"), //
	EVAR07("eVar7", "Vehicle Type"), //
	EVAR16("eVar16", "Form name"), //
	EVAR17("eVar17", "ISO countrycode"), //
	EVAR18("eVar18", "Brand"), //
	EVAR24("eVar24", "Special sales offer, set by Auhor"), //
	EVAR25("eVar25", "Site-to-site"), //
	EVAR27("eVar27", "Carline Title"), //
	EVAR28("eVar28", "Model Year"), //
	EVAR30("eVar30", "Search Term"), //
	EVAR31("eVar31", "Country"), //
	EVAR32("eVar32", "Region"), //
	EVAR34("eVar34", "Satisfaction Score"), //
	EVAR38("eVar38", "iPerception Specific Survey Trkg"), //
	EVAR57("eVar57", "Page Name"), //
	EVAR58("eVar58", "Component:Container"), //
	EVAR75("eVar75", "Image / Teaser Name"), //
	EVENTS("events", "Omniture Events"), //
	HIER1("hier1", "Site Hierarchy, like a breadcrumb"), //
	LINKNAME("linkname", "Name of the link"), //
	PAGENAME("pageName", "Name of the page"), //
	PAGETYPE("pageType", "Page type"), //
	PE("pe", "Link Type"), //
	PEV2("pev2", "Link Name"), //
	PROP01("prop1", "Model"), //
	PROP02("prop2", "Bodystyle"), //
	PROP03("prop3", "Vehicle Type"), //
	PROP09("prop9", "CRM Form Name"), //
	PROP10("prop10", "Site Section Level 1"), //
	PROP11("prop11", "Site Section Level 2"), //
	PROP12("prop12", "Site Section Level 3"), //
	PROP13("prop13", "Site Section Level 4"), //
	PROP14("prop14", "Site Section Level 5"), //
	PROP15("prop15", "Lead Type"), //
	PROP16("prop16", "Application Name"), //
	PROP17("prop17", "Microsite Name"), //
	PROP18("prop18", "Brand"), //
	PROP23("prop23", "Selected Language"), //
	PROP24("prop24", "Homepage Banner / Teaser"), //
	PROP27("prop27", "Page Area : Title"), //
	PROP29("prop29", "Video Tracking"), //
	PROP30("prop30", "Communication Type"), //
	PROP31("prop31", "Survey ID"), //
	PROP33("prop33", "Concatenation: corsavan | vehicles (model | site section)"), //
	PROP34("prop34", "Concatenation: opel-range | highlights (site section | active state)"), //
	PROP35("prop35", "Concatenation: agila | opel-range | highlights (model |site section |active state)"), //
	PROP36("prop36", "Search term"), //
	PROP41("prop41", "Lightbox Item Page Name"), //
	PROP47("prop47", "Search result count"), //
	PROP59("prop59", "Big Image Name"), //
	PROP32("prop32", "prop32");

	private final String variableName;

	private final String description;

	/**
	 * Constructor for an omniture variable.
	 * 
	 * @param variableName
	 *            The javascript variable name of the omniture object that the
	 *            value will be asinged to.
	 * @param description
	 *            Description of what value should be saved into this variable
	 *            (e.g. used for logging and debugging)
	 */
	private OmnitureVariables(final String variableName,
			final String description) {
		this.variableName = variableName;
		this.description = description;
	}

	/**
	 * Returns the javascript variable name.
	 * 
	 * @return the javaScriptVariableName
	 */
	public final String getJavaScriptVariableName() {
		return this.variableName;
	}

	/**
	 * Returns the variable description.
	 * 
	 * @return the variableDescription
	 */
	public final String getVariableDescription() {
		return this.description;
	}
}
