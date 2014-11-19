/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.baseballcard.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;

import com.aditya.wp.aem.services.baseballcard.BaseballcardInfoService;
import com.aditya.wp.aem.services.baseballcard.model.BaseballcardInfoModel;
import com.aditya.wp.aem.services.config.LevelService;
import com.aditya.wp.aem.services.core.AbstractService;
import com.aditya.wp.aem.services.core.JcrService;
import com.aditya.wp.aem.services.core.QueryService;
import com.aditya.wp.aem.utils.PersistentCacheUtil;
import com.aditya.wp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(value = BaseballcardInfoService.class)
@Component(immediate=true, enabled=true, label="com.aditya.wp.aem.services.baseballcard.BaseballcardInfoService", description="%baseballcardinfoservice.description", name="com.aditya.wp.aem.services.baseballcard.BaseballcardInfoService", metatype=true)
public class BaseballcardInfoServiceImpl extends AbstractService<BaseballcardInfoServiceImpl> implements BaseballcardInfoService {

	private final class UpdateJob implements Runnable {
		@Override
		public void run() {
		    if(BaseballcardInfoServiceImpl.this.schedulerEnabled.booleanValue()) {
		    	BaseballcardInfoServiceImpl.this.run();
		    } else {
		    	getLog(BaseballcardInfoServiceImpl.this).warn("Baseball Card Data Update Job skipped as scheduler has been disabled.");
		    }
		}
	}

    private static final String CACHE_KEY = "CachedBaseballcardData";

    private static final String UPDATE_JOB_NAME = "BaseballcardDataUpdateJob";

    /** Is the scheduler enabled ? */
    @Property(boolValue = true)
    protected static final String SCHEDULER_ENABLED = "baseballcard.scheduler.enable";

    /** The expression for the scheduler */
    @Property(value = "0 0 1 * * ?")
    protected static final String SCHEDULER_EXPRESSION = "baseballcard.scheduler.expression";

    /** Should the cache be flushed ? */
    @Property(boolValue = false)
    protected static final String FLUSH_CACHE = "baseballcard.cache.flush";

    /** the scheduler. */
    @Reference
    private final transient Scheduler scheduler = null;

    /** the level service */
    @Reference
    private final transient LevelService levelService = null;
    
    @Reference
    private final transient JcrService jcrService = null;

    /** the query service */
    @Reference
    private final transient QueryService queryService = null;

    /** is the scheduler enabled */
    private Boolean schedulerEnabled;

    /** map containing baseball card data for each market */
    private Map<String, BaseballcardInfoModel> persistedBaseballCardDataCache;

    @Activate
    @SuppressWarnings("unchecked")
    protected void activate(final Map<String, Object> config) {
    	getLog(this).debug("Activating BaseballcardInfoService...");
    	
    	this.schedulerEnabled = PropertiesUtil.toBoolean(config.get(SCHEDULER_ENABLED), false);
    	final String schedulerExpression = PropertiesUtil.toString(config.get(SCHEDULER_EXPRESSION), null);
    	final Boolean flush = PropertiesUtil.toBoolean(config.get(FLUSH_CACHE), false);

    	try {
	    	if(this.schedulerEnabled) {
    			if(flush) {
    				run();
    			}
    			final ScheduleOptions options = this.scheduler.EXPR((StringUtils.isBlank(schedulerExpression) ? "0 0 1 * * ?" : schedulerExpression)).name(UPDATE_JOB_NAME).canRunConcurrently(false);
    			this.scheduler.schedule(new UpdateJob(), options);
	    	}

	    	this.persistedBaseballCardDataCache = (Map<String, BaseballcardInfoModel>) PersistentCacheUtil.loadPersistedObject(CACHE_KEY, this.jcrService.getAdminSession());
	    	if (this.persistedBaseballCardDataCache == null) {
	    		this.persistedBaseballCardDataCache = new LinkedHashMap<String, BaseballcardInfoModel>();
	    	}
    	} catch (Exception e) {
    		getLog(this).warn("Unable to schedule update job for Baseball card data.\nError: ", e);
    	}

    	getLog(this).debug("Activation of BaseballcardInfoService done.");
    }
    
    @Deactivate
    protected void deactivate() {
    	getLog(this).debug("Deactivating BaseballCardInfoService...");
        this.scheduler.unschedule(UPDATE_JOB_NAME);
        getLog(this).debug("Deactivation of BaseballCardInfoService done.");
    }

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.core.CacheService#run()
	 */
	@Override
	public void run() {
		loadData();
		persistData();
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.core.CacheService#flushCache()
	 */
	@Override
	public void flushCache() {
		run();
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.baseballcard.BaseballCardInfoService#getBaseballcardData(com.day.cq.wcm.api.Page)
	 */
	@Override
	public BaseballcardInfoModel getBaseballcardData(final Page currentPage) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.baseballcard.BaseballCardInfoService#getBaseballcardResource(com.day.cq.wcm.api.Page, java.lang.String)
	 */
	@Override
	public Set<String> getBaseballcardResource(final Page currentPage,
	                                           final String resourceType) {
		return null;
	}

	@SuppressWarnings("unchecked")
    private void loadData() {
		this.persistedBaseballCardDataCache = (Map<String, BaseballcardInfoModel>) PersistentCacheUtil.loadPersistedObject(CACHE_KEY, this.jcrService.getAdminSession());
        if (this.persistedBaseballCardDataCache == null) {
            this.persistedBaseballCardDataCache = new LinkedHashMap<String, BaseballcardInfoModel>();
        }

        final List<GMResource> lslrResources = this.queryService.findAllByKeyValue("sling:resourceType", "gmds/pages/tc_languageslr", "/content");
        for (GMResource res : lslrResources) {
            final Page lslrPage = res.getContainingPage();
            try {
                final BaseballcardInfoModel bbcData = this.persistedBaseballCardDataCache.get(lslrPage.getPath());

                if (bbcData == null) {
                    final BaseballcardInfoModel data = new BaseballcardInfoModelImpl();
                    data.update(lslrPage);
                    this.persistedBaseballCardDataCache.put(lslrPage.getPath(), data);
                } else {
                    bbcData.update(lslrPage);
                }
            } catch (Exception e) {
            	getLog(this).error("Exception while building baseball card data. Error:\n", e);
            }
        }
	}

	private void persistData() {
		PersistentCacheUtil.persistObject(CACHE_KEY, (Serializable) this.persistedBaseballCardDataCache, this.jcrService.getAdminSession());
	}
}
