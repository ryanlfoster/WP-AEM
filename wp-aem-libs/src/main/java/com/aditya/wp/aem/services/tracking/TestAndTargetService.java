/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking;

import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface TestAndTargetService {

	/**
	 * Returns true if on the Company Template the Test&Target function is enabled and the mbox.js
	 * is uploaded.
	 *
	 * @param currentPage
	 *            the current page
	 * @return true if on the Company Template the Test&Target function is enabled and the mbox.js
	 *         is uploaded
	 */
	boolean isEnabled(final Page currentPage);

	/**
	 * Returns true if the Test&Target function is enabled on the Company Template and on the
	 * current page.
	 *
	 * @param currentPage
	 *            the current Page
	 * @return true if the Test&Target function is enabled on the Company Template and on the
	 *         current page
	 */
	boolean isGlobalMboxEnabled(final Page currentPage);

	/**
	 * Returns the global mbox JavaScript either from the Company Template or the current page if
	 * possible.
	 *
	 * @param currentPage
	 *            the current page
	 * @return the global mbox JavaScript either from the Company Template or the current page if
	 *         possible
	 */
	String getGlobalMboxJSPath(final Page currentPage);

	/**
	 * Returns the HTML code snipped for the mbox.
	 *
	 * @param currentPage
	 *            the current page
	 * @return the HTML code snipped for the mbox
	 */
	String getGlobalMboxCode(final Page currentPage);

	/**
	 * Returns the HTML code snipped for the mbox where the opening and closing squared brackets are
	 * escaped. This way the code can be written our by the usage of the JavaScript document.write
	 * function together with the unescape function.
	 *
	 * @param currentPage
	 *            the current page
	 * @return the escaped HTML code snipped for the mbox
	 */
	String getEscapedGlobalMboxCode(final Page currentPage);

	/**
	 * Returns true, if the sitecatalyst plugin is enabled.
	 *
	 * @param currentPage
	 *            the current page
	 * @return true, if enabled.
	 */
	boolean isSiteCatalystPluginEnabled(final Page currentPage);
}
