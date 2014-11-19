/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core;


/**
 * 
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 * @param <T> type of cached data
 */
public interface CacheService<T> {
	/**
	 * Logic to execute to load/persist data every so often.
	 */
	void run();

	/**
	 * Flushes the cached data.
	 */
	void flushCache();
}
