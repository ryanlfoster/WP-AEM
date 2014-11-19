/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.components;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.aditya.gmwp.aem.utils.dictionary.Dictionary;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.EditAction;
import com.day.cq.wcm.api.components.EditConfig;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.components.EditLayout;
import com.day.cq.wcm.api.components.Toolbar;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class EditContextUtil {

    private static final String TEXT = "text:";

    /**
     * Private constructor.
     */
    private EditContextUtil() {
        throw new AssertionError("This class is not meant to be instantiated.");
    }

    /**
     * The method adds or overwrites the first toolbar label.
     * 
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     * @param keyOrText
     *            a key from the language file or a text
     */
    public static void addOrOverwriteFirstToolbarLabel(final SlingHttpServletRequest request,
                                                       final EditContext editContext,
                                                       final String keyOrText) {

        final Toolbar toolbar = getToolbar(request, editContext);
        if (null != toolbar) {
            addOrOverwriteFirstToolbarLabel(toolbar, keyOrText);
        }
    }

    /**
     * The method returns the toolbar or null if not present.
     * 
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     * @return the toolbar or null if not present
     */
    public static Toolbar getToolbar(final SlingHttpServletRequest request,
                                     final EditContext editContext) {

        final EditConfig editConfig = getEditConfig(request, editContext);
        Toolbar toolbar = null;
        if (null != editConfig) {
            toolbar = getToolbar(editConfig);
        }
        return toolbar;
    }

    /**
     * The method returns the edit configuration or null if not found or if not in edit mode.
     * 
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     * @return the edit configuration or null if not found or if not in edit mode
     */
    public static EditConfig getEditConfig(final SlingHttpServletRequest request,
                                           final EditContext editContext) {

        EditConfig editConfig = null;
        if (null != editContext && WCMMode.fromRequest(request) == WCMMode.EDIT) {
            editConfig = editContext.getEditConfig();
        }
        return editConfig;
    }

    /**
     * The method returns the toolbar or null if not present.
     * 
     * @param editConfig
     *            the edit context
     * @return the toolbar or null if not present
     */
    public static Toolbar getToolbar(final EditConfig editConfig) {

        Toolbar toolbar = null;
        if (null != editConfig && editConfig.getLayout() == EditLayout.EDITBAR) {
            toolbar = editConfig.getToolbar();
        }
        return toolbar;
    }

    /**
     * The method adds or overwrites the first toolbar label.
     * 
     * @param toolbar
     *            the toolbar
     * @param keyOrText
     *            a key from the language file or a text
     */
    public static void addOrOverwriteFirstToolbarLabel(final Toolbar toolbar,
                                                       final String keyOrText) {

        final int index = searchIndexOfFirstToolbarLabel(toolbar);
        if (index < 0) {
            addToolbarLabel(toolbar, keyOrText);
        } else {
            overwriteToolbarLabel(toolbar, keyOrText, index);
        }
    }

    /**
     * The method returns the index of the first toolbar label or -1 if no toolbar label was found.
     * 
     * @param toolbar
     *            the toolbar
     * @return the index of the first toolbar label or -1 if no toolbar label was found
     */
    public static int searchIndexOfFirstToolbarLabel(final Toolbar toolbar) {

        final Iterator<Toolbar.Item> toolbarItems = toolbar.iterator();
        int indexOfLabel = -1;
        for (int index = 0; toolbarItems.hasNext(); index++) {
            final Toolbar.Item item = toolbarItems.next();
            if (item instanceof Toolbar.Label) {
                indexOfLabel = index;
                break;
            }
        }
        return indexOfLabel;
    }

    /**
     * The method overwrites the toolbar label.
     * 
     * @param toolbar
     *            the toolbar
     * @param keyOrText
     *            a key from the language file or a text
     * @param indexOfLabel
     *            the index of the toolbar label
     */
    private static void overwriteToolbarLabel(final Toolbar toolbar,
                                              final String keyOrText,
                                              final int indexOfLabel) {

        removeToolbarLabel(toolbar, indexOfLabel);
        addToolbarLabel(toolbar, keyOrText, indexOfLabel);
    }

    /**
     * The method adds an toolbar label to the toolbar.
     * 
     * @param toolbar
     *            the toolbar
     * @param keyOrText
     *            a key from the language file or a text
     */
    public static void addToolbarLabel(final Toolbar toolbar,
                                       final String keyOrText) {

        toolbar.add(new Toolbar.Label(keyOrText));
    }

    /**
     * The method adds an toolbar label to the toolbar.
     * 
     * @param toolbar
     *            the toolbar
     * @param keyOrText
     *            a key from the language file or a text
     * @param indexOfLabel
     *            the index of the toolbar label
     */
    public static void addToolbarLabel(final Toolbar toolbar,
                                       final String keyOrText,
                                       final int indexOfLabel) {

        toolbar.add(indexOfLabel, new Toolbar.Label(keyOrText));
    }

    /**
     * The method removes an editbar label from the toolbar.
     * 
     * @param toolbar
     *            the toolbar
     */
    public static void removeFistToolbarLabel(final Toolbar toolbar) {

        final int index = searchIndexOfFirstToolbarLabel(toolbar);
        if (index >= 0) {
            toolbar.remove(index);
        }

    }

    /**
     * The method removes an toolbar label from the toolbar.
     * 
     * @param toolbar
     *            the toolbar
     * @param indexOfLabel
     *            the index of the toolbar label
     */
    public static void removeToolbarLabel(final Toolbar toolbar,
                                          final int indexOfLabel) {

        toolbar.remove(indexOfLabel);
    }

    /**
     * The method removes all toolbar items expect of the first label.
     * 
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     */
    public static void removeAllToolbarItemsExpectOfFirstLabel(final SlingHttpServletRequest request,
                                                               final EditContext editContext) {

        final Toolbar toolbar = getToolbar(request, editContext);
        final int index = searchIndexOfFirstToolbarLabel(toolbar);
        final Toolbar.Item label = toolbar.get(index);
        toolbar.clear();
        toolbar.add(label);
    }

    /**
     * The method removes the edit action "delete".<br/>
     * <br/>
     * This can be used for fixed components to remove the "delete" button.
     * 
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     */
    public static void removeEditActionDelete(final SlingHttpServletRequest request,
                                              final EditContext editContext) {

        final Toolbar toolbar = getToolbar(request, editContext);
        toolbar.remove(EditAction.DELETE);
    }

    /**
     * The method sets the edit layout.
     * <p>
     * Possible layouts:
     * </p>
     * <ul>
     * <li>EDITBAR</li>
     * <li>ROLLOVER</li>
     * <li>AUTO</li>
     * <li>NONE</li>
     * </ul>
     * The method ignores the notation (EDITBAR, editbar, Editbar, ...).
     * 
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     * @param editLayout
     *            the edit layout
     */
    public static void setEditLayout(final SlingHttpServletRequest request,
                                     final EditContext editContext,
                                     final String editLayout) {

        if (EditLayout.EDITBAR.name().equalsIgnoreCase(editLayout)) {
            setEditLayout(request, editContext, EditLayout.EDITBAR);

        } else if (EditLayout.ROLLOVER.name().equalsIgnoreCase(editLayout)) {
            setEditLayout(request, editContext, EditLayout.ROLLOVER);

        } else if (EditLayout.AUTO.name().equalsIgnoreCase(editLayout)) {
            setEditLayout(request, editContext, EditLayout.AUTO);

        } else if (EditLayout.NONE.name().equalsIgnoreCase(editLayout)) {
            setEditLayout(request, editContext, EditLayout.NONE);
        }
    }

    /**
     * The method sets the edit layout.
     * 
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     * @param editLayout
     *            the edit layout
     */
    public static void setEditLayout(final SlingHttpServletRequest request,
                                     final EditContext editContext,
                                     final EditLayout editLayout) {

        final EditConfig editConfig = getEditConfig(request, editContext);
        if (null != editConfig) {
            editConfig.setLayout(editLayout);
        }
    }

    /**
     * The method can be used to customize the tool bar.
     * <p>
     * Example:<br/>
     * customizeToolbar(request, editContext, "edit,delete,-,text:toolbar text")<br/>
     * is equal to<br/>
     * cq:actions="[edit,delete,-,text:toolbar text]" in a _cq_editConfig.xml
     * </p>
     * <p>
     * After text: also a i18n key can be used.
     * </p>
     * 
     * @see com.day.cq.wcm.api.components.Toolbar.Item
     * @see com.day.cq.wcm.api.components.EditAction
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     * @param actions
     *            the actions
     */
    public static void customizeToolbar(final SlingHttpServletRequest request,
                                        final EditContext editContext,
                                        final String actions) {

        final Toolbar toolbar = getToolbar(request, editContext);
        if (null != toolbar && StringUtils.isNotBlank(actions)) {

            toolbar.clear();
            final String[] items = actions.split(",");

            for (int i = 0; i < items.length; i++) {
                final String item = items[i];

                if (EditAction.COPYMOVE.name().equalsIgnoreCase(item)) {
                    toolbar.add(EditAction.COPYMOVE);

                } else if (EditAction.DELETE.name().equalsIgnoreCase(item)) {
                    toolbar.add(EditAction.DELETE);

                } else if (EditAction.EDIT.name().equalsIgnoreCase(item)) {
                    toolbar.add(EditAction.EDIT);

                } else if (EditAction.EDITCOPYMOVEDELETEINSERT.name().equalsIgnoreCase(item)) {
                    toolbar.add(EditAction.EDITCOPYMOVEDELETEINSERT);

                } else if (EditAction.EDITDELETE.name().equalsIgnoreCase(item)) {
                    toolbar.add(EditAction.EDITDELETE);

                } else if (EditAction.EDITDELETEINSERT.name().equalsIgnoreCase(item)) {
                    toolbar.add(EditAction.EDITDELETEINSERT);

                } else if (EditAction.INSERT.name().equalsIgnoreCase(item)) {
                    toolbar.add(EditAction.INSERT);

                } else if (EditAction.COPYMOVE.name().equalsIgnoreCase(item)) {
                    toolbar.add(EditAction.COPYMOVE);

                } else if ("-".equalsIgnoreCase(item)) {
                    toolbar.add(new Toolbar.Separator());

                } else if (item.startsWith(TEXT)) {
                    final String label = item.substring(TEXT.length());
                    final String entry = Dictionary.getDictionary(request).getEntry(label);
                    if (label.equalsIgnoreCase(entry)) {
                        toolbar.add(new Toolbar.Label(label));

                    } else {
                        toolbar.add(new Toolbar.Label(entry));
                    }

                }
            }
        }
    }

    /**
     * The method can be used to customize the toolbar.
     * <p>
     * Example:<br/>
     * customizeToolbar(request, editContext, Object[]{EditAction.EDIT, EditAction.DELETE, new Toolbar.Separator(), new
     * Toolbar.Label("Toolbar Label")})
     * </p>
     * <p>
     * A String is interpreted as tool bar label.
     * </p>
     * 
     * @see com.day.cq.wcm.api.components.Toolbar.Item
     * @see com.day.cq.wcm.api.components.EditAction
     * @param request
     *            the request
     * @param editContext
     *            the edit context
     * @param items
     *            the items
     */
    public static void customizeToolbar(final SlingHttpServletRequest request,
                                        final EditContext editContext,
                                        final Object... items) {

        final Toolbar toolbar = getToolbar(request, editContext);
        if (null != toolbar) {

            toolbar.clear();

            for (int i = 0; i < items.length; i++) {
                final Object object = items[i];

                if (object instanceof EditAction) {
                    final EditAction editAction = (EditAction) object;
                    toolbar.add(editAction);

                } else if (object instanceof Toolbar.Label) {
                    final Toolbar.Label toolbarLabel = (Toolbar.Label) object;
                    toolbar.add(toolbarLabel);

                } else if (object instanceof String) {
                    final String toolbarLabel = (String) object;
                    if (StringUtils.isNotBlank(toolbarLabel)) {
                        toolbar.add(new Toolbar.Label(toolbarLabel));
                    }

                } else if (object instanceof Toolbar.Separator) {
                    final Toolbar.Separator toolbarSeparator = (Toolbar.Separator) object;
                    toolbar.add(toolbarSeparator);
                }
            }
        }
    }
}
