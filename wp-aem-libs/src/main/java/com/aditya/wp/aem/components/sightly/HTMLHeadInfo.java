/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.components.sightly;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.components.global.PageModifier;
import com.aditya.gmwp.aem.global.AEMTemplateInfo;
import com.aditya.gmwp.aem.properties.BaseballcardBodystyleProperties;
import com.aditya.gmwp.aem.properties.BaseballcardCarlineProperties;
import com.aditya.gmwp.aem.properties.CompanyConfigProperties;
import com.aditya.gmwp.aem.properties.LSLRComponentProperties;
import com.aditya.gmwp.aem.properties.LanguageConfigProperties;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.ConfigService;
import com.aditya.gmwp.aem.services.config.CookiePrivacyService;
import com.aditya.gmwp.aem.services.config.DomainPathService;
import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.services.config.LevelService;
import com.aditya.gmwp.aem.services.vehicledata.VehicleDataService;
import com.aditya.gmwp.aem.services.vehicledata.data.BodystyleBaseballcardData;
import com.aditya.gmwp.aem.utils.PageUtil;
import com.aditya.gmwp.aem.utils.PathUtil;
import com.aditya.gmwp.aem.utils.ProtocolUtil;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class HTMLHeadInfo extends WCMUse {

	private String code;
	private String direction;
	private String templateCss;
	private String contentPath;
	private boolean motionPointEnabled;
	private String langSwitchUrlPath;
	private String langSwitchLangCode;
	private boolean ioswebappcapable;

	private boolean consentRequired;
	private boolean silentConsent;
	private boolean displayInfoOnly;
	private String adviceDisplayTime;
	private String consentRequiredTrackingProvider;

	private String appleIconPath;
	private String androidIconPath;
	private String microsoftIconPath;
	private String microsoftTileColor;

	private boolean vehicleInfoAvailable;
	private String brand;
	private String carline;
	private String bodystyle;
	private String modelyear;

	private String openGraphImageReference;
	private String openGraphCanonicalUrl;
	private String openGraphSitename;
	private String openGraphFacebookAdmins;

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUse#activate()
	 */
	@Override
	public void activate() throws Exception {
		final LanguageSLRService lslrService = getSlingScriptHelper().getService(LanguageSLRService.class);
		final LevelService levelService = getSlingScriptHelper().getService(LevelService.class);
		final ConfigService configService = getSlingScriptHelper().getService(ConfigService.class);
		final CompanyService companyService = getSlingScriptHelper().getService(CompanyService.class);
		final DomainPathService domainPathService = getSlingScriptHelper().getService(DomainPathService.class);
		final CookiePrivacyService cookiePrivacyService = getSlingScriptHelper().getService(CookiePrivacyService.class);
		final VehicleDataService vehicleDataService = getSlingScriptHelper().getService(VehicleDataService.class);

		final Page lslrPage = getCurrentPage().getAbsoluteParent(levelService.getLanguageLevel());

		final Locale locale = configService.getPageLocale(lslrPage);
		this.code = locale.getLanguage() + "_" + locale.getCountry();

		final String directionCode = lslrService.getConfigValue(getCurrentPage(), LanguageConfigProperties.DIRECTION);
		if(StringUtils.equals(directionCode, "rtl")) {
			this.direction = "rtl";
		} else {
			this.direction = "ltr";
		}

		final AEMTemplateInfo pageInfo = AEMTemplateInfo.lookup(getCurrentPage());
		final PageModifier pageModifier = new PageModifier();
		String templateCssClass = "no-js";
		if (pageInfo != null) {
			String cssClass = pageInfo.getTemplateCssClass();
			if (cssClass != null) {
				templateCssClass += " " + cssClass;
			}
			if (pageModifier.isEmbedded()) {
				templateCssClass += " embedded";
			}
		}
		this.templateCss = templateCssClass;

		this.contentPath = (domainPathService != null ? domainPathService.getCountryByDomain(getRequest().getHeader("Host")).getContentPath() : StringUtils.EMPTY);

		final Resource navmetac1_res = lslrService.getResourceOfLSLRComponent(getRequest(), LSLRComponentProperties.HEADER_METANAVIGATION_PATH);

		final GMResource navmetac1_gmres = new GMResource(navmetac1_res);
		if (navmetac1_gmres.isExisting()) {
			this.motionPointEnabled = navmetac1_gmres.getChild("nav_lang_switch_c1").getPropertyAsBoolean("isMotionPointEnabled");
			this.langSwitchUrlPath = navmetac1_gmres.getChild("nav_lang_switch_c1").getPropertyAsString("urlPath");
			this.langSwitchLangCode = navmetac1_gmres.getChild("nav_lang_switch_c1").getPropertyAsString("languageCode");
		} else {
			this.motionPointEnabled = false;
			this.langSwitchUrlPath = StringUtils.EMPTY;
			this.langSwitchLangCode = StringUtils.EMPTY;
		}

		this.ioswebappcapable = companyService.getBooleanConfigValue(getCurrentPage(), CompanyConfigProperties.IOS_WEB_APP_CAPABLE, false);

	    this.consentRequired = companyService.getBooleanConfigValue(getCurrentPage(), CompanyConfigProperties.COOKIEPRIVACY_CONSENT_REQUIRED,false);
	    this.silentConsent = companyService.getBooleanConfigValue(getCurrentPage(), CompanyConfigProperties.COOKIEPRIVACY_SILENT_CONSENT_ENABLED,false);
	    this.displayInfoOnly = StringUtils.equals("consent_not_required", companyService.getConfigValue(getCurrentPage(), CompanyConfigProperties.COOKIEPRIVACY_CONSENT_REQUIRED));
	    this.adviceDisplayTime = companyService.getConfigValue(getCurrentPage(), CompanyConfigProperties.COOKIEPRIVACY_ADVICE_DISPLAY_TIME);
	    this.consentRequiredTrackingProvider = cookiePrivacyService.getTrackingProvidersAsJSONString(getCurrentPage(), "required");
	    this.appleIconPath = companyService.getConfigValue(getCurrentPage(), CompanyConfigProperties.TOUCH_ICON_APPLE);
	   	this.androidIconPath = companyService.getConfigValue(getCurrentPage(), CompanyConfigProperties.TOUCH_ANDROID_ICON);
	   	this.microsoftIconPath = companyService.getConfigValue(getCurrentPage(), CompanyConfigProperties.TOUCH_TILE_ICON_MICROSOFT);
	    this.microsoftTileColor = companyService.getConfigValue(getCurrentPage(), CompanyConfigProperties.TOUCH_TILE_ICON_MICROSOFT_TILE_COLOR);

	    final ValueMap visualizerProps = getCurrentPage().getProperties("mh_c1/multimedia_visualizer");
	    String bbcLink = (visualizerProps != null ? visualizerProps.get("bbcReference", StringUtils.EMPTY) : StringUtils.EMPTY);
	    if(StringUtils.isBlank(bbcLink)) {
	    	bbcLink = PageUtil.getPropertyFromPageIncludingAncestors(getCurrentPage(), "baseballCardLink");
	    }
	    final BodystyleBaseballcardData bbcdata = vehicleDataService.getBaseballcardData(bbcLink, getRequest());
	    if (bbcdata != null) {
	    	this.vehicleInfoAvailable = true;
		    this.brand = configService.getBrandNameFromRequest(getRequest()).getId();
		    this.carline = bbcdata.getBaseballcardProperty(BaseballcardCarlineProperties.CARLINE_CODE);
		    this.bodystyle= bbcdata.getBaseballcardProperty(BaseballcardBodystyleProperties.BODYSTYLE_CODE);
		    this.modelyear = String.valueOf(bbcdata.getModelYear());
	    } else {
	    	this.vehicleInfoAvailable = false;
	    	this.brand = StringUtils.EMPTY;
	    	this.carline = StringUtils.EMPTY;
	    	this.bodystyle = StringUtils.EMPTY;
	    	this.modelyear = StringUtils.EMPTY;
	    }

	    final String ref = get("og_image_reference", String.class);
	    final String httpHostPublish = ProtocolUtil.getHttpHostFromConfiguration(getRequest());
	    if (StringUtils.isNotBlank(ref)) {
	    	this.openGraphImageReference = httpHostPublish + ref;
	    } else {
	    	this.openGraphImageReference = StringUtils.EMPTY;
	    }

	    final String hostWithProtocol = ProtocolUtil.getHostWithProtocol(getRequest(), getCurrentPage().adaptTo(Resource.class));
	    final String relativePublisherUrl = PathUtil.getRelativePublisherUrl(getCurrentPage().getPath(), getRequest());
	    this.openGraphCanonicalUrl = hostWithProtocol + relativePublisherUrl;

	    this.openGraphSitename = companyService.getConfigValue(getCurrentPage(), CompanyConfigProperties.OPEN_GRAPH_SITE_NAME);
	    if (StringUtils.isBlank(this.openGraphSitename)) {
	    	this.openGraphSitename = httpHostPublish;
	    }

	    this.openGraphFacebookAdmins = companyService.getConfigValue(getCurrentPage(), CompanyConfigProperties.OPEN_GRAPH_FB_ADMINS);
	}

	public final String getCode() {
		return code;
	}

	public final String getDirection() {
		return direction;
	}

	public final String getTemplateCssClass() {
		return templateCss;
	}

	public final String getContentPath() {
		return this.contentPath;
	}

	public final boolean getMotionPointEnabled() {
		return this.motionPointEnabled;
	}

	public final String getLangswitchUrlPath() {
		return this.langSwitchUrlPath;
	}

	public final String getLangswitchLangCode() {
		return this.langSwitchLangCode;
	}

	public final boolean getIosWebappCapable() {
		return this.ioswebappcapable;
	}

    /**
     * @return the consentRequired
     */
    public final boolean getConsentRequired() {
    	return consentRequired;
    }

    /**
     * @return the silentConsent
     */
    public final boolean getSilentConsent() {
    	return silentConsent;
    }

    /**
     * @return the displayInfoOnly
     */
    public final boolean getDisplayInfoOnly() {
    	return displayInfoOnly;
    }

    /**
     * @return the adviceDisplayTime
     */
    public final String getAdviceDisplayTime() {
    	return adviceDisplayTime;
    }

    /**
     * @return the consentRequiredTrackingProvider
     */
    public final String getConsentRequiredTrackingProvider() {
    	return consentRequiredTrackingProvider;
    }

    /**
     * @return the appleIconPath
     */
    public final String getAppleIconPath() {
    	return appleIconPath;
    }

    /**
     * @return the androidIconPath
     */
    public final String getAndroidIconPath() {
    	return androidIconPath;
    }

    /**
     * @return the microsoftIconPath
     */
    public final String getMicrosoftIconPath() {
    	return microsoftIconPath;
    }

    /**
     * @return the microsoftTileColor
     */
    public final String getMicrosoftTileColor() {
    	return microsoftTileColor;
    }

    /**
     * @return the brand
     */
    public final String getBrand() {
    	return brand;
    }

    /**
     * @return the carline
     */
    public final String getCarline() {
    	return carline;
    }

    /**
     * @return the bodystyle
     */
    public final String getBodystyle() {
    	return bodystyle;
    }

    /**
     * @return the modelyear
     */
    public final String getModelyear() {
    	return modelyear;
    }

    /**
     * @return the vehicleInfoAvailable
     */
    public final boolean getVehicleInfoAvailable() {
    	return vehicleInfoAvailable;
    }

    /**
     * @return the openGraphImageReference
     */
    public final String getOpenGraphImageReference() {
    	return openGraphImageReference;
    }

    /**
     * @return the openGraphCanonicalUrl
     */
    public final String getOpenGraphCanonicalUrl() {
    	return openGraphCanonicalUrl;
    }

    /**
     * @return the openGraphSitename
     */
    public final String getOpenGraphSitename() {
    	return openGraphSitename;
    }

    /**
     * @return the openGraphFacebookAdmins
     */
    public final String getOpenGraphFacebookAdmins() {
    	return openGraphFacebookAdmins;
    }
}
