/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.global;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum AEMTemplateInfo {
	TEMPLATE_BASEBALLCARD_BODYSTYLE("ts_baseballcard_bodystyle"), //
	TEMPLATE_BASEBALLCARD_BODYSTYLE_MANUAL("ts_baseballcard_bodystyle_manual"), //
	TEMPLATE_BASEBALLCARD_CARLINE("ts_baseballcard_carline"), //
	TEMPLATE_BASEBALLCARD_CONFIGURATION("ts_baseballcard_configuration"), //
	TEMPLATE_BASEBALLCARD_NODE("ts_baseballcard_node", true), //
	TEMPLATE_BASEBALLCARD_OPTIONAL_GROUP("ts_baseballcard_optionalgroup"), //
	TEMPLATE_BASEBALLCARD_COLLECTION("ts_baseballcard_collection"), //
	TEMPLATE_BASEBALLCARD_BODYSTYLE_REFERENCE("ts_baseballcard_bodystyle_reference"), //
	TEMPLATE_BASEBALLCARD_PRODUCT("ts_baseballcard_product"), //
	TEMPLATE_EXTERNAL_FLASH("ts_external_flash"), //
	TEMPLATE_EXTERNAL_LINK("ts_external_link"), //
	TEMPLATE_FLASH_CONFIG("tc_extended_flash_config"), //
	TEMPLATE_FOLDER("ts_folder", true), //
	TEMPLATE_LSLR("tc_languageslr", "tc_lslr"), //
	TEMPLATE_COMPANY("tc_company", "tc_company"), //
	TEMPLATE_N01_THUMBNAIL("tn01_thumbnail"), //
	TEMPLATE_N01b_THUMBNAIL_NAVIGATION_BY_CARLINE("tn01b_thumbnail_navigation_by_carline"), //
	TEMPLATE_N02_BODYSTYLE_ROLLOVER("tn02_bodystyle_rollover"), //
	TEMPLATE_N02b_BODYSTYLE_NAVIGATION("tn02b_bodystyle_navigation"), //
	TEMPLATE_N03_CONTENT("tn03_content"), //
	TEMPLATE_T01("t01_language_selection", "t01"), //
	TEMPLATE_T02("t02_home", "t02"), //
	TEMPLATE_T03("t03_view_all_vehicles", "t03"), //
	TEMPLATE_T03b("t03b_view_all_vehicles_fallback", "t03b"), //
	TEMPLATE_T03c("t03c_view_all_vehicles_with_filtering", "t03c"), //
	TEMPLATE_T04a("t04a_vehicleinformation", "t04a"), //
	TEMPLATE_T04b("t04b_index", "t04b"), //
	TEMPLATE_T04c("t04c_vehicleinformation_scroller", "t04c"), //
	TEMPLATE_T05("t05_core_generic", "t05"), //
	TEMPLATE_T05b("t05b_core_generic", "t05b"), //
	TEMPLATE_T06("t06_model_overview", "t06"), //
	TEMPLATE_T06b("t06b_model_overview_longpageformat", "t06b"), //
	TEMPLATE_T06c("t06c_model_overview_without_sidebar", "t06c"), //
	TEMPLATE_T06d("t06d_model_overview_semi_responsive", "t06d"), //
	TEMPLATE_T06e("t06e_model_overview_without_sidebar_cad", "t06e"), //
	TEMPLATE_T07("t07_gallery_360_colors_vehicles", "t07"), //
	TEMPLATE_T07b("t07b_full_width_liquid_gallery", "t07b"), //
	TEMPLATE_T08("t08_extended_navigation", "t08"), //
	TEMPLATE_T09("t09_downloads", "t09"), //
	TEMPLATE_T10("t10_fullwidth_features", "t10"), //
	TEMPLATE_T12("t12_news_and_events", "t12"), //
	TEMPLATE_T12b("t12b_news_and_events", "t12b"), //
	TEMPLATE_T13("t13_news_article", "t13"), //
	TEMPLATE_T13b("t13b_news_article", "t13b"), //
	TEMPLATE_T14b("t14b_extended_glossary", "t14b"), //
	TEMPLATE_T15_SITEMAP("t15_sitemap", "t15"), //
	TEMPLATE_T15b_MANUAL_SITEMAP("t15b_manual_sitemap", "t15b"), //
	TEMPLATE_T16("t16_webclipping", "t16"), //
	TEMPLATE_T16b("t16b_iframe", "t16b"), //
	TEMPLATE_T16w("t16w_webwrapping", "t16w"), //
	TEMPLATE_T17("t17_child_window", "t17"), //
	TEMPLATE_T17b("t17b_redirect_child_window", "t17b"), //
	TEMPLATE_T17c("t17c_modal_layer", "t17c"), //
	TEMPLATE_T17d("t17d_lightbox", "t17d"), //
	TEMPLATE_T20("t20_dealer_page", "t20"), //
	TEMPLATE_T21("t21_pdf", "t21"), //
	TEMPLATE_TAB_NAVIGATION("ts_tabnavigation", true), //
	TEMPLATE_TAB_NAVIGATION_GALLERY("ts_tabnavigation_gallery", true), //
	TEMPLATE_TS_CL("ts_component_library_template"), //
	TEMPLATE_TS_CL_FOLDER("ts_component_library", true), //
	TEMPLATE_TS_KBB_RSS("ts_kbb_rss"), //
	TEMPLATE_TS_KBB_RSS_ITEM("ts_kbb_rss_item"),    //
	TEMPLATE_SOCIALFEED("socialfeed", "socialfeed"),
	TEMPLATE_N02_NODE("t02_node");

	private static final Map<String, AEMTemplateInfo> LUT = new HashMap<String, AEMTemplateInfo>();

	private static final String TEMPLATE_PATH = "/apps/wp/templates/";

	static {
		for (final AEMTemplateInfo inf : EnumSet.allOf(AEMTemplateInfo.class)) {
			LUT.put(inf.getTemplateName(), inf);
		}
	}

	/**
	 * Looks up the CQTemplateInfo by the given page object.
	 * 
	 * @param currentPage
	 *            the page object
	 * @return a CQTemplateInfo instance or null, if template is not konwn
	 */
	public static AEMTemplateInfo lookup(final Page currentPage) {
		if (currentPage != null) {
			final Template template = currentPage.getTemplate();
			if (template != null) {
				return lookup(template.getName());
			}
		}
		return null;
	}

	/**
	 * Looks up the CQTemplateInfo by the given template name.
	 * 
	 * @param templateName
	 *            the template name
	 * @return a CQTemplateInfo instance or null, if template is not konwn
	 */
	public static AEMTemplateInfo lookup(final String templateName) {
		return LUT.get(templateName);
	}

	/**
	 * Whether it is a folder template.
	 */
	private boolean isFolderTemplate;

	/** The template name. */
	private String templateName;

	/** The template class name. */
	private String templateCssClass;

	/**
	 * Instantiates a new cQ template info.
	 * 
	 * @param templateName
	 *            the template name
	 */
	private AEMTemplateInfo(final String templateName) {
		this.templateName = templateName;
	}

	/**
	 * Instantiates a new CQ template info.
	 * 
	 * @param templateName
	 *            the template name
	 * @param templateCssClass
	 *            the css class for this template
	 */
	private AEMTemplateInfo(final String templateName, final String templateCssClass) {
		this.templateName = templateName;
		this.templateCssClass = templateCssClass;
	}

	/**
	 * Instantiates a new cQ template info.
	 * 
	 * @param templateName
	 *            the template name
	 * @param isFolderTemplate
	 *            whether it is a folder/redirect template
	 */
	private AEMTemplateInfo(final String templateName, final boolean isFolderTemplate) {
		this(templateName);
		this.isFolderTemplate = isFolderTemplate;
	}

	/**
	 * Gets the template name.
	 * 
	 * @return the template name
	 */
	public String getTemplateName() {
		return this.templateName;
	}

	/**
	 * Gets the template css class.
	 * 
	 * @return the template css class
	 */
	public String getTemplateCssClass() {
		return this.templateCssClass;
	}

	/**
	 * Gets the template name with full template path.
	 * 
	 * @return the template name
	 */
	public String getTemplatePath() {
		return TEMPLATE_PATH + this.templateName;
	}

	/**
	 * Returns whether is folder template.
	 * 
	 * @return whether it is a folder/redirect template
	 */
	public boolean isFolderTemplate() {
		return this.isFolderTemplate;
	}

	/**
	 * Checks if the used template of the current Page matches the templateName.
	 * 
	 * @param currentPage
	 *            the current page
	 * @return true, if successful
	 */
	public boolean matchesTemplate(final Page currentPage) {
		if (currentPage != null) {
			final Template t = currentPage.getTemplate();
			if (t != null) {
				return this.templateName.equals(t.getName());
			}
		}
		return false;
	}
}
