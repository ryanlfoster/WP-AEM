/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.config;

import java.util.SortedMap;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.wp.aem.utils.html.LinkBehavior;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LinkBehaviorService {
	
	SortedMap<String, LinkBehavior> getLinkConfigurationBehaviors(Page currentPage);

	SortedMap<String, LinkBehavior> getLinkConfigurationBehaviors(SlingHttpServletRequest request);

	LinkBehavior getLinkConfigurationBehavior(Page currentPage, String name);

	LinkBehavior getLinkConfigurationBehavior(SlingHttpServletRequest request, String name);
}
