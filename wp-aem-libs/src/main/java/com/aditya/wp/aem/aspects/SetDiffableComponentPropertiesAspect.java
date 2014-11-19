/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.aspects;

import java.lang.reflect.Field;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.wp.aem.annotations.DiffableProperty;
import com.aditya.wp.aem.components.AbstractComponent;
import com.aditya.wp.aem.components.ComponentAspect;
import com.aditya.wp.aem.utils.diff.DiffUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class SetDiffableComponentPropertiesAspect implements ComponentAspect {


    /** the logger. */
    private static final Logger LOG = LoggerFactory.getLogger(SetDiffableComponentPropertiesAspect.class);

    /** the concrete component. */
    private AbstractComponent component;

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.components.ComponentAspect#applyAspect()
	 */
	@Override
	public void applyAspect() {
        final Field[] fields = this.component.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DiffableProperty.class)) {
                final DiffableProperty property = field.getAnnotation(DiffableProperty.class);
                // avoid IllegalAccessExcpetion due to all instance variables being private
                field.setAccessible(true);
                try {
                    final Resource resource = this.component.getResource();
                    final SlingScriptHelper sling = this.component.getSlingScriptHelper();
                    field.set(this.component, DiffUtil.getDiff(resource, property.path(), property.isRichtext(), sling));
                } catch (IllegalAccessException e) {
                    LOG.error("Unable to set property via reflection. Cause: ", e);
                } catch (IllegalArgumentException e) {
                    LOG.error("Unable to set property via reflection. Cause: ", e);
                }
            }
        }
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.components.ComponentAspect#init(org.apache.sling.api.SlingHttpServletRequest, com.day.cq.wcm.api.Page, com.aditya.wp.aem.components.AbstractComponent)
	 */
	@Override
	public void init(final SlingHttpServletRequest slingRequest,
	                 final Page currentPage,
	                 final AbstractComponent component) {
		this.component = component;
	}

}
