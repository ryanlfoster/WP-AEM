/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.diff;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.DiffInfo;
import com.day.cq.commons.DiffInfo.TYPE;
import com.day.cq.commons.DiffService;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.WCMMode;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class DiffUtil {

    /** request cq_diffTo parameter. */
    public static final String DIFF_TO_PARAMETER = "cq_diffTo";
    /** the logger. */
    private static final Logger LOG = LoggerFactory.getLogger(DiffUtil.class);

    /**
     * This method returns the difference from a resource and its property from one version to another as a string.
     * 
     * @param resource
     *            the resource to get difference for
     * @param name
     *            the name of the property
     * @param isRichtext
     *            determine whether property is richtext
     * @param sling
     *            the sling script helper used to retrieve the {@link DiffService}
     * @return the difference as string
     */
    public static String getDiff(final Resource resource,
                                 final String name,
                                 final boolean isRichtext,
                                 final SlingScriptHelper sling) {
        final String property = getPropertyFromResource(resource, name);
        final DiffInfo info = resource.adaptTo(DiffInfo.class);
        if (null == info) {
            return property;
        }

        String s = null;
        try {
            // the try/catch is more like a workaround, day hotfix will come with 5.4
            s = info.getDiffOutput(sling.getService(DiffService.class), property,
                    getPropertyFromResource(info.getContent(), name), isRichtext);
        } catch (ArrayIndexOutOfBoundsException e) {
            s = property;
        }

        return s;
    }

    /**
     * Returns the (string) value of a property.
     * 
     * @param resource
     *            the resource to get properties from
     * @param name
     *            the name of the property
     * @return property value
     */
    @SuppressWarnings("deprecation")
    private static String getPropertyFromResource(final Resource resource,
                                                  final String name) {
        return ResourceUtil.getValueMap(resource).get(name, String.class);
    }

    /**
     * This method returns a <code>DiffableResource</code> wrapping the current resource and its <code>DiffInfo</code>
     * in case cq_diffTo was found in request. Otherwise current resource is returned.
     * 
     * @param request
     *            the request to look for cq_diffTo parameter
     * @param resource
     *            the current resource to make diffable resource from
     * @return diffable resource or current resource if cq_diffTo not found in request
     */
    public static Resource getDiffableResource(final HttpServletRequest request,
                                               final Resource resource) {
        Resource r = resource;
        final String v = getVersion(request);

        if (StringUtils.isNotEmpty(v)) {
            r = new DiffableResource(resource, getDiffInfo(resource, v));
        }

        return r;
    }

    /**
     * This method returns a <code>DiffableResource</code> wrapping the current resource and its <code>DiffInfo</code>
     * in case a version is passed. Otherwise current resource is returned.
     * 
     * @param version
     *            the version
     * @param resource
     *            the current resource to make diffable resource from
     * @return diffable resource or current resource if no version passed
     */
    public static Resource getDiffableResource(final String version,
                                               final Resource resource) {
        Resource r = resource;

        if (StringUtils.isNotEmpty(version)) {
            r = new DiffableResource(resource, getDiffInfo(resource, version));
        }

        return r;
    }

    /**
     * This method returns a history resource of the resource/version label passed wrapped as <code>DiffInfo</code>. Can
     * be from type removed, added or same.
     * 
     * @param resource
     *            the resource to get history resource from
     * @param version
     *            the version to get version node from
     * @return history resource as {@link DiffInfo}
     */
    public static DiffInfo getDiffInfo(final Resource resource,
                                       final String version) {
        DiffInfo info = null;
        final Resource history = getHistoryResource(resource, version);

        if (ResourceUtil.isSyntheticResource(resource) && !ResourceUtil.isSyntheticResource(history)) {
            info = new DiffInfo(history, TYPE.REMOVED);
        } else if (!ResourceUtil.isSyntheticResource(resource) && ResourceUtil.isSyntheticResource(history)) {
            info = new DiffInfo(history, TYPE.ADDED);
        } else {
            info = new DiffInfo(history, TYPE.SAME);
        }

        return info;
    }

    /**
     * Returns a history resource, can also be a fallback one if no history resource for version available.
     * 
     * @param resource
     *            the current resource
     * @param version
     *            the version to get history resource with
     * @return history resource
     */
    private static Resource getHistoryResource(final Resource resource,
                                               final String version) {
        final Node node = getCurrentVersionNode(resource);
        Resource history = null;

        if (null == node) {
            history = getFallbackHistoryResource(resource);
        } else {
            final String v = getVersionPath(version, node);
            if (null == v) {
                history = getFallbackHistoryResource(resource);
            } else {
                history = resource.getResourceResolver().resolve(
                        v + "/jcr:frozenNode/" + getRelativeVersionPath(resource));
            }
        }

        return history;
    }

    /**
     * Returns the version path of a version node retrieved by passed version.
     * 
     * @param version
     *            the version to get node for
     * @param node
     *            the current node to get version history for
     * @return version path or <code>null</code> if no path
     */
    private static String getVersionPath(final String version,
                                         final Node node) {
        String p;
        try {
            final Version v = getVersionNode(version, node);
            p = null != v ? v.getPath() : null;
        } catch (RepositoryException e) {
            p = null;
        }

        return p;
    }

    /**
     * Returns the version node by passed version.
     * 
     * @param version
     *            the version to look for a node
     * @param node
     *            the current node to retrieve version history
     * @return version node or <code>null</code> if not version found
     */
    private static Version getVersionNode(final String version,
                                          final Node node) {
        Version v = null;
        try {
            final VersionHistory h = getVersionHistory(node);
            try {
                v = h.getVersion(version);
            } catch (VersionException ve) {
                v = h.getVersionByLabel(version);
            }
        } catch (RepositoryException re) {
            LOG.warn("Unable to get version '" + version + "'. Cause: " + re.toString());
        }

        return v;
    }

    /**
     * Returns a fallback histroy resource with no actual data.
     * 
     * @param resource
     *            the current resource
     * @return fallback history resource
     */
    private static Resource getFallbackHistoryResource(final Resource resource) {
        return new SyntheticResource(resource.getResourceResolver(), "/UNKNOWN" + resource.getPath(),
                resource.getResourceType());
    }

    /**
     * Returns the current version node from a resource.
     * 
     * @param resource
     *            the current resource
     * @return current version node or <code>null</code> if no node found
     */
    private static Node getCurrentVersionNode(final Resource resource) {
        final ResourceResolver rr = resource.getResourceResolver();
        final Resource r = rr.getResource(getVersionPath(resource));

        return null != r ? r.adaptTo(Node.class) : null;
    }

    /**
     * Returns the relative version path of a resource.
     * 
     * @param resource
     *            the current resource
     * @return relative version path
     */
    private static String getRelativeVersionPath(final Resource resource) {
        return resource.getPath().substring(getJCRContentEndIndex(resource) + 1);
    }

    /**
     * Returns the version path of a resource.
     * 
     * @param resource
     *            the current resource
     * @return version path
     */
    private static String getVersionPath(final Resource resource) {
        return resource.getPath().substring(0, getJCRContentEndIndex(resource));
    }

    /**
     * Returns the jcr:content end index position in the current resource path.
     * 
     * @param resource
     *            the current resource
     * @return jcr:content end index
     */
    private static int getJCRContentEndIndex(final Resource resource) {
        return resource.getPath().indexOf(JcrConstants.JCR_CONTENT) + JcrConstants.JCR_CONTENT.length();
    }

    /**
     * Returns a version history from a node.
     * 
     * @param node
     *            the node to get version history from
     * @return version history
     * @throws RepositoryException
     *             if retrieving version history not possible
     */
    private static VersionHistory getVersionHistory(final Node node) throws RepositoryException {
        return node.getSession().getWorkspace().getVersionManager().getVersionHistory(node.getPath());
    }

    /**
     * This method returns the diff to version parameter value from request.
     * 
     * @param request
     *            the request to get version from
     * @return diff to version or <code>null</code> if parameter not found in request
     */
    public static String getVersion(final HttpServletRequest request) {
        return request.getParameter(DIFF_TO_PARAMETER);
    }

    /**
     * This method returns whether current view is diff view by looking for cq_diffTo parameter in request.
     * 
     * @param request
     *            the request do determine whether diff view
     * @return is diff view
     */
    public static boolean isDiffView(final HttpServletRequest request) {
        return StringUtils.isNotEmpty(getVersion(request)) && WCMMode.fromRequest(request) == WCMMode.PREVIEW;
    }

    /**
     * Constructor.
     */
    private DiffUtil() {

    }
}
