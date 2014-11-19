/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.writers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.wp.aem.global.GmdsRequestAttribute;
import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.services.core.link.HTMLLink;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class Template17cLinkWriter extends DefaultLinkWriter {

    private static final String COMPONENT_BBC_BODYSTYLE_C1 = "gmds/components/baseballcard/bbc_bodystyle_c1";

    private static final String COMPONENT_BBC_BODYSTYLE_MANUAL_C1 = //
    "gmds/components/baseballcard/bbc_bodystyle_manual_c1";

    private static final String CONTENT_LAYER_ID_PREFIX = "layer_content_";

    private static final Logger LOG = LoggerFactory.getLogger(Template17cLinkWriter.class);

    /** {@inheritDoc} */
    @Override
    public final HTMLLink rewrite(final SlingHttpServletRequest request,
                                  final LinkModel linkModel) {
        final HTMLLink link = super.rewrite(request, linkModel);
        link.setHref("#" + setContentLayerMapForT17cTemplateInRequest(request, linkModel));
        link.setClazz("btn_lyr");

        return link;
    }

    /**
     * Set the content "contentLayer" attribute in request - First get the last id from request
     * ("contentLayerIdCounter"), if one is available count the id and set new in request. This id
     * and the path to the modal layer will be saved in an new request attribute or if one is
     * available add the new attribute to the map and save in request.
     * 
     * @param request
     *            the ServletRequest
     * @param modalLayerLink
     *            the link to the t17c modal layer template
     * @return the id of the current content layer
     */
    @SuppressWarnings("unchecked")
    private String setContentLayerMapForT17cTemplateInRequest(final ServletRequest request,
                                                              final LinkModel modalLayerLink) {
        final HttpServletRequest servletReq = (HttpServletRequest) request;
        String contentLayerLinkPath = modalLayerLink.getInternalLinkPath();
        if (StringUtils.isEmpty(contentLayerLinkPath)) {
            LOG.warn("No page could be found for internal link.");
            contentLayerLinkPath = modalLayerLink.getInPageLink();
            if (StringUtils.isEmpty(contentLayerLinkPath)) {
                LOG.warn("No page layer could be found for link.");
                return "";
            }
        }
        final SlingBindings s = (SlingBindings) servletReq.getAttribute("org.apache.sling.api.scripting.SlingBindings");
        final SlingScriptHelper sh = s.getSling();
        final Resource resource = sh.getRequest().getResource();

        final StringBuilder contentLayerId = new StringBuilder();
        // for content layer's in bbc layers in navigation, we need an unique id per ajax request,
        // otherwise the content/js would be overridden, using md5 hash of path since base64 encode
        // didn't
        // work for all
        if (null != resource
                && (COMPONENT_BBC_BODYSTYLE_C1.equals(resource.getResourceType()) || COMPONENT_BBC_BODYSTYLE_MANUAL_C1.equals(resource.getResourceType()))) {
            final String hash = DigestUtils.md5Hex(resource.getPath());
            contentLayerId.append(CONTENT_LAYER_ID_PREFIX).append(hash).append("_");
        } else {
            contentLayerId.append(CONTENT_LAYER_ID_PREFIX);
        }

        Map<String, String> contentLayerMap = new HashMap<String, String>();
        if (GmdsRequestAttribute.CONTENT_LAYER.get(servletReq) != null) {
            final Object contentLayerReqAtt = GmdsRequestAttribute.CONTENT_LAYER.get(servletReq);
            if (contentLayerReqAtt instanceof HashMap) {
                contentLayerMap = (HashMap<String, String>) contentLayerReqAtt;
            }
        }

        Map<Integer, String> contentUniqueLayerIdMap = new HashMap<Integer, String>();
        if (GmdsRequestAttribute.CONTENT_UNIQUE_LAYER_ID.get(servletReq) != null) {
            final Object contentUniqueLayerIdReqAtt = GmdsRequestAttribute.CONTENT_UNIQUE_LAYER_ID.get(servletReq);
            if (contentUniqueLayerIdReqAtt instanceof HashMap) {
                contentUniqueLayerIdMap = (HashMap<Integer, String>) contentUniqueLayerIdReqAtt;
            }
        }

        if (GmdsRequestAttribute.CONTENT_LAYER_ID.get(servletReq) != null) {
            int idCounter = (Integer) GmdsRequestAttribute.CONTENT_LAYER_ID.get(servletReq);
            if (!contentLayerMap.containsValue(contentLayerLinkPath)) {
                int counter = ++idCounter;
                GmdsRequestAttribute.CONTENT_LAYER_ID.set(servletReq, counter);
                contentUniqueLayerIdMap.put(counter, contentLayerLinkPath);
                contentLayerId.append(counter).toString();
            } else {
                Integer key = getKeyByValue(contentUniqueLayerIdMap, contentLayerLinkPath);
                contentLayerId.append(key).toString();
            }

        } else {
            GmdsRequestAttribute.CONTENT_LAYER_ID.set(servletReq, 1);
            contentUniqueLayerIdMap.put(1, contentLayerLinkPath);
            contentLayerId.append(1).toString();
        }

        if (!contentLayerMap.containsValue(contentLayerLinkPath)) {
            contentLayerMap.put(contentLayerId.toString(), contentLayerLinkPath);
        }
        GmdsRequestAttribute.CONTENT_LAYER.set(servletReq, contentLayerMap);
        GmdsRequestAttribute.CONTENT_UNIQUE_LAYER_ID.set(servletReq, contentUniqueLayerIdMap);

        return contentLayerId.toString();
    }

    /**
     * A generic method to get the key of the hashmap given its value.
     * 
     * @param map
     *            the map we are searching through.
     * @param value
     *            the value who's key we want to find.
     * @param <T>
     *            for some key of type T
     * @param <E>
     *            for some value of type E
     * @return T the key we are looking for
     */
    private <T, E> T getKeyByValue(final Map<T, E> map,
                                   final E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
