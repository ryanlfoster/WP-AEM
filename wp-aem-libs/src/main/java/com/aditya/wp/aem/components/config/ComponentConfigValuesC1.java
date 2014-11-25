/**
 * 
 */
package com.aditya.wp.aem.components.config;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.wp.aem.components.AbstractComponent;
import com.day.cq.wcm.api.Page;

/**
 * @author aditya.vennelakanti
 *
 */
public class ComponentConfigValuesC1 extends AbstractComponent {

	private static final Logger LOG = LoggerFactory.getLogger(ComponentConfigValuesC1.class);
	public static final String RESOURCE_TYPE = "wp/components/config/config_values_c1";

	private String imagePath;
	private String loadTime;
	private String cookieExpireTime;
	private String httpHost;
	private String httpsHost;

	private String inheritedImagePath;
	private String inheritedLoadTime;
	private String inheritedCookieExpireTime;
	private String inheritedHttpHost;
	private String inheritedHttpsHost;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aditya.wp.aem.components.AbstractComponent#init()
	 */
	@Override
	public void init() {
		LOG.error("Initializing Config Values Component");
		this.imagePath = getPropertyAsString("imagePath");
		this.loadTime = getPropertyAsString("loadTime");
		this.cookieExpireTime = getPropertyAsString("cookieExpireTime");
		this.httpHost = getPropertyAsString("httpHost");
		this.httpsHost = getPropertyAsString("httpsHost");

		Page page = getCurrentPage().getParent();
		while (page != null) {
			final Resource res = page.getContentResource().getChild("config_values_c1");
			if (res != null) {
				final ValueMap vm = res.getValueMap();
				this.inheritedImagePath = vm.get("imagePath", StringUtils.EMPTY);
				this.inheritedLoadTime = vm.get("loadTime", StringUtils.EMPTY);
				this.inheritedCookieExpireTime = vm.get("cookieExpireTime", StringUtils.EMPTY);
				this.inheritedHttpHost = vm.get("httpHost", StringUtils.EMPTY);
				this.inheritedHttpsHost = vm.get("httpsHost", StringUtils.EMPTY);
				break;
			}
			page = page.getParent();
		}
	}

	/**
	 * @return the imagePath
	 */
	public final String getImagePath() {
		return imagePath;
	}

	/**
	 * @return the loadTime
	 */
	public final String getLoadTime() {
		return loadTime;
	}

	/**
	 * @return the cookieExpireTime
	 */
	public final String getCookieExpireTime() {
		return cookieExpireTime;
	}

	/**
	 * @return the httpHost
	 */
	public final String getHttpHost() {
		return httpHost;
	}

	/**
	 * @return the httpsHost
	 */
	public final String getHttpsHost() {
		return httpsHost;
	}

	/**
	 * @return the inheritedImagePath
	 */
	public final String getInheritedImagePath() {
		return inheritedImagePath;
	}

	/**
	 * @return the inheritedLoadTime
	 */
	public final String getInheritedLoadTime() {
		return inheritedLoadTime;
	}

	/**
	 * @return the inheritedCookieExpireTime
	 */
	public final String getInheritedCookieExpireTime() {
		return inheritedCookieExpireTime;
	}

	/**
	 * @return the inheritedHttpHost
	 */
	public final String getInheritedHttpHost() {
		return inheritedHttpHost;
	}

	/**
	 * @return the inheritedHttpsHost
	 */
	public final String getInheritedHttpsHost() {
		return inheritedHttpsHost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aditya.wp.aem.components.AbstractComponent#getResourceType()
	 */
	@Override
	public String getResourceType() {
		return RESOURCE_TYPE;
	}
}
