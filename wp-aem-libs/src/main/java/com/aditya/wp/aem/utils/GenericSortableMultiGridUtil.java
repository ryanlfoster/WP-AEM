/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.wrapper.GMResource;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class GenericSortableMultiGridUtil {

    private static final String PROPERTY_CHECKED = "selected";

    private static final String PROPERTY_ID = "id";

    private static final String PROPERTY_ALL_OPTIONS_CHECKED = "allOptionsChecked";

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private GenericSortableMultiGridUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }

    /**
     * Returns the ids of all items that have been marked as selected.
     * 
     * @param resource
     *            resource of the generic sortable multi grid widget
     * @param nodeName
     *            name of the node where the fields are stored
     * @return list of selected ids in the correct order
     */
    public static List<String> getSelectedIds(final GMResource resource,
                                              final String nodeName) {
        final List<String> selectedIds = new LinkedList<String>();
        for (final ValueMap item : getItems(resource, nodeName)) {
            if (item.get(PROPERTY_CHECKED, false)) {
                selectedIds.add(item.get(PROPERTY_ID, String.class));
            }
        }
        return selectedIds;
    }

    /**
     * Returns all selected item nodes of the grid.
     * 
     * @param resource
     *            resource of the generic sortable multi grid widget
     * @param nodeName
     *            name of the node where the fields are stored
     * @return list of item nodes in the correct order
     */
    public static List<ValueMap> getSelectedItems(final GMResource resource,
                                                  final String nodeName) {
        return getItems(resource, nodeName, false);
    }

    /**
     * Returns all item nodes of the grid.
     * 
     * @param resource
     *            resource of the generic sortable multi grid widget
     * @param nodeName
     *            name of the node where the fields are stored
     * @return list of item nodes in the correct order
     */
    public static List<ValueMap> getItems(final GMResource resource,
                                          final String nodeName) {
        return getItems(resource, nodeName, true);
    }

    /**
     * Tells whether all options are selected or not.
     * 
     * @param resource
     *            resource of the generic sortable multi grid widget
     * @param nodeName
     *            name of the node where the fields are stored
     * @return true if all items are selected, false otherwise
     */
    public static boolean getAllOptionsSelected(final GMResource resource,
                                                final String nodeName) {
        if (resource.isExisting()) {
            final GMResource storageResource = resource.getChild(nodeName);
            if (storageResource.isExisting()) {
                return storageResource.getPropertyAsBoolean(PROPERTY_ALL_OPTIONS_CHECKED, false);
            }
        }
        return false;
    }

    /**
     * Returns item nodes of the grid.
     * 
     * @param resource
     *            resource of the generic sortable multi grid widget
     * @param nodeName
     *            name of the node where the fields are stored
     * @param returnUnselected
     *            tells whether unselected entries should be returned too
     * @return list of item nodes in the correct order
     */
    private static List<ValueMap> getItems(final GMResource resource,
                                           final String nodeName,
                                           final boolean returnUnselected) {
        final List<ValueMap> items = new LinkedList<ValueMap>();
        if (resource.isExisting()) {
            final GMResource storageResource = resource.getChild(nodeName);
            if (storageResource.isExisting()) {
                for (final GMResource res : storageResource.getChildren()) {
                    final ValueMap item = res.adaptTo(ValueMap.class);
                    if ((returnUnselected || item.get(PROPERTY_CHECKED, false))
                            && StringUtils.isNotEmpty(item.get(PROPERTY_ID, StringUtils.EMPTY))) {
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }
}