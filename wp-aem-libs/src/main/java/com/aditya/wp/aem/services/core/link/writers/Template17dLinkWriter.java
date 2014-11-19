/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.writers;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.wp.aem.model.AdvancedGalleryModel;
import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.services.core.link.HTMLLink;
import com.aditya.wp.aem.services.core.link.writers.utils.LinkWriterUtil;
import com.aditya.wp.aem.wrapper.DeepResolvingResourceUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class Template17dLinkWriter extends DefaultLinkWriter {

    @Override
    public final HTMLLink rewrite(final SlingHttpServletRequest request,
                                  final LinkModel linkModel) {
        boolean addedSelector = false;
        if (isMultimediaGallery(request.getResource())) {
            final ResourceResolver resolver = request.getResource().getResourceResolver();
            final Page targetPage = LinkWriterUtil.retrieveTargetPage(request, linkModel);
            final Resource container = resolver.getResource(targetPage.getContentResource(), "lightbox_container");

            if (isLightboxUsingVideo(container)) {
                final Resource r = resolver.getResource(container, "video_c1");
                if (null != r) {
                    linkModel.addSelector("snipplet");
                    addedSelector = true;
                }
            }
        }
        if (!addedSelector) {
            linkModel.addSelector("content");
        }
        final HTMLLink link = super.rewrite(request, linkModel);
        link.setClazz("btn_lyr");
        return link;
    }

    /**
     * Returns whether lightbox is setup to use/display a video.
     * 
     * @param lightboxContainer
     *            the lightbox container resource
     * @return uses/displays video
     */
    private boolean isLightboxUsingVideo(final Resource lightboxContainer) {
        if (null == lightboxContainer) {
            return false;
        }

        final ValueMap m = DeepResolvingResourceUtil.getValueMap(lightboxContainer);
        final String s = m.get("componentTypeSelection", String.class);

        return "video".equals(s);
    }

    /**
     * Returns whether gallery is a multimedia one (image/video).
     * 
     * @param currentResource
     *            the current resource to get usage
     * @return is multimedia gallery
     */
    private boolean isMultimediaGallery(final Resource currentResource) {
        if (null == currentResource) {
            return false;
        }

        final ValueMap m = DeepResolvingResourceUtil.getValueMap(currentResource);
        final String u = m.get("compUsage", String.class);

        return AdvancedGalleryModel.Type.MULTIMEDIA.getType().equals(u);
    }
}
