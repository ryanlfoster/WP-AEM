/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 * @param <T> service implemenation class; needs to extend the {@link AbstractService}
 */
public abstract class AbstractService<T extends AbstractService<T>> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractService.class);
	private Map<String, Logger> loggers = new LinkedHashMap<String, Logger>();

	/**
	 * Gets the logger for writing log statements.
	 * @param as the concrete osgi service implementation class
	 * @return a logger associated with the osgi service implementation class.
	 */
	public final Logger getLog(final AbstractService<T> as) {
		if (this.loggers == null) {
			this.loggers = new LinkedHashMap<String, Logger>();
		}
		if (as == null) {
			return LOG;
		}

		if (this.loggers.containsKey(as.getClass().getName())) {
			return this.loggers.get(as.getClass().getName());
		}

		this.loggers.put(as.getClass().getName(), LoggerFactory.getLogger(as.getClass()));
		return this.loggers.get(as.getClass().getName());
	}
}
