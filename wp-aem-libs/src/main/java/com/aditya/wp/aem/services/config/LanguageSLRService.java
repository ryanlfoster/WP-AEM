/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.config;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.aditya.wp.aem.properties.LSLRComponentProperties;
import com.aditya.wp.aem.properties.LSLRConfigProperties;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LanguageSLRService extends ConfigService {
    /**
     * <p>
     * Get the language value from the properties of the Language Template.
     * </p>
     * <p>
     * This function searches a language page at the current page or the parent pages. If no
     * language page is found at the parent pages, the function returns null. If the language page
     * is found, the property value will return.
     * </p>
     * 
     * @param currentPage
     *            the current page object of the request
     * @param property
     *            the request property (configured in {@link LSLRConfigProperties})
     * @return the value of the requested property, or null if the property is not found.
     */
    String getConfigValue(Page currentPage,
                          LSLRConfigProperties property);

    /**
     * <p>
     * Get the absolute resource path of the language page path and the requested relative path.
     * </p>
     * <table>
     * <tr>
     * <td>request resourcePath</td>
     * <td>company page path</td>
     * <td>result resourcePath</td>
     * </tr>
     * <tr>
     * <td>"cnt_legalglobal_c1"</td>
     * <td>"/content/opel/europe/master/hq/de"</td>
     * <td>"/content/opel/europe/master/hq/de/cnt_legalglobal_c1"</td>
     * </tr>
     * </table>
     * 
     * @param currentPage
     *            the current page object of the request
     * @param resourcePath
     *            the relative path of the resource at the language page (configuration in
     *            {@link LSLRComponentProperties})
     * @return the absolute resource path of the requested relative resource path. Has not the
     *         language page the requested resource, it will return null.
     */
    String getResourcePath(Page currentPage,
                           LSLRComponentProperties resourcePath);

    /**
     * Get the requested resource from the LSLR.
     * 
     * @param request
     *            the current request. needed to find the corresponding LSLR page
     * @param lslrComponent
     *            which LSLR component to retrieve.
     * @return the component as a {@link Resource}.
     */
    Resource getResourceOfLSLRComponent(SlingHttpServletRequest request,
                                        LSLRComponentProperties lslrComponent);
}
