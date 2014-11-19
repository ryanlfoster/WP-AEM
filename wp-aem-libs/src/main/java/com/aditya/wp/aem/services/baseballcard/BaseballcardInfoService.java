/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.baseballcard;

import java.util.Set;

import com.aditya.gmwp.aem.services.baseballcard.model.BaseballcardInfoModel;
import com.aditya.gmwp.aem.services.core.CacheService;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface BaseballcardInfoService extends CacheService<BaseballcardInfoModel> {
	/**
	 * Gets the baseball card information model associated with the page.
	 * 
	 * @param currentPage the current page
	 * @return the {@link BaseballcardInfoModel}
	 */
	BaseballcardInfoModel getBaseballcardData(final Page currentPage);

	/**
	 * Gets a set of baseball card resource paths associated with the page.
	 * 
	 * @param currentPage the current page
	 * @param resourceType the resource type
	 * @return a set of resource paths
	 */
	Set<String> getBaseballcardResource(final Page currentPage, final String resourceType);
}
