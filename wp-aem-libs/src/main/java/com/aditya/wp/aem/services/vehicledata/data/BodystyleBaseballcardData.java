/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;

import com.aditya.wp.aem.global.LegalPriceContext;
import com.aditya.wp.aem.global.ShoppingLinkContext;
import com.aditya.wp.aem.model.ShoppingLinkModel;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface BodystyleBaseballcardData extends BaseballcardData {

    /**
     * Gets the bodystyle page.
     * 
     * @return bodystyle page
     */
    Page getPage();

    /**
     * Composes the title of the carline name and the page name of the bodystyle. Can be used as
     * fallback value, when no title is defined in DDP.
     * 
     * @return the title
     */
    String getBaseballCardTitle();

    /**
     * Composes the title of the carline name (optional) and the page name of the bodystyle. Can be
     * used as fallback value, when no title is defined in DDP.
     * 
     * @param showCarlineText
     *            include carline name or not
     * @return the title with carline name or not
     */
    String getBaseballCardTitle(final boolean showCarlineText);

    /**
     * Composes the title of the carline name (optional).
     * 
     * @return the carline title
     */
    String getCarlineTitle();

    /**
     * Composes the title of the carline name (optional).
     * 
     * @param showCarlineText
     *            show carline name or not
     * @return the carline title
     */
    String getCarlineTitle(final boolean showCarlineText);

    /**
     * get the legal price suffix.
     * 
     * @param legalPriceContext
     *            the legalPriceContext
     * @return the suffix
     */
    String getLegalPriceSuffix(final LegalPriceContext legalPriceContext);

    /**
     * Returns true if gross & netto price should be used instead of the standard price.
     * 
     * @return true, if gross & netto price should be used
     */
    boolean useGrossAndNetPrice();

    /**
     * Convenience method to simplify access to the model-year property by handling the parsing to
     * an int.
     * 
     * @return the model year or {@link Carline#INVALID_MODEL_YEAR} if this bodystyles carline does
     *         not depend on a model year.
     * @see Carline#INVALID_MODEL_YEAR
     */
    int getModelYear();

    /**
     * Returns the shopping parameter taken form the market.xml. The function only works if the
     * related bodystyle was set via {@link #setRelatedBodystyle} before calling it.
     * 
     * @param shoppingLink
     *            the {@link ShoppingLink} in question
     * @return the additional shopping link parameters
     */
    String getShoppingLinkParameter(final ShoppingLink shoppingLink);

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
    List<ShoppingLinkModel> getTemplateShoppingLinks(final ShoppingLinkContext shoppingLinkContext,
                                                     final ResourceResolver resourceResolver) throws RepositoryException;

    /**
     * Indicates weather the baseball card is a duplicate as set by the author. See GMDSPLM-2983 for
     * details.
     * 
     * @return true if duplicate, false otherwise.
     */
    boolean isDuplicateBaseballCard();

    /**
     * Checks if the baseball card in question is a fallback card.
     * 
     * @return true, if is fallback baseball card
     */
    boolean isFallbackBaseballCard();

    /**
     * Returns the carline baseballcard data bodystyle is associated with.
     * 
     * @return carline baseballcard data
     */
    CarlineBaseballcardData getCarlineBaseballcardData();

    /**
     * The method returns a list of all series or an empty list if no series were found.
     * 
     * @return a list of all series or an empty list if no series were found
     */
    List<Series> getSeries();

    /**
     * The method returns the series with the given series code.
     * 
     * @param seriesCode
     *            the series code
     * @return the series with the given series code or null if not found
     */
    Series getSeries(String seriesCode);

    /**
     * The method returns the series with the given alternate Id.
     * 
     * @param alternateId
     *            the alternateId for series code
     * @return the series with the given series code or null if not found
     */
    Series getSeriesByAlternateId(String alternateId);

    /**
     * The method returns a list of all trims or an empty list if no trims were found.<br/>
     * <br/>
     * The data of the trim and the series will be automatically merged.
     * 
     * @return a list of all trims or an empty list if no trims were found
     */
    List<Trim> getTrims();

    /**
     * The method returns the first trim with the given series code.<br/>
     * <br/>
     * The data of the trim and the series will be automatically merged.
     * 
     * @param seriesCode
     *            the series code
     * @return the first trim with the given series code or null if not found
     */
    Trim getTrim(String seriesCode);
}
