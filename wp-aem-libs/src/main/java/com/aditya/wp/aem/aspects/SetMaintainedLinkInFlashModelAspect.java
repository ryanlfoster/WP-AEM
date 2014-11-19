/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.aspects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.annotations.ComponentWithStandardModel;
import com.aditya.gmwp.aem.annotations.StandardModelGetter;
import com.aditya.gmwp.aem.components.AbstractComponent;
import com.aditya.gmwp.aem.components.ComponentAspect;
import com.aditya.gmwp.aem.model.FlashModel;
import com.aditya.gmwp.aem.model.FlashParameter;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.LinkWriterService;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class SetMaintainedLinkInFlashModelAspect implements ComponentAspect {

    private static final Logger LOG = LoggerFactory.getLogger(SetMaintainedLinkInFlashModelAspect.class);

    private AbstractComponent component;

    private LinkWriterService linkResolver;

    private SlingHttpServletRequest slingRequest;

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.components.ComponentAspect#applyAspect()
	 */
	@Override
	public void applyAspect() {

        // Fetch link-model and flash-model from component object
        LinkModel linkModel = null;
        FlashModel flashModel = null;

        try {
            if (this.component.getClass().getAnnotation(ComponentWithStandardModel.class).hasLink()) {
                for (Method method : this.component.getClass().getDeclaredMethods()) {
                    if (method.isAnnotationPresent(StandardModelGetter.class)
                            && StandardModelGetter.ModelType.LINK == method.getAnnotation(StandardModelGetter.class).type()) {
                        linkModel = (LinkModel) method.invoke(this.component);
                        break;
                    }
                }
            }
            if (this.component.getClass().getAnnotation(ComponentWithStandardModel.class).hasFlash()) {
                for (Method method : this.component.getClass().getDeclaredMethods()) {
                    if (method.isAnnotationPresent(StandardModelGetter.class)
                            && StandardModelGetter.ModelType.FLASH == method.getAnnotation(StandardModelGetter.class).type()) {
                        flashModel = (FlashModel) method.invoke(this.component);
                        break;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            LOG.error("Unable to load flash-model or link-model via reflection. Cause: ", e);
            return;
        } catch (InvocationTargetException e) {
            LOG.error("Unable to load flash-model or link-model via reflection. Cause: ", e);
            return;
        }

        // add the link to the flash model param 'FlashVars'
        if (null != flashModel && null != linkModel) {
            final HTMLLink htmlLink = this.linkResolver.rewriteLink(this.slingRequest, linkModel);
            String href = "";
            if (null != htmlLink) {
                href = htmlLink.getHref();
                if (null != href) {
                    flashModel.putFlashParam(FlashParameter.FLASHVARS, "link=" + href);
                }
            }
        }
	}

	/* (non-Javadoc)
	 * @see com.aditya.gmwp.aem.components.ComponentAspect#init(org.apache.sling.api.SlingHttpServletRequest, com.day.cq.wcm.api.Page, com.aditya.gmwp.aem.components.AbstractComponent)
	 */
	@Override
	public void init(final SlingHttpServletRequest slingRequest,
	                 final Page currentPage,
	                 final AbstractComponent component) {
        this.slingRequest = slingRequest;
        this.component = component;
        this.linkResolver = this.component.getSlingScriptHelper().getService(LinkWriterService.class);
	}

}
