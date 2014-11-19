/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.vi.price.capi;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface PriceConfiguration {
    /**
     * This method returns whether overwrite ssi price.
     * 
     * @return overwrite ssi price
     */
    boolean getOverwriteSsi();

    /**
     * This method returns the price label.
     * 
     * @return the price label
     */
    String getPriceLabel();

    /**
     * This method returns whether price label should be right aligned.
     * 
     * @return price label right aligned.
     */
    boolean getPriceLabelRightAligned();

    /**
     * This method returns the short price label.
     * 
     * @return the short price label
     */
    String getShortPriceLabel();

    /**
     * This method returns whether show price at all.
     * 
     * @return show price at all
     */
    boolean getShowPrice();

    /**
     * This method returns the gross price suffix.
     * 
     * @return the gross price suffix
     */
    String getGrossPriceSuffix();

    /**
     * This method returns the net price suffix.
     * 
     * @return the net price suffix
     */
    String getNetPriceSuffix();
}
