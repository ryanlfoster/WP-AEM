/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.impl;

import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.http.HttpContext;

import com.aditya.gmwp.aem.services.config.ConfigService;
import com.aditya.gmwp.aem.services.config.LevelService;
import com.aditya.gmwp.aem.services.core.ErrorService;
import com.aditya.gmwp.aem.services.core.JcrService;
import com.aditya.gmwp.aem.utils.WCMModeUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(value = ErrorService.class)
@Component(name = "com.aditya.gmwp.aem.services.core.ErrorService", label = "GMWP ErrorService", description = "This service helps handling error pages.", metatype = true)
public class ErrorServiceImpl implements ErrorService {
	
    private static final String SERVICE_NAMESPACE = "errorservice.";

    @Property(label = "Use Simple Error Page", description = "If the checkbox is checked the simple error page will be rendered.", boolValue = true)
    private static final String USE_SIMPLE_ERROR_PAGE = SERVICE_NAMESPACE + "usesimpleerrorpage";
    private boolean useSimpleErrorPage = true;

    /** The Constant DEFAULT_ERROR_PATH_POST. */
    private static final String DEFAULT_ERROR_PATH_POST = "/index/tools/";

    /** The Constant SELECTOR_FOOTER. */
    private static final String SELECTOR_FOOTER = ".footer";

    /** The Constant SELECTOR_HTML_HEAD. */
    private static final String SELECTOR_HTML_HEAD = ".htmlhead";

    /** The Constant SELECTOR_PAGE_HEAD. */
    private static final String SELECTOR_PAGE_HEAD = ".pagehead";

    @Reference
    private ConfigService configService;

    @Reference
    private LevelService levelService;
    
    @Reference
    private JcrService jcrService;

    /**
     * Reference will be only set in unit test.
     */
    private PageManager testPageManager = null;

    /**
     * Reference will be only set in unit test.
     */
    private ResourceResolver testResourceResolver = null;

    @Activate
    protected void activate(final Map<String, Object> config) {
    	this.useSimpleErrorPage = PropertiesUtil.toBoolean(config.get(USE_SIMPLE_ERROR_PAGE), false);
    }

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.ErrorService#getErrorPath(javax.servlet.http.HttpServletRequest, com.day.cq.wcm.api.Page, org.apache.sling.api.resource.Resource, int)
	 */
	@Override
	public String getErrorPath(HttpServletRequest request,
	                           Page currentPage,
	                           Resource resource,
	                           int errorCode) throws ParseException {
		if (resource.getPath().indexOf("/system/error/500") >= 0) {
            throw new IllegalStateException("Recursiv call of " + errorCode + " page");
        }
        String errorPath = StringUtils.EMPTY;
        String language = StringUtils.EMPTY;
        String languageName = StringUtils.EMPTY;
        String salesPath = StringUtils.EMPTY;

        // page to be used to retrieve inforamtion
        Page page = null;

        final Resource requestedResource = resource.getResourceResolver().resolve(request, request.getPathInfo());

        Locale local = null;
        if (currentPage != null) {
            page = currentPage;
            local = page.getLanguage(true);
        } else {
            // Declaring initial variables needed for author error redirect
            final String resourcePath = requestedResource.getPath();
            final String marketPath = this.configService.getMarketNameFromPath(resourcePath);
            final int nscwebsiteLevel = this.levelService.getSalesLevel(); // = 4;
            final int languageLevel = this.levelService.getLanguageLevel(); // = 5;

            // author can pick the page only if language is available on author
            final String errorPathUrl = StringUtils.substringBefore(resourcePath, marketPath) + marketPath + "/"
                    + this.configService.getElementFromContentPath(resourcePath, nscwebsiteLevel) + "/"
                    + this.configService.getElementFromContentPath(resourcePath, languageLevel)
                    + DEFAULT_ERROR_PATH_POST + errorCode;

            if (!isAuthorInstance(request)) {
                page = getPageManager().getPage(errorPathUrl);
                if (page == null) {
                    throw new ParseException("Page  with path: " + errorPathUrl + " does not exist.", 1);
                }
                local = page.getLanguage(false);

            } else {
                // Get the handle to the market page and add the error page
                page = getPageManager().getPage(errorPathUrl);
                if (page == null) {
                    // can happen when pathToMarketErrorPage is not valid
                    // e.g. /opel/europe/master/hqasdf
                    throw new ParseException("Page  with Path: " + errorPathUrl + " does not exist.", 1);
                }
                // page content is not ignored
                local = page.getLanguage(false);
            }
        }
        // is current page deeper than Sales Level
        if (page.getDepth() <= this.levelService.getSalesLevel()) {
            throw new IllegalStateException("Out of Level" + errorCode + " page");
        } else {
            // path to Sales Level
            final Page salesPage = page.getAbsoluteParent(this.levelService.getSalesLevel());
            salesPath = salesPage.getPath();
        }

        // is current page deeper than Language level
        if (page.getDepth() <= this.levelService.getLanguageLevel()) {
            languageName = page.getName();
        } else {
            // name of page
            final Page languagePage = page.getAbsoluteParent(this.levelService.getLanguageLevel());
            languageName = languagePage.getName();
        }

        // do exists language page
        if (languageName.equals(local.getLanguage())) {
            language = local.getLanguage();
            errorPath = salesPath + "/" + language + DEFAULT_ERROR_PATH_POST + errorCode;
        } else {
            errorPath = salesPath + DEFAULT_ERROR_PATH_POST + errorCode;
        }

        // exists error template
        final Page errorPage = getPageManager().getPage(errorPath);
        if (errorPage == null) {
            throw new IllegalStateException("There is no " + errorCode + " page configured!");
        }
        return errorPath;
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.ErrorService#isAnonymousUser(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean isAnonymousUser(HttpServletRequest request) {
		return request.getAttribute(HttpContext.AUTHENTICATION_TYPE) == null || request.getRemoteUser() == null || "anonymous".equals(request.getRemoteUser());
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.ErrorService#isAuthorInstance(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean isAuthorInstance(HttpServletRequest request) {
		return WCMModeUtil.isAuthorInstance(request);
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.ErrorService#isHumanRequest(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean isHumanRequest(HttpServletRequest request) {
		final String requestUri = request.getRequestURI();
        if (requestUri.endsWith(".html")) {
            if (isWebWrappingRequest(requestUri)) {
                return false;
            }
            return true;
        } else {
            if (requestUri.startsWith("/content/dam")) {
                return false;
            }
            if (requestUri.startsWith("/dam")) {
                return false;
            }
            if (requestUri.startsWith("/var/dam")) {
                return false;
            }
            return true;
        }
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.ErrorService#isUseSimpleErrorPage()
	 */
	@Override
	public boolean isUseSimpleErrorPage() {
		return this.useSimpleErrorPage;
	}

	/**
     * Gets the page manager. If a testPageManager was set, then the testPageManager will be returned, otherwise the
     * PageManager from {@link AbstractRepositoryService}.
     * 
     * @return the page manager
     */
    protected final PageManager getPageManager() {
        if (this.testPageManager != null) {
            return this.testPageManager;
        }
        return this.jcrService.getPageManager();
    }

    /**
     * Gets the resource resolver. If a testResourceResolver was set, then the testResourceResolver will be returned,
     * otherwise the ResourceResolver from {@link AbstractRepositoryService}.
     * 
     * @return the resource resolver
     */
    protected final ResourceResolver getResourceResolver() {
        if (this.testResourceResolver != null) {
            return this.testResourceResolver;
        }
        return this.jcrService.getResourceResolver();
    }

	/**
     * Checks if is web wrapping request.
     * 
     * @param requestUri
     *            the request uri
     * @return true, if is web wrapping request
     */
    private boolean isWebWrappingRequest(final String requestUri) {
        return requestUri.contains(SELECTOR_HTML_HEAD) || requestUri.contains(SELECTOR_PAGE_HEAD)
                || requestUri.contains(SELECTOR_FOOTER);
    }
}
