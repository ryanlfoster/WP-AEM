/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.impl;

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.properties.CompanyConfigProperties;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.ConfigService;
import com.aditya.gmwp.aem.services.config.LevelService;
import com.aditya.gmwp.aem.services.tracking.TestAndTargetService;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(TestAndTargetService.class)
@Component(name = "com.aditya.gmwp.aem.services.tracking.TestAndTargetService")
public class TestAndTargetServiceImpl implements TestAndTargetService {

	private static final Logger LOG = LoggerFactory.getLogger(TestAndTargetServiceImpl.class);

	private static final String MBOX_PATH_SUFFIX_COMPANY_TEMPLATE = "testandtarget_config/mbox.js";

	private static final String MBOX_PATH_SUFFIX_CURRENT_PAGE = "mbox.js";

	private static final String MBOX_NAME_PLACEHOLDER = "${MBOX_NAME}";

	private static final String MBOX_CODE = "<div class=\"mboxDefault\"></div><script type=\"text/javascript\">mboxCreate(\"" + MBOX_NAME_PLACEHOLDER
	        + "\", \"path=\" + location.pathname);</script>";

	private static final String UNDERSCORE = "_";

	private static final String SLASH = "/";

	private static final String GLOBAL = "global";

	private static final String TRUE = "true";

	@Reference
	private transient final CompanyService companyService = null;

	@Reference
	private transient final ConfigService configService = null;

	@Reference
	private transient final LevelService levelService = null;
	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.tracking.TestAndTargetService#isEnabled(com.day.cq.wcm.api.Page)
	 */
	@Override
	public boolean isEnabled(Page currentPage) {
		final Page companyPage = currentPage.getAbsoluteParent(this.levelService.getCompanyLevel());
		if (companyPage != null) {
			final Resource mboxResourceOnCompany = companyPage.getContentResource(MBOX_PATH_SUFFIX_COMPANY_TEMPLATE);
			final boolean isEnabledOnCompany = this.companyService.getBooleanConfigValue(currentPage,
			        CompanyConfigProperties.TEST_AND_TARGET_GLOBAL_MBOX_ENABLED, false);

			return isEnabledOnCompany && mboxResourceOnCompany != null;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.tracking.TestAndTargetService#isGlobalMboxEnabled(com.day.cq.wcm.api.Page)
	 */
	@Override
	public boolean isGlobalMboxEnabled(Page currentPage) {
		final boolean isEnabledOnCurrentPage = TRUE.equals(currentPage.getProperties().get("testandtarget_enabled", String.class));
		return isEnabled(currentPage) && isEnabledOnCurrentPage;
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.tracking.TestAndTargetService#getGlobalMboxJSPath(com.day.cq.wcm.api.Page)
	 */
	@Override
	public String getGlobalMboxJSPath(Page currentPage) {
		final Page companyPage = currentPage.getAbsoluteParent(this.levelService.getCompanyLevel());
		final String mboxCompanyPath = companyPage.getPath() + SLASH + JcrConstants.JCR_CONTENT + SLASH + MBOX_PATH_SUFFIX_COMPANY_TEMPLATE;
		final String mboxCurrentPagePath = currentPage.getPath() + SLASH + JcrConstants.JCR_CONTENT + SLASH + MBOX_PATH_SUFFIX_CURRENT_PAGE;
		return currentPage.getContentResource(MBOX_PATH_SUFFIX_CURRENT_PAGE) != null ? mboxCurrentPagePath : mboxCompanyPath;
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.tracking.TestAndTargetService#getGlobalMboxCode(com.day.cq.wcm.api.Page)
	 */
	@Override
	public String getGlobalMboxCode(Page currentPage) {
		return MBOX_CODE.replace(MBOX_NAME_PLACEHOLDER, getMboxName(currentPage));
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.tracking.TestAndTargetService#getEscapedGlobalMboxCode(com.day.cq.wcm.api.Page)
	 */
	@Override
	public String getEscapedGlobalMboxCode(Page currentPage) {
		return escapeMboxCode(MBOX_CODE.replace(MBOX_NAME_PLACEHOLDER, getMboxName(currentPage)));
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.tracking.TestAndTargetService#isSiteCatalystPluginEnabled(com.day.cq.wcm.api.Page)
	 */
	@Override
	public boolean isSiteCatalystPluginEnabled(Page currentPage) {
		final Page companyPage = currentPage.getAbsoluteParent(this.levelService.getCompanyLevel());
		if (companyPage != null) {
			final boolean isEnabledOnCompany = this.companyService.getBooleanConfigValue(currentPage,
			        CompanyConfigProperties.TEST_AND_TARGET_SITECATALYST_PLUGIN_ENABLED, false);

			return isEnabledOnCompany;
		}
		return false;
	}


	/**
	 * Returns the mbox name for the current page. Either the default auto-generated one or the
	 * overwritten one from the page properties.
	 *
	 * @param currentPage
	 *            the current page
	 * @return the mbox name for the current page
	 */
	private String getMboxName(final Page currentPage) {
		String mboxName = currentPage.getProperties().get("testandtarget_mboxname", String.class);
		if (StringUtils.isBlank(mboxName)) {
			mboxName = generateMboxName(currentPage);
		}
		return mboxName;
	}

	/**
	 * Generates the mbox name consisting of the market name, the brand, the language and the page
	 * name.
	 *
	 * @param currentPage
	 *            the current page
	 * @return the mbox name for the current page
	 */
	private String generateMboxName(final Page currentPage) {
		String marketName = StringUtils.EMPTY;
		String brand = StringUtils.EMPTY;
		String language = StringUtils.EMPTY;
		try {
			brand = this.configService.getBrandNameFromPath(currentPage.getPath()).getName().toLowerCase(Locale.ENGLISH);
			marketName = this.configService.getMarketNameFromPath(currentPage.getPath()).toLowerCase(Locale.ENGLISH);
			language = this.configService.getPageLocale(currentPage).getDisplayLanguage(Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
		} catch (ParseException e) {
			LOG.error("Not able to generate mbox name for the current page: '" + currentPage.getPath() + "'.", e);
		}
		final String pageName = currentPage.getName();
		return marketName + UNDERSCORE + brand + UNDERSCORE + GLOBAL + UNDERSCORE + language + UNDERSCORE + pageName;
	}

	/**
	 * Escapes the squared brackets open and close so that the mbox code can be written out by
	 * document.write(unescape(...)).
	 *
	 * @param mboxCode
	 *            the mbox code to be escaped
	 * @return the escaped mbox code
	 */
	private String escapeMboxCode(final String mboxCode) {
		return mboxCode.replace("<", "%3C").replace(">", "%3E");
	}
}
