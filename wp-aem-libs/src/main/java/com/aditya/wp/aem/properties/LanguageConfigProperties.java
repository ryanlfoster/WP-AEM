/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.properties;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;

import com.aditya.wp.aem.services.config.LanguageSLRService;
import com.aditya.wp.aem.utils.diff.DiffUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum LanguageConfigProperties implements LSLRConfigProperties {

	/**
	 * IMPORTANT NOTE: Be careful not to add so many enum values as to exceed the maximum bytecode
	 * size of 2^16 bytes (~64KB). If this happens, the following cryptic error will occur when
	 * wp-wcms-libs is built: Failed to execute goal org.apache.felix:maven-scr-plugin:1.7.4:scr
	 * (generate-scr-scrdescriptor) on project wp-wcms-libs: Execution generate-scr-scrdescriptor
	 * of goal org.apache.felix:maven-scr-plugin:1.7.4:scr failed: 500 -> [Help 1] If needed, move
	 * additional enum values to a new file (e.g., AccessoryCatalogConfigProperties.java).
	 */

	/** if true, disclaimer links are applied to the whole attribute unit. */
	ATTRIBUTE_UNIT_LINK("attr_container_c1/isAttributeUnitLink"),

	/** BackToTop Link on FAQ and Glossary Template. */
	BACK_TO_TOP_LINKTEXT("language_config/backtotop_linktext"),

	/** Gets the baseball card legal text value. */
	BASEBALLCARD_LEGALTEXT("language_config/baseballcardlegaltext"),

	/** The build your own link text. */
	BUILDYOUROWN_LINKTEXT("language_config/buildyourown_linktext"),

	/** close button text. */
	CLOSE_BTN_TEXT("language_config/close_btn_text"),

	/** The compare trims linkt ext. */
	COMPARETRIMS_LINKTEXT("language_config/comparetrims_linktext"),

	/** the competitive comparison msrp text. */
	COMPETITIVE_COMPARISON_MSRP_TEXT("cnt_compare_config_c1/msrpTxt"),

	/** The competitive comparison advantages icon. */
	COMPETITIVE_COMPARISON_ADVANTAGES_ICON_PATH("cnt_compare_config_c1/advantagesIcon/fileReference"),

	/** The competitive comparison advantages text default. */
	COMPETITIVE_COMPARISON_ADVANTAGES_TEXT("cnt_compare_config_c1/advantagesTxt"),

	/** The competitive comparison disclaimer text. */
	COMPETITIVE_COMPARISON_DISCLAIMER_TEXT("cnt_compare_config_c1/disclaimer"),

	/** The competitive comparison msrp icon path. */
	COMPETITIVE_COMPARISON_MSRP_ICON_PATH("cnt_compare_config_c1/msrp/fileReference"),

	/** The competitive comparison vendor icon path. */
	COMPETITIVE_COMPARISON_VENDOR_ICON_PATH("cnt_compare_config_c1/vendorIcon/fileReference"),

	/** The COMPETITIVE compariso linktext. */
	COMPETITIVE_FEATURE_COMPARISON_LINKTEXT("language_config/competitivefeaturecomparison_linktext"),

	/** The COMPETITIVE photo comparison linktext. */
	COMPETITIVE_PHOTO_COMPARISON_LINKTEXT("language_config/competitivephotocomparison_linktext"),

	/** Gets the component library entry point path. */
	COMPONENT_LIBRARY_ENTRY_POINT("cl_config_c1/entryPoint"),

	/** Gets the Alternative Content for IFrames text. */
	COOKIE_ALT_CONTENT_FOR_IFRAMES("ut_cookie_privacy_lslr_config_c1/alternative_content_iframes"),

	/** Gets the cookie privacy copy & link to cookielist text. */
	COOKIE_PRIVACY_LABELS_COPY_LINK_COOKIELIST("ut_cookie_privacy_lslr_config_c1/copy_link_cookielist"),

	/** Gets the text for the Opt-in Button. */
	COOKIE_PRIVACY_LABELS_OPTIN_BUTTON_LABEL("ut_cookie_privacy_lslr_config_c1/optin_button_label"),

	/** Gets the text for the Opt-out Button. */
	COOKIE_PRIVACY_LABELS_OPTOUT_BUTTON_LABEL("ut_cookie_privacy_lslr_config_c1/optout_button_label"),

	/** Gets the text for the Tab-Label. */
	COOKIE_PRIVACY_LABELS_TAB_LABEL("ut_cookie_privacy_lslr_config_c1/tablabel"),

	/** Gets the Tab-Label Icon. */
	COOKIE_PRIVACY_LABELS_TAB_LABEL_ICON("ut_cookie_privacy_lslr_config_c1/imageReference"),

	/** current news. */
	CURRENT_NEWS("language_config/current_news"),

	/** currency thousand separator. */
	CURRENCY_THOUSAND_SEPERATER("number_format_c1/thousandSeparator"),

	/** currency decimal separator. */
	CURRENCY_DECIMAL_SEPERATER("number_format_c1/decimalSeparator"),

	/** currency symbol. */
	CURRENCY_SYMBOL("number_format_c1/currencySymbol"),

	/** The DEALERLOCATO r_ externa l_ link. */
	DEALERLOCATOR_EXTERNAL_LINK("language_config/dealerlocator_external_link"),

	/** The dealer locator link. */
	DEALERLOCATOR_LINK("language_config/dealerlocator_link"),

	/** The dealer locator link text. */
	DEALERLOCATOR_LINKTEXT("language_config/dealerlocator_linktext"),

	/** Get the Read and Write Direction. */
	DIRECTION("language_config/direction"),

	/** The ESTIMAT e_ payment s_ externa l_ link. */
	ESTIMATE_PAYMENTS_EXTERNAL_LINK("language_config/estimatepayments_external_link"),

	/** The search inventory link. */
	ESTIMATE_PAYMENTS_LINK("language_config/estimatepayments_link"),

	/** The search inventory link text. */
	ESTIMATE_PAYMENTS_LINKTEXT("language_config/estimatepayments_linktext"),

	/** The family link text. */
	FAMILY_LINKTEXT("language_config/family_linktext"),

	/** ilike height for the gallery. */
	GALLERY_ILIKE_HEIGHT("social_config_c1/ilike_gallery_like_button_height"),

	/** ilike width for the gallery. */
	GALLERY_ILIKE_WIDTH("social_config_c1/ilike_gallery_like_button_width"),

	/** The gallery link text. */
	GALLERY_LINKTEXT("language_config/gallery_linktext"),

	/** Boolean if metanavigation is scrolling. */
	HEADER_METANAVIGATION_META_SCROLLING("nav_meta_config_c1/meta_scrolling"),

	/** Inventory count label. */
	INVENTORY_COUNT_LABEL("inventory_config/inv_count_label"),

	/** Inventory icon image. */
	INVENTORY_ICON_IMAGE("inventory_config/imageReference"),

	/** Inventory label for a new search. */
	INVENTORY_NEW_SEARCH_LINK_LABEL("inventory_config/inv_new_search_link_label"),

	/** Inventory text for no results. */
	INVENTORY_NO_RESULTS_TEXT("inventory_config/inv_no_results_text"),

	/** Inventory label for the postal code search field. */
	INVENTORY_POSTAL_CODE_SEARCH_LABEL("inventory_config/inv_postal_code_search_label"),

	/** Inventory label for submit postal code search field. */
	INVENTORY_SUBMIT_LINK_LABEL("inventory_config/inv_submit_link_label"),

	/** Inventory title. */
	INVENTORY_TITLE("inventory_config/inv_conf_title"),

	/** Inventory search inventory link label. */
	INVENTORY_VIEW_INV_LINK_LABEL("inventory_config/inv_view_inventory_label"),

	/** JavaScript Requirement label. */
	JAVASCRIPT_REQUIRED_HEADER("javascript_required_config_c1/jsRequiredAlertHeader"),

	/** JavaScript Requirement Text. */
	JAVASCRIPT_REQUIRED_MESSAGE("javascript_required_config_c1/jsRequiredAlertMessage"),

	/** The text of the layer button. */
	LAYER_BUTTON_BUTTON_TEXT("btn_lyr_c1_config/buttontext"),

	/** The external link of the layer button. */
	LAYER_BUTTON_EXTERNALLINK("btn_lyr_c1_config/externalLink"),

	/** The image of the layer button. */
	LAYER_BUTTON_IMAGE("btn_lyr_c1_config/image"),

	/** The alternative text of the button image of the layer button. */
	LAYER_BUTTON_IMAGE_ALTTEXT("btn_lyr_c1_config/altText"),

	/** The height of the layer button's layer. */
	LAYER_BUTTON_LAYER_HEIGHT("btn_lyr_c1_config/layer_height"),

	/** The width of the layer button's layer. */
	LAYER_BUTTON_LAYER_WIDTH("btn_lyr_c1_config/layer_width"),

	/** The alternative title of the layer button's image which would be shown as tooltip. */
	LAYER_BUTTON_LINK_TITLE("btn_lyr_c1_config/linktitle"),

	/**
	 * If the 'close window' label should additionally be shown to the close icon for the layer
	 * button.
	 */
	LAYER_BUTTON_SHOW_BUTTON_LABEL("btn_lyr_c1_config/showButtonLabelEnabled"),

	LIGHTBOX_ENLARGE_TEXT("language_config/lightbox_enlarge_text"),

	LIGHTBOX_IMAGE_TEXT("language_config/lightbox_image_text"),

	LIGHTBOX_PHOTO_GALLERY_TEXT("language_config/lightbox_photo_gallery_text"),

	/** LocalDealer Advanced Search. */
	LOCALDEALER_ADVANCED_SEARCH("language_config/localdealeradvancedsearch"),

	/** LocalDealer Advanced Search. */
	LOCALDEALER_ADVANCED_SEARCH_TOOLTIP("language_config/localdealeradvancedsearchtooltip"),

	/** Get the values for the local dealer values used in ut_loc_c1 component. LocalDealer Button. */
	LOCALDEALER_BUTTON("language_config/localdealerbutton"),

	/** LocalDealer Label1. */
	LOCALDEALER_LABEL1("language_config/localdealerlabel1"),

	/** LocalDealer Label2. */
	LOCALDEALER_LABEL2("language_config/localdealerlabel2"),

	/** LocalDealer Label3. */
	LOCALDEALER_LABEL3("language_config/localdealerlabel3"),

	/** Locate a dealer page. */
	LOCALDEALER_PAGE("language_config/localdealerpage"),

	/** LocalDealer Text. */
	LOCALDEALER_TEXT("language_config/localdealertext"),

	/** LocalDealer teaser title. */
	LOCALDEALER_TT("language_config/localdealertt"),

	/** LocalDealer Advanced Search. */
	LOCALDEALER_WATERMARK("language_config/localdealerwatermark"),

	/** label text for component mm_gal_c1 download. */
	MM_GAL_C1_DOWNLOAD("language_config/mm_gal_c1_download"),

	/** label text for component mm_gal_c1 enlarge. */
	MM_GAL_C1_ENLARGE("language_config/mm_gal_c1_enlarge"),

	/** The model year switch linktext prefix. */
	MODEL_YEAR_SWITCH_LINKTEXT_PREFIX("language_config/modelyearswitch_linktext_prefix"),

	/** The Mmodel year switch linktext suffix. */
	MODEL_YEAR_SWITCH_LINKTEXT_SUFFIX("language_config/modelyearswitch_linktext_suffix"),

	/** The model overview link text. */
	MODELOVERVIEW_LINKTEXT("language_config/modeloverview_linktext"),

	/** The more details link text. */
	MORE_DETAILS_LINK_TEXT("language_config/more_details_link_text"),

	/** The model overview link text. */
	NEWS_ARCHIVE("language_config/news_archive"),

	/** The plan route external link. */
	PLANROUTE_EXTERNAL_LINK("language_config/planroute_external_link"),

	/** The plan route link. */
	PLANROUTE_LINK("language_config/planroute_link"),

	/** The plan route link text. */
	PLANROUTE_LINKTEXT("language_config/planroute_linktext"),

	/** The please choose label. */
	PLEASE_CHOOSE_LABEL("language_config/please_choose_label"),

	/** The preferred pricing college label. */
	PREF_PRICING_COLLEGE_LABEL("language_config/preferred_pricing_college_label"),

	/** The preferred pricing military label. */
	PREF_PRICING_MILITARY_LABEL("language_config/preferred_pricing_military_label"),

	/** The preferred pricing not eligible label. */
	PREF_PRICING_NOT_ELIGIBLE_LABEL("language_config/preferred_pricing_not_eligible_label"),

	/** The preferred pricing discount program link. */
	PREF_PRICING_DISCOUNT_PROGRAM_LINK("language_config/preferred_pricing_discount_program_external_link"),

	/** Get the values for the print button used in ut_ln_print_c1 component. */
	PRINTLABEL("language_config/print"),

	/** Get the values for the Date of Print used in pdf_byo_c1 component. */
	PRINTEDLABEL("language_config/printed"),

	/** The PSW Floater program 1 eligibility disclaimer. */
	PSW_FLOATER_PROGRAM1_ELIGIBILITY_DISCLAIMER("psw_floater_config_c1/program1/eligibility_disclaimer"),

	/** The PSW Floater program 2 eligibility disclaimer. */
	PSW_FLOATER_PROGRAM2_ELIGIBILITY_DISCLAIMER("psw_floater_config_c1/program2/eligibility_disclaimer"),

	/** The PSW Floater program 1 eligibility overlay CTA button label. */
	PSW_FLOATER_PROGRAM1_ELIGIBILITY_OVERLAY_CTA_BUTTON_LABEL("psw_floater_config_c1/program1/eligibility_overlay_cta_button_label"),

	/** The PSW Floater program 2 eligibility overlay CTA button label. */
	PSW_FLOATER_PROGRAM2_ELIGIBILITY_OVERLAY_CTA_BUTTON_LABEL("psw_floater_config_c1/program2/eligibility_overlay_cta_button_label"),

	/** The PSW Floater program 1 eligibility overlay message. */
	PSW_FLOATER_PROGRAM1_ELIGIBILITY_OVERLAY_MESSAGE("psw_floater_config_c1/program1/eligibility_overlay_message"),

	/** The PSW Floater program 2 eligibility overlay message. */
	PSW_FLOATER_PROGRAM2_ELIGIBILITY_OVERLAY_MESSAGE("psw_floater_config_c1/program2/eligibility_overlay_message"),

	/** The PSW Floater program 1 eligibility overlay popup title. */
	PSW_FLOATER_PROGRAM1_ELIGIBILITY_OVERLAY_POPUP_TITLE("psw_floater_config_c1/program1/eligibility_overlay_popup_title"),

	/** The PSW Floater program 2 eligibility overlay popup title. */
	PSW_FLOATER_PROGRAM2_ELIGIBILITY_OVERLAY_POPUP_TITLE("psw_floater_config_c1/program2/eligibility_overlay_popup_title"),

	/** The PSW Floater program 1 eligibility popup link label. */
	PSW_FLOATER_PROGRAM1_ELIGIBILITY_POPUP_LINK_LABEL("psw_floater_config_c1/program1/eligibility_popup_link_label"),

	/** The PSW Floater program 2 eligibility popup link label. */
	PSW_FLOATER_PROGRAM2_ELIGIBILITY_POPUP_LINK_LABEL("psw_floater_config_c1/program2/eligibility_popup_link_label"),

	/** The PSW Floater program 1 program code parameter. */
	PSW_FLOATER_PROGRAM1_PROGRAM_CODE_PARAMETER("psw_floater_config_c1/program1/program_code_param"),

	/** The PSW Floater program 2 program code parameter. */
	PSW_FLOATER_PROGRAM2_PROGRAM_CODE_PARAMETER("psw_floater_config_c1/program2/program_code_param"),

	/** The PSW Floater program 1 PSW message. */
	PSW_FLOATER_PROGRAM1_PSW_MESSAGE("psw_floater_config_c1/program1/psw_floater_message"),

	/** The PSW Floater program 2 PSW message. */
	PSW_FLOATER_PROGRAM2_PSW_MESSAGE("psw_floater_config_c1/program2/psw_floater_message"),

	/** The PUBLISHE r_ domain. */
	PUBLISHER_DOMAIN("nav_lang_switch_config_c1/publisher_domain"),

	/** The REVIEW LINk TITEL. */
	REVIEW_LINKS_TITLE("language_config/review_links_title"),

	/** The REVIEW LINk TITEL. */
	ACCESSORIES_LINKS_TITLE("language_config/accessories_link_title"),

	/** The REQUESTBROCHUR e_ externa l_ link. */
	REQUESTBROCHURE_EXTERNAL_LINK("language_config/requestbrochure_external_link"),

	/** The request a brochure link. */
	REQUESTBROCHURE_LINK("language_config/requestbrochure_link"),

	/** The request a brochure link text. */
	REQUESTBROCHURE_LINKTEXT("language_config/requestbrochure_linktext"),

	/** The REQUESTLEASE external_ link. */
	REQUESTLEASE_EXTERNAL_LINK("language_config/requestlease_external_link"),

	/** The request a lease link. */
	REQUESTLEASE_LINK("language_config/requestlease_link"),

	/** The request a lease link text. */
	REQUESTLEASE_LINKTEXT("language_config/requestlease_linktext"),

	/** The REQUESTNEWSLETTE r_ externa l_ link. */
	REQUESTNEWSLETTER_EXTERNAL_LINK("language_config/requestnewsletter_external_link"),

	/** The request newsletter link. */
	REQUESTNEWSLETTER_LINK("language_config/requestnewsletter_link"),

	/** The request a newsletter link text. */
	REQUESTNEWSLETTER_LINKTEXT("language_config/requestnewsletter_linktext"),

	/** The request a quote external link. */
	REQUESTQUOTE_EXTERNAL_LINK("language_config/requestquote_external_link"),

	/** The request a quote link. */
	REQUESTQUOTE_LINK("language_config/requestquote_link"),

	/** The request a quote link text. */
	REQUESTQUOTE_LINKTEXT("language_config/requestquote_linktext"),

	/** The request a service external link. */
	REQUESTSERVICE_EXTERNAL_LINK("language_config/requestservice_external_link"),

	/** The request a service link. */
	REQUESTSERVICE_LINK("language_config/requestservice_link"),

	/** The request a service link text. */
	REQUESTSERVICE_LINKTEXT("language_config/requestservice_linktext"),

	/** The request a test drive link. */
	REQUESTTESTDRIVE_LINK("language_config/testdrive_link"),

	/** The request a test drive link text. */
	REQUESTTESTDRIVE_LINKTEXT("language_config/testdrive_linktext"),

	/** The SEARCHINVENTOR y_ externa l_ link. */
	SEARCHINVENTORY_EXTERNAL_LINK("language_config/searchinventory_external_link"),

	/** The search inventory link. */
	SEARCHINVENTORY_LINK("language_config/searchinventory_link"),

	/** The search inventory link text. */
	SEARCHINVENTORY_LINKTEXT("language_config/searchinventory_linktext"),

	/** The service dealer locator external link. */
	SERVICE_DEALERLOCATOR_EXTERNAL_LINK("language_config/service_dealerlocator_external_link"),

	/** The service dealer locator internal link. */
	SERVICE_DEALERLOCATOR_LINK("language_config/service_dealerlocator_link"),

	/** label text for component mm_gal_c1 enlarge. */
	SHARE("language_config/share"),

	/** The maximum age of the expiry time cookie for the social sharing switch. */
	SHARING_SWITCH_MAXIMUM_COOKIE_AGE("social_sharingswitch_message_c1/maximum_age_of_expiry_time_cookie"),

	/** Popup paragraph text when social sharing switch is enabled. */
	SHARING_SWITCH_PARAGRAPH_TEXT("social_sharingswitch_message_c1/cookie_approval_popup_paragraph_text"),

	/** Popup title text when social sharing switch is enabled. */
	SHARING_SWITCH_TITLE_TEXT("social_sharingswitch_message_c1/cookie_approval_popup_title_text"),

	/** Shopping tools. */
	SHOPPING_TOOLS("social_sharingswitch_message_c1/language_config/shopping_tools"),

	/** The SOCIA l_ link s_ position. */
	SOCIAL_LINKS_POSITION("social_links_c1/socialLinkPosition"),

	/** The static dealer page error page. */
	STATICDEALERPAGE_ERRORPAGE("language_config/staticdealerpage_errorpage"),

	/** The static dealer page link. */
	STATICDEALERPAGE_LINK("language_config/staticdealerpage_link"),

	/** The text for the exterior view in the 360 gallery. */
	T07_MM_GAL360_C1_EXTERIOR("language_config/t07_mm_gal360_c1_exterior"),

	/** The text for the footnote in the 360 gallery. */
	T07_MM_GAL360_C1_FOOTNOTE("language_config/t07_mm_gal360_c1_footnote"),

	/** The text for the interior view in the 360 gallery. */
	T07_MM_GAL360_C1_INTERIOR("language_config/t07_mm_gal360_c1_interior"),

	/** label text for t07 template. */
	T07_NEXTBUTTON("language_config/t07_nextbutton"),

	/** The T07_ prevbutton. */
	T07_PREVBUTTON("language_config/t07_prevbutton"),

	T12_ARCHIVE_LABEL("language_config/t12_archive_label"),

	/** The event filter label on T12b. */
	T12_CARLINE_FILTER_DEFAULT("language_config/t12_carline_filter_default"),

	/** The event filter label on T12b. */
	T12_CARLINE_FILTER_LABEL("language_config/t12_carline_filter_label"),

	/** The event filter label on T12a. */
	T12A_CARLINE_FILTER_DEFAULT("language_config/t12a_carline_filter_default"),

	/** The event filter label on T12a. */
	T12A_CARLINE_FILTER_LABEL("language_config/t12a_carline_filter_label"),

	/** The event filter label on T12. */
	T12_CATEGORY_FILTER_DEFAULT("language_config/t12_category_filter_default"),

	/** The event filter label on T12. */
	T12_CATEGORY_FILTER_LABEL("language_config/t12_category_filter_label"),

	T12_CATEGORY_LABEL("language_config/t12_category_label"),

	/** The event filter label on T12. */
	T12_DATE_FILTER_DEFAULT("language_config/t12_date_filter_default"),

	/** The event filter label on T12. */
	T12_DATE_FILTER_LABEL("language_config/t12_date_filter_label"),

	T12_FEATURED_CATEGORY("language_config/t12_featured_category"),

	T12_NEWS_PAGING_NEXT_LABEL("language_config/t12_news_paging_next_label"),

	T12_NEWS_PAGING_PAGE_LABEL("language_config/t12_news_paging_page_label"),

	T12_NEWS_PAGING_PREVIOUS_LABEL("language_config/t12_news_paging_previous_label"),

	/** The event filter label on T12a. */
	T12A_YEAR_FILTER_DEFAULT("language_config/t12_year_filter_default"),

	/** The event filter label on T12a. */
	T12A_YEAR_FILTER_LABEL("language_config/t12_year_filter_label"),

	/** The list view label on T12b. */
	T12B_LIST_VIEW_ICON_LABEL("language_config/t12b_list_view_icon_label"),

	/** The image view label on T12b. */
	T12B_IMAGE_VIEW_ICON_LABEL("language_config/t12b_image_view_icon_label"),

	/** The most popular label on T12b. */
	T12B_MOST_POPULAR_LABEL("language_config/t12b_most_popular_label"),

	/** The most recent label on T12b. */
	T12B_MOST_RECENT_LABEL("language_config/t12b_most_recent_label"),

	/** The read story label on T12b. */
	T12B_READ_STORY_LABEL("language_config/t12b_read_story_link_label"),

	/** The read story link title on T12b. */
	T12B_READ_STORY_LINK_TITLE("language_config/t12b_read_story_link_title"),

	/** The most popular label on the story_browser. */
	T13B_STORYBROWSER_MOST_POPULAR_LABEL("language_config/t13b_story_browser_popular_label"),

	/** The most popular label on the story_browser. */
	T13B_STORYBROWSER_MOST_RECENT_LABEL("language_config/t13b_story_browser_recent_label"),

	/** the tab all label. */
	TAB_ALL_LABEL("language_config/nav_tablay_filter_all_label"),

	/** The TESTDRIV e_ externa l_ link. */
	TESTDRIVE_EXTERNAL_LINK("language_config/testdrive_external_link"),

	/** The test drive link. */
	TESTDRIVE_LINK("language_config/testdrive_link"),

	/** The test drive link text. */
	TESTDRIVE_LINKTEXT("language_config/testdrive_linktext"),

	/** whether to load the vehicle attributes or not. */
	VEHICLE_DATA_CONFIG_LOADATTRIBUTES("vehicledata_config_c1/loadAttributes"),

	/** The loadfrom attribute defines from where the market.xml will be loaded (crx or http). */
	VEHICLE_DATA_CONFIG_LOADFROM("vehicledata_config_c1/loadfrom"),

	/**
	 * With this attribute, the bodystyle selection label can be overwritten on the N2B carline
	 * detail navigation.
	 */
	VEHICLE_NAVIGATION_LABEL("language_config/vehicle_navigation_label"),

	/** The view configuration shopping link label. */
	VIEW_CONFIGURATION_LINKTEXT("language_config/viewconfiguration_linktext"),

	/** The VIE w_ curren t_ offer s_ externa l_ link. */
	VIEW_CURRENT_OFFERS_EXTERNAL_LINK("language_config/viewcurrentoffers_external_link"),

	/** The search inventory link. */
	VIEW_CURRENT_OFFERS_LINK("language_config/viewcurrentoffers_link"),

	/** The visualizer conflict message. */
	VISUALIZER_CONFLICT_MESSAGE("visualizer_config/conflictMessageLabel"),

	/** The visualizer details link label. */
	VISUALIZER_DETAILS_LINK_LABEL("visualizer_config/detailsLinkLabel"),

	/** The visualizer conflict detail message. */
	VISUALIZER_CONFLICT_DETAIL_MESSAGE("visualizer_config/conflictDetailsMessage"),

	/** The visualizer continue link label. */
	VISUALIZER_CONTINUE_LINK_LABEL("visualizer_config/continueLinkLabel"),

	/** The visualizer OK link label. */
	VISUALIZER_UNDO_LINK_LABEL("visualizer_config/undoLinkLabel"),

	/** The visualizer conflict message. */
	VISUALIZER_COMPARE_LINK_LABEL("visualizer_config/compareLinkLabel"),

	/** The visualizer zoom label. */
	VISUALIZER_ZOOM_LABEL("visualizer_config/zoomLabel"),

	/** Does the visualizer utilize preferred vehicle in local storage. */
	VISUALIZER_APPLICATION_PREFERENCES_ENABLED("visualize_config/enablePrefs"),

	/** The visualizer application message title. */
	VISUALIZER_APPLICATION_MESSAGE_TITLE("visualizer_config/messageTitle"),

	/** The visualizer application message body. */
	VISUALIZER_APPLICATION_MESSAGE_BODY("visualizer_config/messageBody"),

	/** The visualizer application reset label. */
	VISUALIZER_APPLICATION_RESET_LABEL("visualizer_config/resetLabel"),

	/** The visualizer application byo link label. */
	VISUALIZER_APPLICATION_BYO_LINK_LABEL("visualizer_config/byoLinkLabel"),

	/** The visualizer application vehicle label. */
	VISUALIZER_APPLICATION_VEHICLE_LABEL("visualizer_config/vehicleLabel"),

	/** The nav footer show attribut title. */
	SHOW_NAV_FOOTER("nav_footer_config/showNavFooter"),

	/** The nav_footer_2's position. */
	NAV_FOOTER_2_POSITION("nav_footer_config_c2/position"),

	/** The search inventory link text. */
	VIEW_CURRENT_OFFERS_LINKTEXT("language_config/viewcurrentoffers_linktext"),

	HMC_COMPARISON_CONFIG_AVAILABLE_FEATURE_LABEL("hmc_comparison_config_c1/availableFeatureLabel"),

	HMC_COMPARISON_CONFIG_NOT_AVAILABLE_FEATURE_LABEL("hmc_comparison_config_c1/notAvailableFeatureLabel"),

	HMC_COMPARISON_CONFIG_STANDARD_FEATURE_LABEL("hmc_comparison_config_c1/standardFeatureLabel"),

	/** The sitemap generation. */
	SITEMAP_GENERATE_IMAGEMAP("sitemap_config_c1/sitemap/generateImageMap"),

	SITEMAP_GENERATE_VIDEOMAP("sitemap_config_c1/sitemap/generateVideoMap"),

	/** Schema.org Markup */
	SCHEMA_ORG_MARKUP_ENABLED("schema_config/schemaMarkupEnabled"),

	/** net pricing type. */
	NET_PRICING_TYPE("language_config/pricing_type"),

	/** msrp label for net pricing tooltip. */
	NET_PRICING_MSRP_LABEL("language_config/nat_inc_msrp"),

	/** incentive label for net pricing tooltip. */
	NET_PRICING_INCENTIVE_LABEL("language_config/nat_inc_incentive"),

	/** total price label for net pricing tooltip. */
	NET_PRICING_TOTAL_PRICE_LABEL("language_config/nat_inc_total_price"),

	/** vehicle title disclaimer for net pricing tooltip. */
	NET_PRICING_VEHICLE_TITLE_DISCLAIMER("language_config/nat_inc_vt_disclaimer"),

	/** incentive disclaimer for net pricing tooltip. */
	NET_PRICING_INCENTIVE_DISCLAIMER("language_config/nat_inc_disclaimer"),

	/** destination freight charges disclaimer for net pricing tooltip. */
	NET_PRICING_DFC_DISCLAIMER("language_config/nat_inc_dfc_disclaimer"),

	/** disclaimer indicator for net pricing. */
	NET_PRICING_DISCLAIMER_INDICATOR("language_config/nat_inc_disclaimer_indicator"),

	/** additional disclaimer for net pricing. */
	NET_PRICING_ADDITIONAL_DISCLAIMER("language_config/nat_inc_additional_disclaimer"),

	RSS_FEED_FOLDER_LOCATION("rss_config_c1/folderLocation"),

	RSS_FEED_TITLE("rss_config_c1/title"),

	RSS_FEED_DESCRIPTION("rss_config_c1/description"),

	RSS_FEED_COPYRIGHT("rss_config_c1/copyright"),

	RSS_FEED_CATEGORIES("rss_config_c1/categories"),

	/** The Clipped Competitive Comparison link text. */
	CLIPPED_COMPETITIVE_COMPARISON_LINKTEXT("language_config/clipped_competitive_comparison_linktext"),

	WISHLIST_MOV_NAVIGATION_TITLE("wishlist_config_c1/navTitle"),

	WISHLIST_MOV_LIST_TITLE("wishlist_config_c1/movListTitle"),

	WISHLIST_MOV_LIST_DESCRIPTION("wishlist_config_c1/movListDescription"),

	WISHLIST_MOV_CTA_LINK_LABEL("wishlist_config_c1/ctaLinkLabel"),

	WISHLIST_BYO_LIST_TITLE("wishlist_config_c1/byoListTitle"),

	WISHLIST_BYO_LIST_DESCRIPTION("wishlist_config_c1/byoListDescription"),

	WISHLIST_ADDITIONAL_FEATURES_LABEL("wishlist_config_c1/additionalFeaturesLabel"),

	WISHLIST_FEATURE_ADD_LABEL("wishlist_config_c1/addFeatureLabel"),

	WISHLIST_FEATURE_REMOVE_LABEL("wishlist_config_c1/removeFeatureLabel"),

	WISHLIST_FEATURE_SHOW_LABEL("wishlist_config_c1/showAvailabilityLabel"),

	WISHLIST_FEATURE_HIDE_LABEL("wishlist_config_c1/hideAvailabilityLabel"),

	WISHLIST_FEATURE_AVAILABILITY_DESCRIPTION("wishlist_config_c1/availabilityDescription"),

	WISHLIST_FEATURE_COMPARE_TRIM_LABEL("wishlist_config_c1/compareTrimLinkLabel"),

	WISHLIST_FEATURE_HELP_TITLE("wishlist_config_c1/featureTitle"),

	WISHLIST_FEATURE_HELP_TEXT("wishlist_config_c1/featureText"),

	COLORIZER_SELECT_COLOR_LABEL("language_config/colorizerSelectColorLabel"),

	NO_IMAGE_LABEL("language_config/no_image_label"),

	/** The read story label on T12b. */
	STORY_POOL_ROOT_PATH("story_pool_c1/storyPoolRootPath"),

	CONFIGURED_VEHICLE_TOTAL_PRICE_LABEL("language_config/configuredVehicleTotalPrice"),

	/** The tire finder link. */
	TIREFINDER_LINK("language_config/tirefinder_link"),

	/** The tire finder link text. */
	TIREFINDER_LINKTEXT("language_config/tirefinder_linktext"),

	/** The tire finder external link. */
	TIREFINDER_EXTERNAL_LINK("language_config/tirefinder_external_link"),

	;

	private static final String LANGUAGE_CONFIG_STRING = "language_config/";

	private String propertyName;

	/**
	 * Instantiates a new config value.
	 * 
	 * @param propertyName
	 *            the property name
	 */
	private LanguageConfigProperties(final String propertyName) {
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
		final String actualPropertyName = getPropertyName().replace(LANGUAGE_CONFIG_STRING, "");
		return DiffUtil.getDiff(resource, actualPropertyName, false, slingScriptHelper);
	}
}