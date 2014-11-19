/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration.impl;

import java.util.Collections;
import java.util.List;

import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.exception.ContentMigrationException;
import com.aditya.gmwp.aem.services.contentmigration.Job;
import com.aditya.gmwp.aem.wrapper.GMNode;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public abstract class AbstractContentMigrationJob implements Job {

	private Session session;

	/**
	 * Move source Node to a specific parent Node at first position.
	 * 
	 * @param srcNode
	 *            source node to move
	 * @param targetParentNode
	 *            parent of target node
	 * @param allNodesMustExist
	 *            is the operation mandatory
	 * @return moved node
	 * @throws ContentMigrationException
	 *             content migration exception
	 */
	protected final GMNode moveNodeToTop(final GMNode srcNode,
	                                     final GMNode targetParentNode,
	                                     final boolean allNodesMustExist) throws ContentMigrationException {
		GMNode movedNode = null;
		if (allNodesMustExist || (srcNode.isValidGMNode() && targetParentNode.isValidGMNode())) {
			movedNode = srcNode.moveToPath(targetParentNode.getPath().concat("/").concat(srcNode.getName()));
			if (movedNode != null) {
				movedNode.orderFirst();
			}
			checkMovedNode(srcNode, movedNode);
		}

		return movedNode;
	}

	/**
	 * Move source Node to a specific parent Node at last position.
	 * 
	 * @param srcNode
	 *            source node to move
	 * @param targetParentNode
	 *            parent of target node
	 * @param allNodesMustExist
	 *            is the operation mandatory
	 * @return moved node
	 * @throws ContentMigrationException
	 *             content migration exception
	 */
	protected final GMNode moveNodeToBottom(final GMNode srcNode,
	                                        final GMNode targetParentNode,
	                                        final boolean allNodesMustExist) throws ContentMigrationException {
		GMNode movedNode = null;
		if (allNodesMustExist || (srcNode.isValidGMNode() && targetParentNode != null && targetParentNode.isValidGMNode())) {
			movedNode = srcNode.moveToPath(targetParentNode.getPath().concat("/").concat(srcNode.getName()));
			checkMovedNode(srcNode, movedNode);
		}

		return movedNode;
	}

	/**
	 * Move source Property to a specific parent Node.
	 * 
	 * @param targetNode
	 *            target node where the property will be moved to
	 * @param property
	 *            property that needs to be moved
	 * @param newPropertyName
	 *            new name of the property
	 * @param allNodesMustExist
	 *            is the operation mandatory
	 * @return true, if property has been moved
	 * @throws ContentMigrationException
	 *             content migration exception
	 */
	protected final boolean moveProperty(final GMNode targetNode,
	                                     final Property property,
	                                     final String newPropertyName,
	                                     final boolean allNodesMustExist) throws ContentMigrationException {
		boolean isPropertyMoved = false;
		if (allNodesMustExist || (targetNode.isValidGMNode() && property != null && StringUtils.isNotEmpty(newPropertyName))) {
			isPropertyMoved = targetNode.moveProperty(property, newPropertyName);
			checkMovedProperty(property, isPropertyMoved);
		}

		return isPropertyMoved;
	}

	/**
	 * Move children to a specific target node as top elements.
	 * 
	 * @param children
	 *            List of node to move
	 * @param targetNode
	 *            target node
	 */
	protected final void moveChildrenToNodeAsFirstElements(final List<GMNode> children,
	                                                       final GMNode targetNode) {
		if (CollectionUtils.isNotEmpty(children)) {
			Collections.reverse(children);
			for (final GMNode childToMove : children) {
				childToMove.moveToPath(targetNode.getPath().concat("/").concat(childToMove.getName()));
				childToMove.orderFirst();
			}
		}
	}

	@Override
	public void init(final Session session) {
		this.session = session;
	}

	@Override
	public ExecutionType getExecutionType() {
		return ExecutionType.AUTO_EVERY_TIME;
	}

	/**
	 * Check if node was successfully moved. Throws an exception if an error occured.
	 * 
	 * @param srcNode
	 *            node that had to be moved
	 * @param movedNode
	 *            moved node
	 * @throws ContentMigrationException
	 *             content migration exception
	 */
	private void checkMovedNode(final GMNode srcNode,
	                            final GMNode movedNode) throws ContentMigrationException {
		if (movedNode == null) {
			throw new ContentMigrationException("Unable to move node " + srcNode);
		}
	}

	/**
	 * Check if property was successfully moved. Throws an exception if an error occured.
	 * 
	 * @param property
	 *            property that had to be moved
	 * @param isPropertyMoved
	 *            flag if property has been moved
	 * @throws ContentMigrationException
	 *             content migration exception
	 */
	private void checkMovedProperty(final Property property,
	                                final boolean isPropertyMoved) throws ContentMigrationException {
		if (!isPropertyMoved) {
			throw new ContentMigrationException("Unable to move property " + property);
		}
	}

	/**
	 * @return the session
	 */
	protected Session getSession() {
		return this.session;
	}
}
