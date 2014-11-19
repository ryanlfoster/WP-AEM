/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.wrapper;

import java.util.HashMap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class DeepResolvingResourceUtil {
	private static final Logger LOG = LoggerFactory.getLogger(DeepResolvingResourceUtil.class);

	/**
	 * return a deepResourceValueMap.
	 * 
	 * @param resource
	 *            _
	 * @return _
	 */
	public static ValueMap getValueMap(final Resource resource) {
		if (resource == null) {
			return new ValueMapDecorator(new HashMap<String, Object>());
		} else {
			return new DeepResolvingValueMap(resource, ResourceUtil.getValueMap(resource));
		}
	}

	/**
     * 
     */
	private DeepResolvingResourceUtil() {

	}

	/**
	 * returns a String value for the propertyName passed in for a given node.
	 * 
	 * @param node
	 * @param propertyName
	 *            _
	 * @return _
	 */
	public static String getValue(final Node node,
	                              final String propertyName) {
		String value = "";
		try {
			value = node.getProperty(propertyName).getString();
		} catch (PathNotFoundException e) {
			LOG.debug(e.getMessage());
		} catch (RepositoryException e) {
			LOG.debug(e.getMessage());
		}
		return value;
	}
}
