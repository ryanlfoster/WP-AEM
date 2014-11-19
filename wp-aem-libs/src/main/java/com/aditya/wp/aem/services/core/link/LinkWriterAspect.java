/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.link;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LinkWriterAspect {

	/**
	 * Applies this aspect on the passed {@link HTMLLink}.
	 *
	 * @param htmlLink
	 *            the link to be modified. Must not be null.
	 * @param request
	 */
	void applyTo(final HTMLLink htmlLink);

	/**
	 * Whether this aspect can be applied to links, depending on the information passed in the
	 * constructor parameters.
	 *
	 * @return true if this aspect is applicable. Calling applyTo() when isApplicable() returns
	 *         false may have undesired results.
	 */
	boolean isApplicable();
}
