/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.properties;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;

import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.utils.diff.DiffUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum FeaturesAndSpecsConfigProperties implements LSLRConfigProperties {
	STATUS_STANDARD("features_and_specs_config_c1/status_standard"),
	STATUS_AVAILABLE("features_and_specs_config_c1/status_available"),
	STATUS_NOT_AVAILABLE("features_and_specs_config_c1/status_notavailable"),
	VIEW_DIFFS_LABEL("features_and_specs_config_c1/viewDiffsLabel"),
	TABS_HEADLINE_LABEL("features_and_specs_config_c1/tabs_headline"),
	EXPAND_ALL_LABEL("features_and_specs_config_c1/expandAll"),
	CLOSE_ALL_LABEL("features_and_specs_config_c1/closeAll"),
	USE_VEHICLE_TITLE("features_and_specs_config_c1/enableVehicleTitle"),
	COMPARISON_TAB_LABEL("features_and_specs_config_c1/featureComparisonTabLabel"),
	CAPABILITIES_TAB_LABEL("features_and_specs_config_c1/capabilitiesTabLabel"),
	RETURN_TO_MOV_LABEL("features_and_specs_config_c1/returnToMOVLabel"),
	SELECT_CONFIGURATION_LABEL("features_and_specs_config_c1/selectConfigurationLabel"),
	SELECT_TRIM_LABEL("features_and_specs_config_c1/selectTrimLabel"),
	CONFIGURATION_CONFLICT_MESSAGE("features_and_specs_config_c1/trucksVansConfigSelectConflictMessage"),
	DRIVE_CONFLICT_MESSAGE_TRUCKS_AND_VANS("features_and_specs_config_c1/trucksVansDriveSelectConflictMessage"),
	DRIVE_AND_CONFIGURATION_CONFLICT_MESSAGE_TRUCKS_AND_VANS("features_and_specs_config_c1/trucksVansDriveAndConfigurationSelectConflictMessage"),
	DRIVE_CONFLICT_MESSAGE_CARS_AND_SUVS("features_and_specs_config_c1/carsSuvsDriveSelectConflictMessage"),
	UNDO_SELECTION_LABEL("features_and_specs_config_c1/undoSelection"),
	CHANGE_CONFIGURATION_MATRIX_LINK_LABEL("features_and_specs_config_c1/changeConfigurationMatrixLabel"),
	CHANGE_CONFIGURATION_DROPDOWN_LABEL("features_and_specs_config_c1/changeConfigurationDropdownLabel"),
	ADD_CONFIGURATION_MATRIX_LINK_LABEL("features_and_specs_config_c1/addConfigurationMatrixLabel"),
	CONFIGURATION_MATRIX_TITLE("features_and_specs_config_c1/configurationMatrixTitle"),
	SEGMENT_TRUCK_LABEL("features_and_specs_config_c1/segmentConfigTruckLabel"),
	SEGMENT_VANS_LABEL("features_and_specs_config_c1/segmentConfigVanLabel"),
	;

	private static final String FS_CONFIG_STRING = "features_and_specs_config_c1";
	private String propertyName;

	private FeaturesAndSpecsConfigProperties(final String propertyName) {
		this.propertyName = propertyName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.gm.gssm.gmds.cq.services.config.LSLRConfigProperties#getPropertyName()
	 */
	@Override
	public String getPropertyName() {
		return this.propertyName;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.gm.gssm.gmds.cq.services.config.LSLRConfigProperties#getConfigValueFrom(com.gm.gssm.gmds
	 * .cq.services.config.LanguageSLRService, com.day.cq.wcm.api.Page)
	 */
	@Override
	public String getConfigValueFrom(final LanguageSLRService languageService,
	                                 final Page currentPage) {
		return languageService.getConfigValue(currentPage, this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.gm.gssm.gmds.cq.services.config.LSLRConfigProperties#getConfigValueFrom(org.apache.sling
	 * .api.resource.Resource, org.apache.sling.api.scripting.SlingScriptHelper)
	 */
	@Override
	public String getConfigValueFrom(final Resource resource,
	                                 final SlingScriptHelper slingScriptHelper) {
		final String actualPropertyName = getPropertyName().replace(FS_CONFIG_STRING, "");
		return DiffUtil.getDiff(resource, actualPropertyName, false, slingScriptHelper);
	}
}