/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.dictionary;

import java.util.ResourceBundle;

import javax.servlet.jsp.PageContext;

import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public final class Dictionary {

	private static final Logger LOG = LoggerFactory.getLogger(Dictionary.class);

	/**
	 * The method returns the Dictionary.
	 * 
	 * @param pageContext
	 *            the PageContext
	 * @return the Dictionary
	 */
	public static Dictionary getDictionary(final PageContext pageContext) {
		final SlingHttpServletRequest slingHttpServletRequest = (SlingHttpServletRequest) pageContext.getAttribute("slingRequest");
		if (slingHttpServletRequest == null) {
			LOG.info("#Dictionary: SlingHttpServletRequest was null.");
		}
		// the slingHttpServletRequest my be null
		return getDictionary(slingHttpServletRequest);
	}

	/**
	 * The method returns the Dictionary.
	 * 
	 * @param request
	 *            the SlingHttpServletRequest
	 * @return the Dictionary
	 */
	public static Dictionary getDictionary(final SlingHttpServletRequest request) {
		if (request == null) {
			LOG.info("#Dictionary: The parameter request was not given.");
			return new Dictionary(null);
		}
		final ResourceBundle resourceBundle = request.getResourceBundle(request.getLocale());
		return new Dictionary(resourceBundle);
	}

	private final ResourceBundle resourceBundle;

	/**
	 * The method returns the Dictionary.
	 * 
	 * @param resourceBundle
	 *            the ResourceBundle
	 */
	private Dictionary(final ResourceBundle resourceBundle) {
		if (resourceBundle == null) {
			LOG.info("#Dictionary: No resource bundle available.");
		}
		this.resourceBundle = resourceBundle;
	}

	/**
	 * Gets an entry for the given key from this dictionary. If an exception occurs, the method
	 * returns an empty string. The method logs an exception as info.
	 * 
	 * @param key
	 *            the key for the desired entry
	 * @return the entry for the given key
	 */
	public String getEntry(final String key) {
		return getEntry(key, null);
	}

	/**
	 * Gets an entry for the given key from this dictionary. If an exception occurs, the method
	 * returns the given default entry or an empty string if no default entry was given. The method
	 * logs an exception as info.
	 * 
	 * @param key
	 *            the key for the desired entry
	 * @param defaultEntry
	 *            the default entry
	 * @return the entry for the given key
	 */
	public String getEntry(final String key,
	                       final String defaultEntry) {

		String entry = "";
		if (defaultEntry != null) {
			entry = defaultEntry;
		}

		if (this.resourceBundle == null || key == null) {
			LOG.info("#getEntry: Couldn't get the entry. The resource bundle or the key is not available.");
			return entry;
		}

		entry = this.resourceBundle.getString(key);

		return entry;
	}
}
