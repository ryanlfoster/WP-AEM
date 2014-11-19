/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.link.writers;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.LinkWriter;
import com.aditya.gmwp.aem.utils.EncodeDecodeUtil;
import com.aditya.gmwp.aem.utils.ProtocolUtil;
import com.aditya.gmwp.aem.utils.WCMModeUtil;
import com.aditya.gmwp.aem.utils.uri.UriBuilder;
import com.day.cq.wcm.api.NameConstants;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ResourceLinkWriter {

    /**
     * Checks if the internalLinkPath points to a sub-resource and not to a page resource. Could e.g. be a path to a
     * component like
     * /content/vauxhall/europe/united_kingdom/nscwebsite/uk/index/jcr:content/gridArea/grid_col1_c1_cdcf/
     * pf1a_parsys/nav_vehicle_selection
     * 
     * @param internalLinkPath
     *            the internal link path
     * @return true, if the resource is no page directly but a sub-resource
     */
    public static boolean isResourceLink(final String internalLinkPath) {
        if (StringUtils.isNotEmpty(internalLinkPath)) {
            return internalLinkPath.contains(NameConstants.NN_CONTENT);
        }
        return false;
    }

    /**
     * Builds the url.
     * 
     * @param internalLinkPath
     *            the internal link path
     * @param protocol
     *            the protocol
     * @param host
     *            the host
     * @param selectors
     *            the selectors
     * @param parameters
     *            the parameters
     * @param extension
     *            the extension
     * @param anchor
     *            the anchor
     * @return the string
     */
    private static String buildUrl(final String internalLinkPath,
                                   final String protocol,
                                   final String host,
                                   final List<String> selectors,
                                   final Map<String, Set<String>> parameters,
                                   final String extension,
                                   final String anchor) {
        final UriBuilder ub = new UriBuilder(internalLinkPath);
        ub.setScheme(protocol);
        ub.setHost(host);
        ub.addAllSelectors(selectors);
        ub.addAllMultiParameters(parameters);
        ub.setExtension(extension);
        ub.setAnchor(anchor);
        return ub.build();
    }

    /**
     * Rewrites an internal link (content path to a resource) to work on publisher.
     * 
     * @param request
     *            the request
     * @param linkModel
     *            the link model
     * @return the hTML link
     */
    public static HTMLLink rewrite(final SlingHttpServletRequest request,
                                   final LinkModel linkModel) {
        final HTMLLink link = new HTMLLink();
        if (linkModel.getInternalLink() != null) {
            String internalLinkPath = linkModel.getInternalLink();
            final ResourceResolver resolver = request.getResourceResolver();
            final Resource targetResource = resolver.resolve(internalLinkPath);
            final Boolean isAuthorInstance = WCMModeUtil.isAuthorInstance(request);
            // check which protocol + host to use, if any
            final boolean isHttps = ProtocolUtil.isHttpsRequest(request);
            String protocolAndHost = null;
            if (!isAuthorInstance) {
                final boolean targetPageRequiresHttps = ProtocolUtil.doesPageRequireHttps(targetResource);
                if (isHttps && !targetPageRequiresHttps) {
                    protocolAndHost = ProtocolUtil.getHttpHostFromConfiguration(request);
                } else if (!isHttps && targetPageRequiresHttps) {
                    protocolAndHost = ProtocolUtil.getHttpsHostFromConfiguration(request);
                }
            }
            final String extension = "html";
            if (!internalLinkPath.endsWith("." + extension) && !internalLinkPath.endsWith("/")) {
                internalLinkPath = internalLinkPath + "." + extension;
            }
            String mapedContentPath = internalLinkPath;
            if (!isAuthorInstance) {
                mapedContentPath = targetResource.getResourceResolver().map(request, internalLinkPath);
            }
            final Map<String, Set<String>> parameters = linkModel.getParameters();
            final String anchor = linkModel.getAnchor();
            final List<String> selectors = new ArrayList<String>();
            final UriBuilder hostUb = new UriBuilder(protocolAndHost);
            final String protocol = hostUb.getScheme();
            final String host = hostUb.getHost();
            // add selectors of Link Model. Care about encoding, because selectors can contain authors input
            selectors.add(EncodeDecodeUtil.urlEncode(linkModel.getSelectorListAsString()));
            final String newInternalLinkPath = buildUrl(mapedContentPath, protocol, host, selectors, parameters,
                    extension, anchor);
            link.setHref(newInternalLinkPath);
        }
        return link;
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private ResourceLinkWriter() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}
