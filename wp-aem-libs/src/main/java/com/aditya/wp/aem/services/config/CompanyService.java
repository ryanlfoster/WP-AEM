/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.config;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.properties.CompanyConfigProperties;
import com.aditya.gmwp.aem.properties.CompanyConfigResourcePath;
import com.aditya.gmwp.aem.services.vehicledata.data.ShoppingLink;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface CompanyService extends ConfigService, LinkBehaviorService {

	public static final String SHOPPING_LINK_PROPERTY = "shopping_links_order";

	/**
	 * <p>
	 * Get the configuration value as a boolean value from the properties of the company template.
	 * </p>
	 * <p>
	 * This function searches a company page at the current page or the parent pages. If no company
	 * page is found at the parent pages, the function returns null. If the company page is found,
	 * the property value will return.
	 * </p>
	 *
	 * @param currentPage
	 *            the current page object of the request
	 * @param property
	 *            the request property (configuration in {@link CompanyConfigProperties})
	 * @param fallbackValueIfEmpty
	 *            the value that should be returned if the value for the given property has not been
	 *            maintained.
	 * @return the found property value converted to a boolean or the given fall-back value, if the
	 *         property is not maintained.
	 */
	boolean getBooleanConfigValue(Page currentPage,
	                              CompanyConfigProperties property,
	                              boolean fallbackValueIfEmpty);

	/**
	 * <p>
	 * Get the configuration value from the properties of the company template.
	 * </p>
	 * <p>
	 * This function searches a company page at the current page or the parent pages. If no company
	 * page is found at the parent pages, the function returns null. If the company page is found,
	 * the property value will return.
	 * </p>
	 *
	 * @param currentPage
	 *            the current page object of the request
	 * @param property
	 *            the request property (configuration in {@link CompanyConfigProperties})
	 * @return the value of the requested property, or null, if the property is not found.
	 */
	String getConfigValue(Page currentPage,
	                      CompanyConfigProperties property);

	/**
	 * <p>
	 * Get the configuration value array from the properties of the company template.
	 * </p>
	 * <p>
	 * This function searches a company page at the current page or the parent pages. If no company
	 * page is found at the parent pages, the function returns null. If the company page is found,
	 * the property value array will return.
	 * </p>
	 *
	 * @param currentPage
	 *            the current page object of the request
	 * @param property
	 *            the request property (configuration in {@link CompanyConfigProperties})
	 * @return the value array of the requested property, or null, if the property is not found.
	 */
	String[] getConfigValueArray(Page currentPage,
	                             CompanyConfigProperties property);

	/**
	 * Gets the ordered shopping link list.
	 *
	 * @param currentPage
	 *            the current page
	 * @return the ordered shopping link list
	 * @throws RepositoryException
	 *             the repository exception
	 */
	List<ShoppingLink> getOrderedShoppingLinkList(Page currentPage) throws RepositoryException;

	/**
	 * Gets the richtext font list.
	 *
	 * @param currentPage
	 *            the current page
	 * @return the richtext font list
	 * @throws RepositoryException
	 *             the repository exception
	 */
	List<String> getRichtextFontList(Page currentPage) throws RepositoryException;

	/**
	 * Gets all available richtext fonts.
	 *
	 * @param currentPage
	 *            the current page
	 * @return the list of richtext font value maps
	 * @throws RepositoryException
	 *             the repository exception
	 */
	List<ValueMap> getAllRichtextFonts(Page currentPage) throws RepositoryException;

	/**
	 * <p>
	 * Get the absolute resource path of the requested resource with relative path.
	 * </p>
	 * <table>
	 * <tr>
	 * <td>request resourcePath</td>
	 * <td>company page path</td>
	 * <td>result resourcePath</td>
	 * </tr>
	 * <tr>
	 * <td>"divisional_logo"</td>
	 * <td>"/content/opel/europe/master"</td>
	 * <td>"/content/opel/europe/master/divisional_logo"</td>
	 * </tr>
	 * </table>
	 *
	 * @param currentPage
	 *            the current page object of the request
	 * @param resourcePath
	 *            the relative path of the resource at the company page (configuration in
	 *            {@link CompanyConfigResourcePath})
	 * @return the absolute resource path of the requested relative resource path. Has not the
	 *         company page the requested resource, it will return null.
	 */
	String getResourcePath(final Page currentPage, final CompanyConfigResourcePath resourcePath);
}
