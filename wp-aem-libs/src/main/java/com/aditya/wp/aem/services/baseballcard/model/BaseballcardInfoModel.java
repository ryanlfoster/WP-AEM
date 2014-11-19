/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.baseballcard.model;

import java.util.Set;

import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface BaseballcardInfoModel {
    /**
     * Update the baseball card data information.
     * 
     * @param lslrPage
     *            the language page for the market
     */
    void update(final Page lslrPage);

    /**
     * Gets the bodystyle resource paths (ts_baseballcard_bodystyle)
     * @return a set of baseball card bodystyle resource paths
     */
    Set<String> getBodystyleResourcePaths();

    /**
     * Gets the carline resource paths (ts_baseballcard_carline)
     * @return a set of baseball card carline resource paths
     */
    Set<String> getCarlineResourcePaths();

    /**
     * Gets the bodystyle manual resource paths (ts_baseballcard_bodystyle_manual)
     * @return a set of baseball card manual bodystyle resource paths
     */
    Set<String> getManualBodystyleResourcePaths();

    /**
     * Gets the configuration resource paths (ts_baseballcard_configuration)
     * @return a set of baseball card configuration resource paths
     */
    Set<String> getConfigurationResourcePaths();
}
