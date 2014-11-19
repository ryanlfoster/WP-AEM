/*
 * (c) 2009 General Motors Corp. All rights reserved. This material is solely and exclusively owned by General Motors
 * and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.wrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class {@link GMNode} wraps a Node to provide custom functionality. It implements the {@link Node} interface, to
 * be backwards compatible.
 * 
 * @author michaelwegener, namics ag
 * @since GMWP Release 2.5
 */
public final class GMNode {

    private static final Logger LOG = LoggerFactory.getLogger(GMNode.class);
    private static final String INVALID_NODE = "Invalid GMNode";
    private final Node node;
    private Map<String, String> propMapCache;

    /**
     * Instantiates a new gm node.
     * 
     * @param node
     *            the node
     */
    public GMNode(final Node node) {
        this.node = node;
    }

    /**
     * Give back an empty {@link GMNode} object, to avoid giving back null references.
     * 
     * @return the gm node
     */
    public static GMNode emptyGMNode() {
        return new GMNode(null);
    }

    /**
     * Gets the node.
     * 
     * @return the node
     */
    public Node getNode() {
        return this.node;
    }

    /**
     * Copies to current {@link GMNode} to the targetPath at the end of the new parent {@link GMNode}. If the copy
     * process was successful, then the copied {@link GMNode} will be returned (Because the node name can change when it
     * is already taken). Else null will be returned to indicate the failure.
     * 
     * @param targetPath
     *            path to copy target location
     * @return copied {@link GMNode}, or null if the process failed
     */
    public GMNode copyToPath(final String targetPath) {
        if (!isValidGMNode() || StringUtils.isEmpty(targetPath)) {
            return null;
        }

        String nodeName = StringUtils.EMPTY;
        final GMNode targetParentNode = getParentOfPath(targetPath);
        try {
            nodeName = targetParentNode.availabeChildNodeName(targetPath.substring(targetPath.lastIndexOf('/') + 1));
            // save all previous made changes on the session. this needs to be done because to 'copy' method on the
            // workspace saves directly to the CRX. Currently there is no way to copy within the session context.
            getSession().save();
            getNode().getSession().getWorkspace()
                    .copy(getPath(), targetParentNode.getPath().concat("/").concat(nodeName));
        } catch (final RepositoryException e) {
            LOG.error("Unable to copy node " + this.node, e);
            return null;
        }

        return targetParentNode.getGMNode(nodeName);
    }

    /**
     * Moved the current {@link GMNode} to the targetPath at the end of the parent {@link GMNode}. If the move process
     * was successful, then the moved {@link GMNode} will be returned (Because the node name can change when it is
     * already taken). Else null will be returned to indicate the failure.
     * 
     * @param targetPath
     *            path to move target location
     * @return moved {@link GMNode}, or null if the process failed
     */
    public GMNode moveToPath(final String targetPath) {
        if (!isValidGMNode() || StringUtils.isEmpty(targetPath)) {
            return null;
        }

        String nodeName = StringUtils.EMPTY;
        final GMNode targetParentNode = getParentOfPath(targetPath);
        try {
            nodeName = targetParentNode.availabeChildNodeName(targetPath.substring(targetPath.lastIndexOf('/') + 1));
            if (!getNode().isLocked()) {
                getNode().getSession().move(getPath(), targetParentNode.getPath().concat("/").concat(nodeName));
            }
        } catch (final RepositoryException e) {
            LOG.error("Unable to move node " + this.node, e);
            return null;
        }
        return targetParentNode.getGMNode(nodeName);
    }

    /**
     * Removes the current {@link GMNode}. If successful true will be returned.
     * 
     * @return true if the current {@link GMNode} could be removed
     */
    public boolean remove() {
        try {
            if (isValidGMNode()) {
                this.node.remove();
                return true;
            }
        } catch (final RepositoryException e) {
            LOG.error("Unable to delete node " + this.node, e);
        }

        return false;
    }

    /**
     * Checks, whether a certain {@link GMNode} has the current {@link GMNode} as a parent.
     * 
     * @param node
     *            possible child {@link GMNode} to check
     * @return true, if current {@link GMNode} is parent of a certain {@link GMNode}
     */
    public boolean hasChildNode(final GMNode node) {
        return isValidGMNode() && node.getParent() == this;
    }

    /**
     * Checks if the current {@link GMNode} is valid.
     * 
     * @return true if current {@link GMNode} is valid
     */
    public boolean isValidGMNode() {
        return null != this.node;
    }

    /**
     * Returns a name that isn't take of any children of the current {@link GMNode}. If possible the desired node name
     * will be used, else there will be a modified name returned.
     * 
     * @param desiredNodeName
     *            name that should be used when possible
     * @return free to use node name
     */
    public String availabeChildNodeName(final String desiredNodeName) {
        // those a node with the desired name already exist?
        if (!hasNode(desiredNodeName)) {
            return desiredNodeName;
        }

        // create an alternative node name, that doesn't alreasy exist
        int count = 0;
        String recommendedNodeName;

        do {
            recommendedNodeName = desiredNodeName + count;
            count++;
        } while (hasNode(recommendedNodeName));

        return recommendedNodeName;
    }

    /**
     * Checks whether the current {@link GMNode} has a specific child {@link GMNode}.
     * 
     * @param nodeName
     *            name of the {@link GMNode} that has to be checked
     * @return true, if the node name exists as a child of the current {@link GMNode}
     */
    public boolean hasNode(final String nodeName) {
        try {
            if (isValidGMNode() && this.node.hasNode(nodeName)) {
                return true;
            }
        } catch (final RepositoryException e) {
            LOG.error("Unable to check if child node with name " + nodeName + " exists within parent " + this.node, e);
        }
        return false;
    }

    /**
     * Checks whether the current {@link GMNode} has a specific property.
     * 
     * @param propertyName
     *            name of the {@link GMNode} that has to be checked
     * @return true, if the property exists of the current {@link GMNode}
     */
    public boolean hasProperty(final String propertyName) {
        try {
            if (isValidGMNode() && this.node.hasProperty(propertyName)) {
                return true;
            }
        } catch (final RepositoryException e) {
            LOG.error("Unable to check if property with name " + propertyName + " exists within node " + this.node, e);
        }
        return false;
    }

    /**
     * Gets the child {@link GMNode} of the current {@link GMNode}.
     * 
     * @param nodeName
     *            name of the {@link GMNode} that should be returned
     * @return {@link GMNode} of the node name from the current {@link GMNode}
     */
    public GMNode getGMNode(final String nodeName) {
        Node childNode = null;
        if (isValidGMNode() && StringUtils.isNotEmpty(nodeName) && hasNode(nodeName)) {
            try {
                childNode = this.node.getNode(nodeName);
            } catch (final RepositoryException e) {
                LOG.error("Unable to get child node with name " + nodeName + " from parent " + this.node, e);
            }
        }
        return new GMNode(childNode);
    }

    /**
     * Gets a list of all the children {@link GMNode} of the current {@link GMNode}.
     * 
     * @return list of children {@link GMNode}
     */
    public List<GMNode> getGMNodes() {
        final List<GMNode> childNodes = new ArrayList<GMNode>();
        if (isValidGMNode()) {
            try {
                final NodeIterator nodeItr = this.node.getNodes();
                while (nodeItr.hasNext()) {
                    final GMNode childNode = new GMNode(nodeItr.nextNode());
                    childNodes.add(childNode);
                }
            } catch (final RepositoryException e) {
                LOG.error("Unable to get children from parent node" + this.node, e);
            }
        }
        return childNodes;
    }

    /**
     * Gets the name of the current {@link GMNode}.
     * 
     * @return name of current {@link GMNode}
     */
    public String getName() {
        String nodeName = StringUtils.EMPTY;

        try {
            if (isValidGMNode()) {
                nodeName = this.node.getName();
            }
        } catch (final RepositoryException e) {
            LOG.error("Unable to get node name for node " + this.node, e);
        }

        return nodeName;
    }

    /**
     * Gets the Session.
     * 
     * @return session
     */
    public Session getSession() {
        if (isValidGMNode()) {
            try {
                return this.node.getSession();
            } catch (final RepositoryException e) {
                LOG.error("Unable to get session for node " + this.node, e);
            }
        }
        return null;
    }

    /**
     * Gets the path of the current {@link GMNode}. If the path cannot be retrieved, an emptry string will be returned.
     * 
     * @return path of the current {@link GMNode}, or an empty string if the path is not available
     */
    public String getPath() {
        if (isValidGMNode()) {
            try {
                return this.node.getPath();
            } catch (final RepositoryException e) {
                LOG.error("Could not get path for node " + this.node);
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * Gets the position of the child {@link GMNode} of the current {@link GMNode}. 0 stands for the top {@link GMNode}
     * child. If the current {@link GMNode} has no child with the passed name, then -1 will be returned.
     * 
     * @param childNodeName
     *            name of possible child {@link GMNode}
     * @return position of {@link GMNode} child, of -1 if the child does not exist
     */
    public int getPositionOfChild(final String childNodeName) {
        if (StringUtils.isNotEmpty(childNodeName)) {
            final List<GMNode> childNodes = getGMNodes();

            for (int pos = 0; pos < childNodes.size(); pos++) {
                if (childNodes.get(pos).getName().equals(childNodeName)) {
                    return pos;
                }
            }
        }

        return -1;
    }

    /**
     * Gets the parent {@link GMNode} of the current {@link GMNode}.
     * 
     * @return parent {@link GMNode}
     */
    public GMNode getParent() {
        if (isValidGMNode()) {
            try {
                return new GMNode(this.node.getParent());
            } catch (final RepositoryException e) {
                LOG.error("Unable to get parent node for node " + this.node, e);
            }
        }
        return emptyGMNode();
    }

    /**
     * Set a property value for a property name.
     * 
     * @param <E>
     *            property value type
     * @param propertyName
     *            name of property
     * @param propertyValue
     *            value of property
     * @throws RepositoryException
     *             if property could not be set
     */
    public <E extends Object> void setProperty(final String propertyName,
                                               final E propertyValue) throws RepositoryException {
        if (propertyValue instanceof Value) {
            getNode().setProperty(propertyName, (Value) propertyValue);
        } else if (propertyValue instanceof Value[]) {
            getNode().setProperty(propertyName, (Value[]) propertyValue);
        } else if (propertyValue instanceof String[]) {
            getNode().setProperty(propertyName, (String[]) propertyValue);
        } else if (propertyValue instanceof String) {
            getNode().setProperty(propertyName, (String) propertyValue);
        } else if (propertyValue instanceof Binary) {
            getNode().setProperty(propertyName, (Binary) propertyValue);
        } else if (propertyValue instanceof Boolean) {
            getNode().setProperty(propertyName, (Boolean) propertyValue);
        } else if (propertyValue instanceof Double) {
            getNode().setProperty(propertyName, (Double) propertyValue);
        } else if (propertyValue instanceof BigDecimal) {
            getNode().setProperty(propertyName, (BigDecimal) propertyValue);
        } else if (propertyValue instanceof Long) {
            getNode().setProperty(propertyName, (Long) propertyValue);
        } else if (propertyValue instanceof Calendar) {
            getNode().setProperty(propertyName, (Calendar) propertyValue);
        } else if (propertyValue instanceof Node) {
            getNode().setProperty(propertyName, (Node) propertyValue);
        } else {
            throw new RepositoryException("Unable to set property with value " + propertyValue);
        }
    }

    /**
     * Gets the property of the current {@link GMNode}.
     * 
     * @param propertyName
     *            name of the property
     * @return property, or null if not found
     */
    public Property getProperty(final String propertyName) {
        if (!isValidGMNode()) {
            return null;
        }

        if (hasProperty(propertyName)) {
            try {
                return getNode().getProperty(propertyName);
            } catch (final RepositoryException e) {
                LOG.error("Unable to get property " + propertyName + " of node " + this.node, e);
            }
        }
        return null;
    }

    /**
     * Gets the property as string from the node.
     * 
     * @param propertyKey
     *            the property key
     * @return the property as string
     */
    public String getPropertyAsString(final String propertyKey) {
        if (this.node == null || StringUtils.isBlank(propertyKey)) {
            throw new IllegalStateException("The given node is null or the property key was empty.");
        }

        final String result = StringUtils.EMPTY;
        try {
            if (this.node.hasProperty(propertyKey)) {
                return this.node.getProperty(propertyKey).getValue().getString();
            } else {
                LOG.warn("The property for the key {} does not exist on the given node.", propertyKey);
            }
        } catch (final RepositoryException e) {
            LOG.warn("The property for the key {} does not exist.", propertyKey);
        }
        return result;
    }

    /**
     * Return the properties of the node as a map of (property name, property)-pairs. Only single values get returned.
     * 
     * @return the property map
     */
    public Map<String, String> getProp() {
        if (this.propMapCache != null) {
            return this.propMapCache;
        }
        this.propMapCache = new HashMap<String, String>();

        if (this.node != null) {
            try {
                final PropertyIterator properties = this.node.getProperties();
                while (properties.hasNext()) {
                    final Property prop = properties.nextProperty();
                    try {
                        // only if value is a single value
                        if (!prop.getDefinition().isMultiple()) {
                            this.propMapCache.put(prop.getName(), prop.getString());
                        }
                    } catch (final ValueFormatException ex) {
                        LOG.error("Error accessing value", ex);
                    } catch (final RepositoryException ex) {
                        LOG.error("Error accessing repository", ex);
                    }
                }
            } catch (final RepositoryException e) {
                LOG.error("Error accessing repository", e);
            }

        }

        return this.propMapCache;
    }

    /**
     * Move a property to current {@link GMNode} with a new name.
     * 
     * @param property
     *            property to move
     * @param newPropertyName
     *            new name of the property
     * @return true, if property was moved successfully
     */
    public boolean moveProperty(final Property property,
                                final String newPropertyName) {
        if (property == null) {
            return false;
        }

        try {
            if (property.isMultiple()) {
                setProperty(newPropertyName, property.getValues());
            } else {
                setProperty(newPropertyName, property.getValue());
            }
            property.remove();
            return true;
        } catch (final RepositoryException e) {
            LOG.error("Unable to move property " + property, e);
            return false;
        }
    }

    /**
     * Rename a specific property of the current {@link GMNode}.
     * 
     * @param oldName
     *            current name of the property
     * @param newName
     *            new name of the property
     * @return true, if property was renamed successfully
     */
    public boolean renameProperty(final String oldName,
                                  final String newName) {
        return moveProperty(getProperty(oldName), newName);
    }

    /**
     * Copy a property to current Node with a new name.
     * 
     * @param property
     *            property to move
     * @param newPropertyName
     *            new name of the property
     * @return true, if property was copied successfully
     */
    public boolean copyProperty(final Property property,
                                final String newPropertyName) {
        if (property == null) {
            return false;
        }

        try {
            if (property.isMultiple()) {
                setProperty(newPropertyName, property.getValues());
            } else {
                setProperty(newPropertyName, property.getValue());
            }
            return true;
        } catch (final RepositoryException e) {
            LOG.error("Unable to copy property " + property, e);
            return false;
        }
    }

    /**
     * Reoders the current {@link GMNode} to the top.
     */
    public void orderFirst() {
        if (isValidGMNode()) {
            final List<GMNode> subnodes = getParent().getGMNodes();
            if (subnodes.size() > 1) {
                final GMNode firstNode = subnodes.get(0);
                orderBefore(firstNode);
            }
        }
    }

    /**
     * Reorders the current {@link GMNode} after another {@link GMNode}.
     * 
     * @param referenceNode
     *            reference {@link GMNode}
     */
    public void orderAfter(final GMNode referenceNode) {
        if (isValidGMNode()) {
            final List<GMNode> subnodes = getParent().getGMNodes();
            final int referenceNodePosition = getParent().getPositionOfChild(referenceNode.getName());

            // if current node should be placed after the last entry.
            if (referenceNodePosition == subnodes.size() - 1) {
                // place as second last entry
                orderBefore(referenceNode);
                // switch last and second last entry
                referenceNode.orderBefore(this);

            } else {
                final GMNode orderBeforeNode = subnodes.get(referenceNodePosition + 1);

                if (!orderBeforeNode.getName().equals(getName())) {
                    orderBefore(orderBeforeNode);
                }
            }

        }
    }

    /**
     * Reorders the current {@link GMNode} before another {@link GMNode}.
     * 
     * @param referenceNode
     *            reference {@link GMNode}
     */
    public void orderBefore(final GMNode referenceNode) {
        try {
            getNode().getParent().orderBefore(getName(), referenceNode.getName());
        } catch (final RepositoryException e) {
            LOG.error("Unable to order node " + this.node, e);
        }
    }

    /**
     * Gets parent {@link GMNode} of a certain path.
     * 
     * @param childPath
     *            path to the child {@link GMNode}
     * @return parent {@link GMNode}
     */
    private GMNode getParentOfPath(final String childPath) {
        if (isValidGMNode() && StringUtils.isNotEmpty(childPath)) {
            final String targetParentPath = childPath.substring(0, childPath.lastIndexOf('/'));
            try {
                return new GMNode(getNode().getSession().getNode(targetParentPath));
            } catch (final RepositoryException e) {
                LOG.error("Unable to get node at path " + targetParentPath, e);
            }
        }

        return new GMNode(null);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (isValidGMNode()) {
            return getPath();
        } else {
            return INVALID_NODE;
        }
    }

}
