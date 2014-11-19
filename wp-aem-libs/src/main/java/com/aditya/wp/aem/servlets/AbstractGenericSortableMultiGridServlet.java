/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.io.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.utils.NodeUtil;
import com.aditya.gmwp.aem.wrapper.GMResource;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public abstract class AbstractGenericSortableMultiGridServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 3089141915713475236L;

    /** The Constant ERROR_400. */
    private static final int ERROR_400 = 400;

    /**
     * Column model for the sortable multi grid.
     * 
     * @author chauzenberger, namics AG
     * @since GMWP Release 2.4
     */
    private static final class ColumnModel {

        /** The id. */
        private final String id;

        /** The title. */
        private final String title;

        /** The editable. */
        private final boolean editable;

        /**
         * Creates a new column model, assigns id and title and configures whether the cells of this column should be
         * editable or not.
         * 
         * @param id
         *            id of the column
         * @param title
         *            title of the column
         * @param editable
         *            if true, cells in this column are editable
         */
        private ColumnModel(final String id, final String title, final boolean editable) {
            this.id = id;
            this.title = title;
            this.editable = editable;
        }
    }

    /**
     * Row model for the sortable multi grid.
     * 
     * @author chauzenberger, namics AG
     * @since GMWP Release 2.4
     */
    private static final class RowModel {

        /** The id. */
        private final String id;

        /** The values. */
        private final String[] values;

        /** The index. */
        private int index;

        /** The checked. */
        private boolean checked;

        /** Flag for disabling row checkboxes. */
        private boolean disabled;

        /** The required. */
        private boolean required;

        /**
         * Creates a new row model and assigns the row values.
         * 
         * @param id
         *            row id
         * @param values
         *            row values
         */
        private RowModel(final String id, final String... values) {
            this.id = id;
            this.values = values;
            this.checked = false;
            this.index = Integer.MAX_VALUE;
            this.disabled = false;
            this.required = false;
        }

    }

    /**
     * Grid model for the sortable multi grid.
     * 
     * @author chauzenberger, namics AG
     * @since GMWP Release 2.4
     */
    public static final class GridModel {

        /** The columns. */
        private final List<ColumnModel> columns;

        /** The rows. */
        private final List<RowModel> rows;

        /** Flag to tell whether all options are checked */
        private boolean allOptionsChecked;

        /** Flag to tell whether the preset order is favoured over the stored order */
        private boolean preservePresetOrder;

        /**
         * Creates a new grid model.
         */
        public GridModel() {
            this(new LinkedList<AbstractGenericSortableMultiGridServlet.ColumnModel>());
        }

        /**
         * Creates a grid model and assigns the columns.
         * 
         * @param columns
         *            grid columns
         */
        private GridModel(final List<ColumnModel> columns) {
            this.columns = columns;
            this.rows = new LinkedList<AbstractGenericSortableMultiGridServlet.RowModel>();
            this.allOptionsChecked = false;
            this.preservePresetOrder = false;
        }

        /**
         * Adds a column to the grid.
         * 
         * @param id
         *            id of the column
         * @param title
         *            title of the column
         */
        public void addColumn(final String id,
                              final String title) {
            this.addColumn(id, title, false);
        }

        /**
         * Adds a column to the grid.
         * 
         * @param id
         *            id of the column
         * @param title
         *            title of the column
         * @param editable
         *            if true, cells are editable
         */
        public void addColumn(final String id,
                              final String title,
                              final boolean editable) {
            this.columns.add(new ColumnModel(id, title, editable));
        }

        /**
         * Adds a row to the grid.
         * 
         * @param id
         *            row id
         * @param values
         *            values
         */
        public void addRow(final String id,
                           final String... values) {
            this.rows.add(new RowModel(id, values));
        }

        /**
         * Adds a row to the grid.
         * 
         * @param id
         *            row id
         * @param checked
         *            true if row is checked, false otherwise
         * @param values
         *            values
         */
        public void addRow(final String id,
                           final boolean checked,
                           final String... values) {
            final RowModel row = new RowModel(id, values);
            row.checked = checked;
            this.rows.add(row);
        }

        /**
         * Adds a row to the grid.
         * 
         * @param id
         *            row id
         * @param checked
         *            true if row is checked, false otherwise
         * @param disabled
         *            true if row is disabled, false otherwise
         * @param values
         *            values
         */
        public void addRow(final String id,
                           final boolean checked,
                           final boolean disabled,
                           final String... values) {
            final RowModel row = new RowModel(id, values);
            row.checked = checked;
            row.disabled = disabled;
            this.rows.add(row);
        }

        /**
         * Adds a row to the grid.
         * 
         * @param id
         *            row id
         * @param checked
         *            true if row is checked, false otherwise
         * @param disabled
         *            true if row is disabled, false otherwise
         * @param required
         *            true if row is required, false otherwise
         * @param values
         *            values
         */
        public void addRow(final String id,
                           final boolean checked,
                           final boolean disabled,
                           final boolean required,
                           final String... values) {
            final RowModel row = new RowModel(id, values);
            row.checked = checked;
            row.disabled = disabled;
            row.required = required;
            this.rows.add(row);
        }

        /**
         * Returns the index of a column in the grid.
         * 
         * @param id
         *            id of the column
         * @return index of the column in the grid
         */
        public int getColumnIndex(final String id) {
            for (int i = 0; i < this.columns.size(); i++) {
                if (StringUtils.equals(this.columns.get(i).id, id)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Tells whether all options are checked or not.
         * 
         * @return true if all options are checked
         */
        public boolean isAllOptionsChecked() {
            return this.allOptionsChecked;
        }

        /**
         * Sets whether all options should be checked or not.
         * 
         * @param allOptionsChecked
         *            if true, all options are checked
         */
        public void setAllOptionsChecked(final boolean allOptionsChecked) {
            this.allOptionsChecked = allOptionsChecked;
        }

        /**
         * Tells whether the original preset order is favoured over the stored order.
         * 
         * @return true if preset order is favoured over the stored order
         */
        public boolean isPreservePresetOrder() {
            return this.preservePresetOrder;
        }

        /**
         * Sets whether the original preset order is favoured over the stored order.
         * 
         * @param preservePresetOrder
         *            if true, preset order is favoured over the stored order
         */
        public void setPreservePresetOrder(final boolean preservePresetOrder) {
            this.preservePresetOrder = preservePresetOrder;
        }
    }

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGenericSortableMultiGridServlet.class);

    /** The Constant JSON_CONTENT_TYPE. */
    private static final String JSON_CONTENT_TYPE = "application/json;charset=utf-8;";

    /** The Constant UTF_8. */
    private static final String UTF_8 = "UTF-8";

    /** The Constant ATTR_STORAGE_NODE. */
    private static final String ATTR_STORAGE_NODE = "storage_node";

    /** The Constant ATTR_JSON. */
    private static final String ATTR_JSON = "json";

    /** The Constant PROPERTY_CHECKED. */
    private static final String PROPERTY_CHECKED = "selected";

    /** The Constant PROPERTY_REQUIRED. */
    private static final String PROPERTY_REQUIRED = "required";

    /** The Constant PROPERTY_DISABLED. */
    private static final String PROPERTY_DISABLED = "disabled";

    /** The Constant PROPERTY_ID. */
    private static final String PROPERTY_ID = "id";

    /** The Constant PROPERTY_INDEX. */
    private static final String PROPERTY_INDEX = "index";

    /** The Constant PROPERTY_TITLE. */
    private static final String PROPERTY_TITLE = "title";

    /** The Constant PROPERTY_EDITABLE. */
    private static final String PROPERTY_EDITABLE = "editable";

    /** The Constant PROPERTY_ALL_OPTIONS_CHECKED */
    private static final String PROPERTY_ALL_OPTIONS_CHECKED = "allOptionsChecked";

    /** The Constant PROPERTY_ROWS. */
    private static final String PROPERTY_ROWS = "rows";

    /** The Constant PROPERTY_COLUMNS. */
    private static final String PROPERTY_COLUMNS = "columns";

    /** The Constant TEMPLATE_NODE_NAME. */
    private static final String TEMPLATE_NODE_NAME = "node_{num}";

    /*
     * (non-Javadoc)
     * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest,
     * org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected final void doGet(final SlingHttpServletRequest request,
                               final SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8);

        final JSONWriter w = new JSONWriter(response.getWriter());

        try {
            final String nodeName = request.getParameter(ATTR_STORAGE_NODE);
            if (StringUtils.isNotBlank(nodeName)) {
                final Node parentNode = getOrCreateParentNode(request);
                final Node storageNode = getOrCreateStorageNode(parentNode, nodeName);

                final GridModel preset = getPreset(request);
                final Map<String, Map<String, Object>> stored = getStoredValues(storageNode, preset);
                final boolean storedAllOptionsChecked = getStoredAllOptionsChecked(storageNode);
                final GridModel mixed = mixin(preset, stored);
                mixed.setAllOptionsChecked(storedAllOptionsChecked);
                toJSON(mixed, w);
            } else {
                LOG.error("parameter " + ATTR_STORAGE_NODE + " not available");
                response.sendError(ERROR_400);
            }
        } catch (final JSONException e) {
            LOG.error("Unable to create JSON", e);
            throw new ServletException(e);
        } catch (final RepositoryException e) {
            LOG.error("Unable to access grid storage node", e);
            throw new ServletException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache.sling.api.SlingHttpServletRequest,
     * org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected final void doPost(final SlingHttpServletRequest request,
                                final SlingHttpServletResponse response) throws ServletException, IOException {

        final Node parentNode = request.getResource().adaptTo(Node.class);
        final String nodeName = request.getParameter(ATTR_STORAGE_NODE);
        final String json = request.getParameter(ATTR_JSON);
        if (StringUtils.isNotBlank(nodeName) && StringUtils.isNotBlank(json)) {
            try {
                final GridModel postedModel = fromJSON(json, request);
                storeValues(postedModel, parentNode, nodeName);
                response.getWriter().println("<div id=\"Status\">200</div>");
            } catch (final JSONException e) {
                LOG.error("Unable to parse JSON", e);
                throw new ServletException(e);
            } catch (final RepositoryException e) {
                LOG.error("Unable to store values", e);
                throw new ServletException(e);
            }
        } else {
            response.sendError(ERROR_400);
        }
    }

    /**
     * Loads the preset data for the grid. The preset data is independent from the values stored by the component and
     * serves as template for all values that should be available.
     * 
     * @param request
     *            current request
     * @return preset
     */
    protected abstract GridModel getPreset(final SlingHttpServletRequest request);

    /**
     * Returns a translated text form the localized resource bundle.
     * 
     * @param key
     *            key of the text
     * @param request
     *            current request where the bundle can be found
     * @return translated text
     */
    protected final String getI18nText(final String key,
                                       final SlingHttpServletRequest request) {
        final ResourceBundle bundle = request.getResourceBundle(request.getLocale());
        try {
            return bundle.getString(key);
        } catch (final MissingResourceException e) {
            return key;
        }
    }

    /**
     * Transforms the grid model to JSON and writes it into the JSON writer.
     * 
     * @param grid
     *            grid model
     * @param w
     *            json writer
     * @throws JSONException
     *             problem writing JSON
     */
    private void toJSON(final GridModel grid,
                        final JSONWriter w) throws JSONException {
        w.object();
        w.key(PROPERTY_COLUMNS);
        w.array();
        for (final ColumnModel col : grid.columns) {
            w.object();
            w.key(PROPERTY_TITLE);
            w.value(col.title);
            w.key(PROPERTY_ID);
            w.value(col.id);
            w.key(PROPERTY_EDITABLE);
            w.value(col.editable);
            w.endObject();
        }
        w.endArray();
        w.key(PROPERTY_ROWS);
        w.array();
        for (final RowModel row : grid.rows) {
            w.object();
            w.key(PROPERTY_ID);
            w.value(row.id);
            w.key(PROPERTY_CHECKED);
            w.value(row.checked);
            w.key(PROPERTY_DISABLED);
            w.value(row.disabled);
            w.key(PROPERTY_REQUIRED);
            w.value(row.required);
            for (int i = 0; i < row.values.length; i++) {
                if (grid.columns.size() > i) {
                    w.key(grid.columns.get(i).id);
                    w.value(row.values[i]);
                }
            }
            w.endObject();
        }
        w.endArray();
        w.key(PROPERTY_ALL_OPTIONS_CHECKED);
        w.value(grid.isAllOptionsChecked());
        w.endObject();
    }

    /**
     * Creates a grid model from JSON values. The preset is used as template for the rows.
     * 
     * @param json
     *            json representation
     * @param request
     *            current request
     * @return grid model
     * @throws JSONException
     *             problem writing JSON
     */
    private GridModel fromJSON(final String json,
                               final SlingHttpServletRequest request) throws JSONException {
        final String jsonString = json.replaceAll("\n", "");
        final JSONObject jsonObject = new JSONObject(jsonString);
        final GridModel preset = getPreset(request);
        final GridModel fromJSON = new GridModel(preset.columns);
        final JSONArray rows = jsonObject.getJSONArray(PROPERTY_ROWS);
        if (rows != null) {
            for (int i = 0; i < rows.length(); i++) {
                final JSONObject row = rows.getJSONObject(i);
                addRowModel(row, fromJSON);
            }
        }
        if (jsonObject.has(PROPERTY_ALL_OPTIONS_CHECKED)) {
            fromJSON.setAllOptionsChecked(jsonObject.getBoolean(PROPERTY_ALL_OPTIONS_CHECKED));
        }
        return fromJSON;
    }

    /**
     * Adds a json row to the grid model.
     * 
     * @param jsonRow
     *            json row
     * @param grid
     *            grid model
     * @throws JSONException
     *             problem writing JSON
     */
    private void addRowModel(final JSONObject jsonRow,
                             final GridModel grid) throws JSONException {
        final List<ColumnModel> columns = grid.columns;
        final List<String> values = new LinkedList<String>();
        if (jsonRow.has(PROPERTY_ID)) {
            final String id = jsonRow.getString(PROPERTY_ID);
            boolean checked = false;
            if (jsonRow.has(PROPERTY_CHECKED)) {
                checked = jsonRow.getBoolean(PROPERTY_CHECKED);
            }
            boolean required = false;
            if (jsonRow.has(PROPERTY_REQUIRED)) {
                required = jsonRow.getBoolean(PROPERTY_REQUIRED);
            }
            for (final ColumnModel column : columns) {
                if (jsonRow.has(column.id)) {
                    values.add(jsonRow.getString(column.id));
                }
            }
            grid.addRow(id, checked, false, required, values.toArray(new String[0]));
        }

    }

    /**
     * Returns the storage node of the grid values. If the storage node is not yet existing it is created.
     * 
     * @param parentNode
     *            parent node
     * @param nodeName
     *            node name
     * @return storage node
     * @throws RepositoryException
     *             repository exception happend
     */
    private Node getOrCreateStorageNode(final Node parentNode,
                                        final String nodeName) throws RepositoryException {

        if (NodeUtil.nodeHasChild(parentNode, nodeName)) {
            return NodeUtil.getChildNodeByName(parentNode, nodeName);
        } else {
            final Node storageNode = parentNode.addNode(nodeName);
            storageNode.getSession().save();
            return storageNode;
        }
    }

    /**
     * Returns the node of the request. If the request node does not exist, it is created.
     * 
     * @param request
     *            current request
     * @return request node
     * @throws RepositoryException
     *             repository exception happend
     */
    private synchronized Node getOrCreateParentNode(final SlingHttpServletRequest request) throws RepositoryException {
        final GMResource resource = new GMResource(request.getResource());
        if (!(resource.isNotExisting())) {
            return resource.adaptTo(Node.class);
        } else {
            final GMResource parentResource = resource.getParent();
            final String nodeName = StringUtils.substringBefore(resource.getName(), ".");
            return getOrCreateStorageNode(parentResource.adaptTo(Node.class), nodeName);
        }
    }

    /**
     * Removes the storage node.
     * 
     * @param parentNode
     *            parent node
     * @param nodeName
     *            node name
     * @throws RepositoryException
     *             repository exception happend
     */
    private void removeStorageNode(final Node parentNode,
                                   final String nodeName) throws RepositoryException {
        if (NodeUtil.nodeHasChild(parentNode, nodeName)) {
            final Node storageNode = NodeUtil.getChildNodeByName(parentNode, nodeName);
            storageNode.remove();
            parentNode.getSession().save();
        }
    }

    /**
     * Return stored values.
     * 
     * @param storageNode
     *            storage node
     * @param preset
     *            a grid model
     * @return list of stored values
     * @throws RepositoryException
     *             repository exception happend
     */
    private Map<String, Map<String, Object>> getStoredValues(final Node storageNode,
                                                             final GridModel preset) throws RepositoryException {
        final Map<String, Map<String, Object>> storedValues = new HashMap<String, Map<String, Object>>();
        final NodeIterator children = storageNode.getNodes();
        int index = 0;
        while (children.hasNext()) {
            final Node child = children.nextNode();
            if (child.hasProperty(PROPERTY_ID)) {
                final Map<String, Object> values = new HashMap<String, Object>();
                boolean checked = false;
                if (child.hasProperty(PROPERTY_CHECKED)) {
                    checked = child.getProperty(PROPERTY_CHECKED).getBoolean();
                }
                values.put(PROPERTY_CHECKED, checked);
                boolean required = false;
                if (child.hasProperty(PROPERTY_REQUIRED)) {
                    required = child.getProperty(PROPERTY_REQUIRED).getBoolean();
                }
                values.put(PROPERTY_REQUIRED, required);
                for (final ColumnModel col : preset.columns) {
                    if (child.hasProperty(col.id)) {
                        values.put(col.id, child.getProperty(col.id).getString());
                    }
                }
                values.put(PROPERTY_INDEX, index++);
                storedValues.put(child.getProperty(PROPERTY_ID).getString(), values);
            }
        }
        return storedValues;
    }

    /**
     * Returns the stored flag for the selection of all options.
     * 
     * @param storageNode
     *            storage node
     * @return true if all options are checked, false otherwise
     * @throws RepositoryException
     */
    private boolean getStoredAllOptionsChecked(final Node storageNode) throws RepositoryException {
        if (storageNode != null && storageNode.hasProperty(PROPERTY_ALL_OPTIONS_CHECKED)) {
            return storageNode.getProperty(PROPERTY_ALL_OPTIONS_CHECKED).getBoolean();
        }
        return false;
    }

    /**
     * Mixes the stored values into the preset. All rows of the preset stay in place while cell values are replaced with
     * stored values. Rows are sorted in the order of the stored values with all new presets (preset rows where no
     * stored value is found) are placed last.
     * 
     * @param preset
     *            preset values
     * @param storedValues
     *            stored values
     * @return preset with stored values mixed in
     */
    protected GridModel mixin(final GridModel preset,
                              final Map<String, Map<String, Object>> storedValues) {
        // iterate over all preset rows
        // if the stored values contain the row id,
        // update the index, the checked flag and overwrite the stored values
        for (final RowModel row : preset.rows) {
            if (storedValues.containsKey(row.id)) {
                final Map<String, Object> rowValues = storedValues.get(row.id);
                for (final Map.Entry<String, Object> entry : rowValues.entrySet()) {
                    if (StringUtils.equals(entry.getKey(), PROPERTY_INDEX)) {
                        row.index = (Integer) entry.getValue();
                    } else if (StringUtils.equals(entry.getKey(), PROPERTY_CHECKED)) {
                        row.checked = (Boolean) entry.getValue();
                    } else if (StringUtils.equals(entry.getKey(), PROPERTY_REQUIRED)) {
                        row.required = (Boolean) entry.getValue();
                    } else {
                        final int index = preset.getColumnIndex(entry.getKey());
                        if (index >= 0 && index < row.values.length) {
                            row.values[index] = (String) entry.getValue();
                        }
                    }
                }
            }
        }
        if (!preset.isPreservePresetOrder()) {
            // sort rows
            Collections.sort(preset.rows, new Comparator<RowModel>() {
                @Override
                public int compare(final RowModel o1, final RowModel o2) {
                    return o1.index - o2.index;
                }

            });
        }
        return preset;
    }

    /**
     * Stores the grid values in the storage node.
     * 
     * @param storeModel
     *            grid model to be stored
     * @param parentNode
     *            parent node
     * @param nodeName
     *            storage node name
     * @throws RepositoryException
     *             repository exception happend
     */
    private void storeValues(final GridModel storeModel,
                             final Node parentNode,
                             final String nodeName) throws RepositoryException {
        // flush storage node
        removeStorageNode(parentNode, nodeName);
        final Node storageNode = getOrCreateStorageNode(parentNode, nodeName);
        storageNode.setProperty(PROPERTY_ALL_OPTIONS_CHECKED, storeModel.isAllOptionsChecked());
        int index = 0;
        for (final RowModel model : storeModel.rows) {
            final String itemNodeName = TEMPLATE_NODE_NAME.replace("{num}", String.format("%03d", index));
            final Node itemNode = storageNode.addNode(itemNodeName);
            itemNode.setProperty(PROPERTY_ID, model.id);
            itemNode.setProperty(PROPERTY_CHECKED, model.checked);
            itemNode.setProperty(PROPERTY_REQUIRED, model.required);
            for (int i = 0; i < model.values.length; i++) {
                final ColumnModel column = storeModel.columns.get(i);
                if (column.editable) {
                    final String propertyName = column.id;
                    itemNode.setProperty(propertyName, model.values[i]);
                }
            }
            index++;
        }
        parentNode.getSession().save();
    }
}
