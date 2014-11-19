/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import com.aditya.gmwp.aem.model.ShoppingLinkModel;
import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.services.vehicledata.data.BodystyleBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.ShoppingLink;
import com.aditya.gmwp.aem.utils.uri.UriBuilder;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class ShoppingLinkUtil {
    /**
     * This methods gets the external link either from bbc or lslr.
     * 
     * @param languageService
     *            the language service to get a config value from
     * @param page
     *            the page to get the external link from lslr
     * @param shoppingLink
     *            the specific shopping link to get external link for
     * @param bodystyleBaseballcardData
     *            the data to get the external link from bbc
     * @param fromLslr
     *            determines whether the external link should be retrieved from lslr or bbc
     * @return external link or <code>null</code> if external link is empty on both lslr and bbc
     */
    public static String getExternalLink(final LanguageSLRService languageService,
                                         final Page page,
                                         final ShoppingLink shoppingLink,
                                         final BodystyleBaseballcardData bodystyleBaseballcardData,
                                         final boolean fromLslr) {
        String external = null;
        if (fromLslr) {
            external = getExternalLinkFromLSLR(languageService, page, shoppingLink);
        } else {
            external = getExternalLinkFromBBC(bodystyleBaseballcardData, shoppingLink);
        }
        return external;
    }

    /**
     * This method gets the external link from the bbc.
     * 
     * @param bodystyleBaseballcardData
     *            the data used to get the external link from
     * @param shoppingLink
     *            the specific shopping link to get external link for
     * @return external link or <code>null</code> if any of the passed parameters is null
     */
    public static String getExternalLinkFromBBC(final BodystyleBaseballcardData bodystyleBaseballcardData,
                                                final ShoppingLink shoppingLink) {
        if (null == bodystyleBaseballcardData //
                || null == shoppingLink || null == shoppingLink.getExternalShoppingLink()) {
            return null;
        }
        return bodystyleBaseballcardData.getBaseballcardProperty(shoppingLink.getExternalShoppingLink());
    }

    /**
     * This method gets the external link from lslr.
     * 
     * @param languageService
     *            the language service used to get a config value
     * @param page
     *            the page used by the language service
     * @param shoppingLink
     *            the specific shopping link to get external link for
     * @return external link or <code>null</code> if any of the passed parameters is null
     */
    public static String getExternalLinkFromLSLR(final LanguageSLRService languageService,
                                                 final Page page,
                                                 final ShoppingLink shoppingLink) {
        if (null == languageService || null == page || null == shoppingLink
                || null == shoppingLink.getLslrExternalLink()) {
            return null;
        }
        return languageService.getConfigValue(page, shoppingLink.getLslrExternalLink());
    }

    /**
     * This methods gets the internal link either from bbc or lslr.
     * 
     * @param languageService
     *            the language service to get a config value from
     * @param page
     *            the page to get the internal link from lslr
     * @param shoppingLink
     *            the specific shopping link to get internal link for
     * @param bodystyleBaseballcardData
     *            the data to get the internal link from bbc
     * @param fromLslr
     *            determines whether the internal link should be retrieved from lslr or bbc
     * @return internal link or <code>null</code> if internal link is empty on both lslr and bbc
     */
    public static String getInternalLink(final LanguageSLRService languageService,
                                         final Page page,
                                         final ShoppingLink shoppingLink,
                                         final BodystyleBaseballcardData bodystyleBaseballcardData,
                                         final boolean fromLslr) {
        String internal = null;
        if (fromLslr) {
            internal = getInternalLinkFromLSLR(languageService, page, shoppingLink);
        } else {
            internal = getInternalLinkFromBBC(bodystyleBaseballcardData, shoppingLink);
        }
        return internal;
    }

    /**
     * This method gets the internal link from the bbc.
     * 
     * @param bodystyleBaseballcardData
     *            the data used to get the internal link from
     * @param shoppingLink
     *            the specific shopping link to get internal link for
     * @return internal link or <code>null</code> if any of the passed parameters is null
     */
    public static String getInternalLinkFromBBC(final BodystyleBaseballcardData bodystyleBaseballcardData,
                                                final ShoppingLink shoppingLink) {
        if (null == bodystyleBaseballcardData || //
                null == shoppingLink || null == shoppingLink.getInternalShoppingLink()) {
            return null;
        }
        return bodystyleBaseballcardData.getBaseballcardProperty(shoppingLink.getInternalShoppingLink());
    }

    /**
     * This method gets the internal link from lslr.
     * 
     * @param languageService
     *            the language service used to get a config value
     * @param page
     *            the page used by the language service
     * @param shoppingLink
     *            the specific shopping link to get internal link for
     * @return internal link or <code>null</code> if any of the passed parameters is null
     */
    public static String getInternalLinkFromLSLR(final LanguageSLRService languageService,
                                                 final Page page,
                                                 final ShoppingLink shoppingLink) {
        if (null == languageService || null == page || null == shoppingLink
                || null == shoppingLink.getLslrInternalLink()) {
            return null;
        }
        return languageService.getConfigValue(page, shoppingLink.getLslrInternalLink());
    }

    /**
     * This method determines whether the underlying internal/external shopping links are empty.
     * 
     * @param internal
     *            the internal link to check
     * @param external
     *            the external link to check
     * @return links empty
     */
    public static boolean hasLinks(final String internal,
                                   final String external) {
        if (StringUtils.isNotEmpty(internal) || StringUtils.isNotEmpty(external)) {
            return true;
        }
        return false;
    }

    /**
     * Sets the shopping link and its parameters
     * 
     * @param shoppingLinkModel the {@link ShoppingLinkModel}
     * @param internalLink the internal link
     * @param externalLink the external link
     * @param shoppingLink the {@link ShoppingLink}
     * @param shoppingLinkParameter the shopping link parameters
     * @param resourceResolver the {@link ResourceResolver}
     */
    public static void setLinks(final ShoppingLinkModel shoppingLinkModel,
                                final String internalLink,
                                final String externalLink,
                                final ShoppingLink shoppingLink,
                                final String shoppingLinkParameter,
                                final ResourceResolver resourceResolver) {
    	if (StringUtils.isNotEmpty(internalLink)) {
			shoppingLinkModel.setInternalLink(internalLink);
			if (StringUtils.isNotBlank(shoppingLinkParameter)) {
				// use the UriBuilder to parse the paramString to a map and anchors
				final StringBuilder paramString = new StringBuilder(shoppingLinkParameter);
				if (paramString.charAt(0) != '?') {
					paramString.insert(0, '?');
				}
				final UriBuilder paramUb = new UriBuilder(paramString.toString());
				shoppingLinkModel.addAllMultiParameters(paramUb.getParameters());
				shoppingLinkModel.setAnchor(paramUb.getAnchor());
			}
		} else if (StringUtils.isNotEmpty(externalLink)) {
			shoppingLinkModel.setExternalLink(externalLink, resourceResolver);
			if (StringUtils.isNotBlank(shoppingLinkParameter)) {
				// use the UriBuilder to parse the paramString to a map and anchors
				final StringBuilder paramString = new StringBuilder(shoppingLinkParameter);
				if (paramString.charAt(0) != '?') {
					paramString.insert(0, '?');
				}
				final UriBuilder paramUb = new UriBuilder(paramString.toString());
				shoppingLinkModel.addAllMultiParameters(paramUb.getParameters());
				shoppingLinkModel.setAnchor(paramUb.getAnchor());
			}
		}
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private ShoppingLinkUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}
