/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.components.webwrapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.components.AbstractComponent;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class WebwrappedAppConfig extends AbstractComponent {

    private static final String APP_ID = "appId";

    private static final Logger LOG = LoggerFactory.getLogger(WebwrappedAppConfig.class);

    /**
     * Add an additional property "app-id". This id is not maintainable by the author. Instead, this property is
     * generated from the first application name that is entered by the author. When the application name is changed
     * later, the generated ID remains the same.
     * 
     * @param props
     *            the Valuemap with the properties.
     * @param currentNode
     *            the current node
     */
    public static void addAppId(final ValueMap props,
                                final Node currentNode) {
        if (!props.containsKey(APP_ID)) {
            try {
                final String appName = props.get("webWrappedAppName", String.class);
                if (StringUtils.isNotBlank(appName)) {
                    final String appId = getExplicitId(appName.toLowerCase(Locale.ENGLISH).replace(" ", "_"), getExistingIds(currentNode, APP_ID), 1);
                    currentNode.setProperty(APP_ID, appId);
                    currentNode.getSession().save();
                    currentNode.getSession().save();
                }
            } catch (RepositoryException e) {
                LOG.error("Could not save appId property ", e);
            }
        }
    }

    /**
     * Get all existing ids below the current application, to check if the new id already exist.
     * 
     * @param currentNode
     *            the current node to get the parent node and all children.
     * @param idProp
     *            id
     * @return the list with all existing ids
     * @throws RepositoryException
     *             this exception could be throw if the node could not be found
     */
    public static List<String> getExistingIds(final Node currentNode,
                                              final String idProp) throws RepositoryException {
        final List<String> existingIds = new ArrayList<String>();
        final Node parentNode = currentNode.getParent();
        final NodeIterator nodeIt = parentNode.getNodes();
        while (nodeIt.hasNext()) {
            final Node child = nodeIt.nextNode();
            if (!child.equals(currentNode) && child.hasProperty(idProp)) {
                final String entryPointId = child.getProperty(idProp).getString();
                existingIds.add(entryPointId);
            }

        }
        return existingIds;
    }

    /**
     * Get the identified id, if the current id already exist the method will be called recursive.
     * 
     * @param id
     *            the current id to check if this id already exist
     * @param ids
     *            the list with all existing ids.
     * @param count
     *            a counter to get an identified id
     * @return the new identified id.
     */
    public static String getExplicitId(final String id,
                                       final List<String> ids,
                                       final int count) {
        if (ids.contains(id)) {
            return getExplicitId(id + count, ids, count);
        }
        return id;
    }

    private final String webWrappedAppName;

    /**
     * Instantiates a new webwrapped app config.
     * 
     * @param pageContext
     *            the page context
     */
    public WebwrappedAppConfig() {
        this.webWrappedAppName = getPropertyAsString("webWrappedAppName");
        addAppId(getProperties(), getCurrentNode());
    }

    /**
     * Gets the web wrapped app name.
     * 
     * @return the web wrapped app name
     */
    public final String getWebWrappedAppName() {
        return this.webWrappedAppName;
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.components.AbstractComponent#init()
     */
    @Override
    public final void init() {
    }

    /*
     * (non-Javadoc)
     * @see com.aditya.gmwp.aem.components.AbstractComponent#getResourceType()
     */
	@Override
    public String getResourceType() {
	    return null;
    }
}
