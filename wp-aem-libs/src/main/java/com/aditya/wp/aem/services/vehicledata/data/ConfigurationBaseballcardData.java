/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;

import com.aditya.gmwp.aem.global.ShoppingLinkContext;
import com.aditya.gmwp.aem.model.ShoppingLinkModel;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface ConfigurationBaseballcardData extends BaseballcardData {
    /**
     * Gets the defined shopping links of the given shopping link context.
     * 
     * @param shoppingLinkContext
     *            the shopping link context
     * @param resourceResolver
     *            the resource resolver
     * @return the template shopping links
     * @throws RepositoryException
     *             the repository exception
     */
    List<ShoppingLinkModel> getTemplateShoppingLinks(ShoppingLinkContext shoppingLinkContext,
                                                     ResourceResolver resourceResolver) throws RepositoryException;

    /**
     * Returns the bodystyle baseballcard data configuration is associated with.
     * 
     * @return bodystyle baseballcard data
     */
    BodystyleBaseballcardData getBodystyleBaseballcardData();
}
