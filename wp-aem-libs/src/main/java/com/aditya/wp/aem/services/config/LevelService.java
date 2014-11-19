/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.config;

import com.day.cq.wcm.api.Page;


/**
 * The level service provides the following levels.
 * <ul>
 * <li>brand</li>
 * <li>region</li>
 * <li>market/company</li>
 * <li>sales</li>
 * <li>language</li>
 * <li>home</li>
 * </ul>
 * 
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LevelService {
    /**
     * Gets the Brand Level.
     * 
     * @return the brand level
     */
    int getBrandLevel();

    /**
     * Gets the Company Level.
     * 
     * @return the company level
     */
    int getCompanyLevel();

    /**
     * Gets the Home Level.
     * 
     * @return the home level
     */
    int getHomeLevel();

    /**
     * Gets the Language Level.
     * 
     * @return the language level
     */
    int getLanguageLevel();

    /**
     * Gets the Region Level.
     * 
     * @return the region level
     */
    int getRegionLevel();

    /**
     * Gets the Sales Level.
     * 
     * @return the sales level
     */
    int getSalesLevel();

    /**
     * Gets the tools level.
     * 
     * @return the tools level
     */
    int getToolsLevel();

    /**
     * This method returns true, when the given page matches the given level. NOTE: As level, the depth of the
     * page minus 1 is taken.
     * 
     * @param page
     *            The page that is checked
     * @param level
     *            The level on which the page should be on.
     * @return true, when page level matches level parameter.
     */
    boolean matchesContentLevel(Page page,
                                int level);
}
