/**
 * 
 */
package com.aditya.wp.aem.services.config.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;

import com.aditya.wp.aem.services.config.LevelService;
import com.day.cq.wcm.api.Page;

/**
 * @author aditya.vennelakanti
 *
 */
@Service
@Component(name = "com.aditya.wp.aem.services.config.LevelService", label="WP Level Service", metatype = true)
public class LevelServiceImpl implements LevelService {
	
	private static final int BRAND_PAGE_LEVEL = 2;
	private static final int REGION_PAGE_LEVEL = 3;

	   /**
     * Company Page Level String for felix configuration.
     */
    @Property(label = "Company Level", description = "Level for company pages", intValue = 4)
    private static final String COMPANY_PAGE_LEVEL = "companyPageLevel";
    private static final int COMPANY_PAGE_LEVEL_DEFAULT = 4;

    /**
     * Sales Page Level String for felix configuration.
     */
    @Property(label = "Sales Level", description = "Level for sales pages.", intValue = 5)
    private static final String SALES_PAGE_LEVEL = "salesPageLevel";
    private static final int SALES_PAGE_LEVEL_DEFAULT = 5;

    /**
     * Language Page Level String for felix configuration.
     */
    @Property(label = "Language Level", description = "Level for language pages.", intValue = 6)
    private static final String LANGUAGE_PAGE_LEVEL = "languagePageLevel";
    private static final int LANGUAGE_PAGE_LEVEL_DEFAULT = 6;

    /**
     * Home Page Level String for felix configuration.
     */
    @Property(label = "Home Level", description = "Level for home pages.", intValue = 7)
    private static final String HOME_PAGE_LEVEL = "homePageLevel";
    private static final int HOME_PAGE_LEVEL_DEFAULT = 7;

    /**
     * Language Page Level String for felix configuration.
     */
    @Property(label = "Tools Level", description = "Level for tools pages.", intValue = 8)
    private static final String TOOLS_PAGE_LEVEL = "toolsPageLevel";
    private static final int TOOLS_PAGE_LEVEL_DEFAULT = 8;

    private int companyPageLevel = COMPANY_PAGE_LEVEL_DEFAULT;
    private int homePageLevel = HOME_PAGE_LEVEL_DEFAULT;
    private int languagePageLevel = LANGUAGE_PAGE_LEVEL_DEFAULT;
    private int salesPageLevel = SALES_PAGE_LEVEL_DEFAULT;
    private int toolsPageLevel = TOOLS_PAGE_LEVEL_DEFAULT;

    @Activate
    protected final void activate(final Map<String, Object> config) {
    	this.companyPageLevel = PropertiesUtil.toInteger(COMPANY_PAGE_LEVEL, COMPANY_PAGE_LEVEL_DEFAULT);
    	this.salesPageLevel = PropertiesUtil.toInteger(SALES_PAGE_LEVEL, SALES_PAGE_LEVEL_DEFAULT);
    	this.languagePageLevel = PropertiesUtil.toInteger(LANGUAGE_PAGE_LEVEL, LANGUAGE_PAGE_LEVEL_DEFAULT);
    	this.homePageLevel = PropertiesUtil.toInteger(HOME_PAGE_LEVEL, HOME_PAGE_LEVEL_DEFAULT);
    	this.toolsPageLevel = PropertiesUtil.toInteger(TOOLS_PAGE_LEVEL, TOOLS_PAGE_LEVEL_DEFAULT);
    }

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.config.LevelService#getBrandLevel()
	 */
	@Override
	public int getBrandLevel() {
		return BRAND_PAGE_LEVEL;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.config.LevelService#getCompanyLevel()
	 */
	@Override
	public int getCompanyLevel() {
		return this.companyPageLevel;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.config.LevelService#getHomeLevel()
	 */
	@Override
	public int getHomeLevel() {
		return this.homePageLevel;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.config.LevelService#getLanguageLevel()
	 */
	@Override
	public int getLanguageLevel() {
		return this.languagePageLevel;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.config.LevelService#getRegionLevel()
	 */
	@Override
	public int getRegionLevel() {
		return REGION_PAGE_LEVEL;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.config.LevelService#getSalesLevel()
	 */
	@Override
	public int getSalesLevel() {
		return this.salesPageLevel;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.config.LevelService#getToolsLevel()
	 */
	@Override
	public int getToolsLevel() {
		return this.toolsPageLevel;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.config.LevelService#matchesContentLevel(com.day.cq.wcm.api.Page, int)
	 */
	@Override
	public boolean matchesContentLevel(final Page page, final int level) {
		return page.getDepth() - 1 == level;
	}
}
