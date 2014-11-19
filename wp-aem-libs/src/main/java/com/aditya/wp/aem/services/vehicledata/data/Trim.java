/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface Trim {
	/**
     * The method returns the series code.
     * 
     * @return the title series code
     */
    String getSeriesCode();

    /**
     * The method sets the series code.
     * 
     * @param seriesCode
     *            the series code
     */
    void setSeriesCode(String seriesCode);

    /**
     * The method returns the title.
     * 
     * @return the title
     */
    String getTitle();

    /**
     * The method sets the title.
     * 
     * @param title
     *            the title
     */
    void setTitle(String title);

    /**
     * The method returns the price.
     * 
     * @return the price
     */
    String getPrice();

    /**
     * The method returns the net price.
     * 
     * @return the net price
     */
    String getNetPrice();

    /**
     * The method sets the price.
     * 
     * @param price
     *            the price
     */
    void setPrice(String price);

    /**
     * The method sets the net price.
     * 
     * @param netPrice
     *            the net price
     */
    void setNetPrice(String netPrice);

    /**
     * The method returns the description.
     * 
     * @return the description
     */
    String getDescription();

    /**
     * The method sets the description.
     * 
     * @param description
     *            the description
     */
    void setDescription(String description);

    /**
     * The method returns the feature list.
     * 
     * @return the feature list
     */
    String getFeatureList();

    /**
     * The method sets the feature list.
     * 
     * @param featureList
     *            list the feature list
     */
    void setFeatureList(String featureList);

    /**
     * The method checks whether the automatically created "More Details" link should be overridden
     * or not.
     * 
     * @return true if the automatically created "More Details" link should be overridden, or not
     */
    boolean isOverrideLinkTarget();

    /**
     * The method sets whether the automatically created "More Details" link should be overridden or
     * not.
     * 
     * @param overrideLinkTarget
     *            true if the automatically created "More Details" link should be overridden, false
     *            otherwise
     */
    void setOverrideLinkTarget(boolean overrideLinkTarget);

    /**
     * The method returns the target of the more details link.
     * 
     * @return the target of the more details link
     */
    String getTargetOfMoreDetailsLink();

    /**
     * The method sets target of the more details link.
     * 
     * @param targetOfMoreDetailsLink
     *            target of the more details link
     */
    void setTargetOfMoreDetailsLink(String targetOfMoreDetailsLink);

    /**
     * The method returns the deeplinking target.
     * 
     * @return the deeplinking target
     */
    String getDeeplinkingTarget();

    /**
     * The method sets deeplinking target.
     * 
     * @param deeplinkingTarget
     *            the deeplinking target
     */
    void setDeeplinkingTarget(String deeplinkingTarget);
}
