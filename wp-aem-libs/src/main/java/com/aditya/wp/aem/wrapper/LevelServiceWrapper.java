/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.wrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.wp.aem.services.config.LevelService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class LevelServiceWrapper {

    /**
     * @author skoch, namics ag
     * @since GMWP Release 2.4
     */
    public enum Constants {
        LSLR, COMPANY
    }

    private static final int INVALID_LEVEL_IDENTIFIER = -1;

    private final ResourceResolver resourceResolver;

    private final LevelService levelService;

    /**
     * Constructor.
     * 
     * @param resourceResolver
     *            the resourceResolver
     * @param levelService
     *            the levelService
     */
    public LevelServiceWrapper(final ResourceResolver resourceResolver, final LevelService levelService) {
        this.resourceResolver = resourceResolver;
        this.levelService = levelService;
    }

    private static final Logger LOG = LoggerFactory.getLogger(LevelServiceWrapper.class);

    /**
     * Get the level for a configuration page.
     * 
     * @param levelConstant
     *            the levelConstant.
     * @return the level of the configuration page
     */
    public final int getLevelOfConfigurationPageType(final Constants levelConstant) {

        int level = INVALID_LEVEL_IDENTIFIER;

        if (levelConstant.equals(Constants.LSLR)) {
            level = this.levelService.getLanguageLevel();
        } else if (levelConstant.equals(Constants.COMPANY)) {
            level = this.levelService.getCompanyLevel();
        }

        return level;
    }

    /**
     * Get the relevant configuration page for the given path and type.
     * 
     * @param levelConstant
     *            the levelConstant
     * @param currentPath
     *            the currentPath
     * @return the resource of the configuration page.
     */
    public final Resource getRelevantConfigurationPageByType(final Constants levelConstant,
                                                             final String currentPath) {

        if (StringUtils.isEmpty(currentPath) || levelConstant == null) {
            return null;
        }

        final PageManager pageManager = this.resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            LOG.warn("could not adapt resolver to page manager");
            return null;
        }

        final Page page = pageManager.getContainingPage(currentPath);
        if (page == null) {
            LOG.warn("could not get containing page for" + currentPath);
            return null;
        }

        final int level = getLevelOfConfigurationPageType(levelConstant);
        if (level == INVALID_LEVEL_IDENTIFIER) {
            return null;
        }

        final Page configurationPage = page.getAbsoluteParent(level);

        if (configurationPage == null) {
            return null;
        }

        return configurationPage.getContentResource();
    }
}
