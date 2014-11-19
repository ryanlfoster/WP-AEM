/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.impl;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import com.aditya.gmwp.aem.services.core.AbstractService;
import com.aditya.gmwp.aem.services.core.JcrService;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(value=JcrService.class)
@Component(immediate=true, enabled=true)
public class JcrServiceImpl extends AbstractService<JcrServiceImpl> implements JcrService {

	@Reference
	private transient final ResourceResolverFactory resourceResolverFactory = null;

	private transient ResourceResolver resourceResolver;

	@Activate
	protected void activate() {
		try {
			this.resourceResolver = this.resourceResolverFactory.getServiceResourceResolver(null);
		} catch (Exception e) {
			getLog(this).warn("Error getting resource resolver.\nError: ", e);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (this.resourceResolver != null) {
			this.resourceResolver.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.JcrService#getAdminSession()
	 */
	@Override
    public Session getAdminSession() {
	    return (this.resourceResolver != null ? this.resourceResolver.adaptTo(Session.class) : null);
    }

	/*
	 * (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.JcrService#getResourceResolver()
	 */
	@Override
    public ResourceResolver getResourceResolver() {
	    return this.resourceResolver;
    }

	/*
	 * (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.JcrService#getGMResource(java.lang.String)
	 */
	@Override
    public GMResource getGMResource(String path) {
		if (this.resourceResolver != null) {
			return new GMResource(this.resourceResolver.getResource(path));
		} else {
			return GMResource.emptyGMResource();
		}
    }

	/*
	 * (non-Javadoc)
	 * @see com.aditya.gmwp.aem.services.core.JcrService#getPageManager()
	 */
	@Override
    public PageManager getPageManager() {
	    return (this.resourceResolver != null ? this.resourceResolver.adaptTo(PageManager.class) : null);
    }
}
