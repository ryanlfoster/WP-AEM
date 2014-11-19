/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class NodeUtil {

    private static final Logger LOG = LoggerFactory.getLogger(NodeUtil.class);

    /**
     * create a node for the given resource.
     * 
     * @param resource
     *            the new resource
     * @return the new created note
     */
    public static Node createNode(final Resource resource) {
        return createNode(resource, resource.getResourceType());
    }

    /**
     * create a node for the given resource.
     * 
     * @param resource
     *            the new resource
     * @param resourceType
     *            the resource Type of the new creating node
     * @return the new created note
     */
    public static Node createNode(final Resource resource,
                                  final String resourceType) {
        return createNode(resource, resourceType, true);
    }

    /**
     * create a node for the given resource.
     * 
     * @param resource
     *            the new resource
     * @param resourceType
     *            the resource Type of the new creating node
     * @param commit
     *            commit the current session
     * @return the new created note
     */
    public static Node createNode(final Resource resource,
                                  final String resourceType,
                                  final boolean commit) {
        Node node = resource.adaptTo(Node.class);
        if (node != null) {
            return node;
        }
        final Resource parentResource = resource.getParent();
        if (parentResource == null) {
            throw new IllegalArgumentException("Cannot create a node for a resource which does not have a parent resource.");
        }
        final Node parentNode = parentResource.adaptTo(Node.class);
        if (parentNode == null) {
            throw new IllegalArgumentException("Cannot create a node for a resource which does not have a parent resource.");
        }
        try {
            node = parentNode.addNode(resource.getName());
            node.setProperty("sling:resourceType", resourceType);
            if (commit) {
                parentNode.getSession().save();
            }
            return node;
        } catch (final RepositoryException e) {
            LOG.error("the last element of: " + resource.getPath() + "has an index or another error occurs. ", e);
        }
        return null;
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private NodeUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }

    /**
     * Save changes in current session.
     * 
     * @param node
     *            the node
     */
    public static void saveSession(final Node node) {

        if (node == null) {
            LOG.error("Could not save state, node was null");
            return;
        }

        try {
            node.getSession().save();
        } catch (final AccessDeniedException e) {
            LOG.error(e.getMessage(), e);
        } catch (final ItemExistsException e) {
            LOG.error(e.getMessage(), e);
        } catch (final ReferentialIntegrityException e) {
            LOG.error(e.getMessage(), e);
        } catch (final ConstraintViolationException e) {
            LOG.error(e.getMessage(), e);
        } catch (final InvalidItemStateException e) {
            LOG.error(e.getMessage(), e);
        } catch (final VersionException e) {
            LOG.error(e.getMessage(), e);
        } catch (final LockException e) {
            LOG.error(e.getMessage(), e);
        } catch (final NoSuchNodeTypeException e) {
            LOG.error(e.getMessage(), e);
        } catch (final RepositoryException e) {
            LOG.error(e.getMessage(), e);
        }

    }

    /**
     * Try to get the direct child node of a node by name.
     * 
     * @param node
     *            the node
     * @param childNodeName
     *            the child node name
     * @return the childNode if found, null otherwise
     */
    public static Node getChildNodeByName(final Node node,
                                          final String childNodeName) {

        if (node == null || StringUtils.isEmpty(childNodeName)) {
            return null;
        }

        try {
            if (node.hasNode(childNodeName)) {
                return node.getNode(childNodeName);
            }
        } catch (final PathNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (final RepositoryException e) {
            LOG.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Check if the node has a child node with the given name.
     * 
     * @param node
     *            the node
     * @param name
     *            the name
     * @return true if a child node with the given name exists, otherwise false.
     */
    public static boolean nodeHasChild(final Node node,
                                       final String name) {

        if (node == null || StringUtils.isEmpty(name)) {
            return false;
        }

        try {
            return node.hasNode(name);
        } catch (final RepositoryException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * Gets the property as string from the node.
     * 
     * @param node
     *            the node
     * @param propertyKey
     *            the property key
     * @return the property as string
     */
    public static String getPropertyAsString(final Node node,
                                             final String propertyKey) {

        if (node == null || StringUtils.isBlank(propertyKey)) {
            throw new IllegalStateException("The given node is null or the property key was empty.");
        }

        final String result = StringUtils.EMPTY;
        try {
            if (node.hasProperty(propertyKey)) {
                return node.getProperty(propertyKey).getValue().getString();
            } else {
                LOG.warn("The property for the key {} does not exist on the given node.", propertyKey);
            }
        } catch (final RepositoryException e) {
            LOG.warn("The property for the key {} does not exist.", propertyKey);
        }
        return result;
    }

    /**
     * Gets the jcrcontent-Node of the given Node.
     * 
     * @param node
     *            the node
     * @return node the node
     * @throws RepositoryException
     */
    public static Node getPageNode(final Node node) {
        Node basenode = node;
        try {
            if (basenode.isNodeType("cq:Page")) {
                basenode = NodeUtil.getChildNodeByName(basenode, JcrConstants.JCR_CONTENT);
            } else {
                while (!JcrConstants.JCR_CONTENT.equals(basenode.getName())) {
                    basenode = NodeUtil.getParentNode(basenode);
                }
            }
        } catch (RepositoryException e) {
            LOG.error(e.getMessage());
        }
        return basenode;
    }

    /**
     * Gets the lockOwner of the given Node.
     * 
     * @param node
     *            the node
     * @return lockOwner as String
     */
    public static String getLockOwner(final Node node) {

        return NodeUtil.getPropertyAsString(getPageNode(node), JcrConstants.JCR_LOCKOWNER);
    }

    /**
     * Gets the parent node of the given Node.
     * 
     * @param node
     *            the node
     * @return the parentNode
     */
    public static Node getParentNode(final Node node) {
        Node parentNode = null;
        try {
            parentNode = node.getNode("../");
        } catch (RepositoryException e) {
            LOG.error(e.getMessage());
        }
        return parentNode;
    }

    /**
     * Get the node at <code>relPath</code> from <code>baseNode</code> or <code>null</code> if no
     * such node exists.
     * 
     * @param baseNode
     *            existing node that should be the base for the relative path
     * @param relPath
     *            relative path to the node to get
     * @return the node at <code>relPath</code> from <code>baseNode</code> or <code>null</code> if
     *         no such node exists.
     * @throws RepositoryException
     *             in case of exception accessing the Repository
     */
    public static Node getNodeIfExists(final Node baseNode,
                                       final String relPath) throws RepositoryException {
        try {
            return baseNode.getNode(relPath);
        } catch (PathNotFoundException e) {
            return null;
        }
    }
}
