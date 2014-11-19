/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.components;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.annotations.ComponentWithStandardModel;
import com.aditya.gmwp.aem.annotations.PageAreaComponent;
import com.aditya.gmwp.aem.annotations.SupportsExtendedOmnitureLinkTagging;
import com.aditya.gmwp.aem.aspects.SetExtendedOmnitureLinkTaggingValuesAspect;
import com.aditya.gmwp.aem.aspects.SetMaintainedLinkInFlashModelAspect;
import com.aditya.gmwp.aem.aspects.SetPageAreaNameAspect;
import com.aditya.gmwp.aem.aspects.UnsetPageAreaNameAspect;
import com.aditya.gmwp.aem.utils.components.EditContextUtil;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.EditAction;
import com.day.cq.wcm.api.components.EditConfig;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.components.EditLayout;
import com.day.cq.wcm.api.components.Toolbar;
import com.day.cq.wcm.commons.WCMUtils;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public abstract class AbstractComponent extends WCMUse {

    /**
     * Wraps all objects that are required to apply an aspect to a component.
     */
    private static final class ComponentAspectBean {

        /** The aspect class. */
        private final Class<? extends ComponentAspect> aspectClass;

        /** The component annotation. */
        private final Class<? extends Annotation> componentAnnotation;

        /** The invert logic. */
        private boolean invertLogic;

        /**
         * Instantiates a new component aspect bean.
         * 
         * @param componentAnnotation
         *            the annotation which is expected to be found on the component implementation class.
         * @param aspectClass
         *            the aspect implementation that has to be applied.
         */
        private ComponentAspectBean(final Class<? extends Annotation> componentAnnotation,
                final Class<? extends ComponentAspect> aspectClass) {
            this.componentAnnotation = componentAnnotation;
            this.aspectClass = aspectClass;
        }

        /**
         * Instantiates a new component aspect bean.
         * 
         * @param componentAnnotation
         *            the annotation which is expected to be found on the component implementation class.
         * @param aspectClass
         *            the aspect implementation that has to be applied.
         * @param invertLogic
         *            whether the logic should be inverted which means that the aspect will be applied if the annotation
         *            is NOT present on the implementing component class.
         */
        private ComponentAspectBean(final Class<? extends Annotation> componentAnnotation,
                final Class<? extends ComponentAspect> aspectClass, final boolean invertLogic) {
            this(componentAnnotation, aspectClass);
            this.invertLogic = invertLogic;
        }

        /**
         * Gets the aspect class.
         * 
         * @return the aspect to be applied.
         */
        Class<? extends ComponentAspect> getAspectClass() {
            return this.aspectClass;
        }

        /**
         * Gets the component annotation.
         * 
         * @return the annotation which tells that a certain aspect should be applied.
         */
        Class<? extends Annotation> getComponentAnnotation() {
            return this.componentAnnotation;
        }

        /**
         * Checks if is invert logic.
         * 
         * @return whether the logic should be inverted so that the aspect is applied if the component class is NOT
         *         annotated.
         */
        boolean isInvertLogic() {
            return this.invertLogic;
        }
    };

    /**
     * A list that contains all aspects that have to be applied after the component has been initialized.
     */
    private static final List<ComponentAspectBean> COMPONENT_ASPECTS;

    private static final String TRUE = "true";

    public static final String ATTR_ABSTRACT_COMPONENT_FORCE_EDIT_BAR = "abstractcomponent.forceEditBar";

    /** current logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractComponent.class);

    static {
        COMPONENT_ASPECTS = new ArrayList<ComponentAspectBean>();
        COMPONENT_ASPECTS.add(new ComponentAspectBean(SupportsExtendedOmnitureLinkTagging.class,
                SetExtendedOmnitureLinkTaggingValuesAspect.class));
        COMPONENT_ASPECTS.add(new ComponentAspectBean(ComponentWithStandardModel.class,
                SetMaintainedLinkInFlashModelAspect.class));
        COMPONENT_ASPECTS.add(new ComponentAspectBean(PageAreaComponent.class, SetPageAreaNameAspect.class));
        COMPONENT_ASPECTS.add(new ComponentAspectBean(PageAreaComponent.class, UnsetPageAreaNameAspect.class, true));
    }

    private EditContext editContext;
    
    private Node currentNode;

    /** The author mode. */
    private boolean authorMode;

    /** The is author instance. */
    private boolean isAuthorInstance;

    /**
     * The String from request witch describe if the component should be mandatory.
     */
    private String mandatoryComp;

    /*
	 * (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUse#activate()
	 */
	@Override
	public void activate() throws Exception {
        this.authorMode = WCMMode.fromRequest(getRequest()) == WCMMode.EDIT || WCMMode.fromRequest(getRequest()) == WCMMode.DESIGN;
        
        final ComponentContext cc = WCMUtils.getComponentContext(getRequest());
    	this.editContext = cc.getEditContext();
    	
    	this.currentNode = getResource().adaptTo(Node.class);

		final Object isAuthorInstanceObject = getRequest().getAttribute("isAuthorInstance");
        if (isAuthorInstanceObject != null) {
            this.isAuthorInstance = (Boolean) isAuthorInstanceObject;
        } else {
            this.isAuthorInstance = this.authorMode;
        }

		this.mandatoryComp = (String) getRequest().getAttribute("abstractcomponent.mandatory");
		getRequest().removeAttribute("abstractcomponent.mandatory");

		forceEditBar();
		applyAspects();
	}

	/**
	 * Gets the resource type associated with the component.
	 * @param component the component whose resource type is requested
	 * @return the resource type
	 */
	public static String getResourceType(final AbstractComponent component) {
		return (component != null) ? component.getResourceType() : StringUtils.EMPTY;
	}

    /**
     * The method forces an edit bar, if the corresponding request attribute was set.
     * <p>
     * This method is only for <b>backward compatibility</b> and should not longer be used.<br/>
     * Use the <code>&lt;gmds:editContext ... /&gt;</code> tag instead.
     * </p>
     */
    private void forceEditBar() {

        final String forceEditBar = (String) getRequest().getAttribute(
                ATTR_ABSTRACT_COMPONENT_FORCE_EDIT_BAR);
        if (forceEditBar != null) {
            if (forceEditBar.equals(TRUE)) {
                forceEditBar(true);
            }
            if ("true_noedit".equals(forceEditBar)) {
                forceEditBar(false);
            }
        }
        getRequest().removeAttribute(ATTR_ABSTRACT_COMPONENT_FORCE_EDIT_BAR);
    }

    /**
     * The method applies all aspects for which the class/type of "this" has annotations.
     */
    private void applyAspects() {

        for (final ComponentAspectBean cab : COMPONENT_ASPECTS) {
            final boolean aspectPresent = this.getClass().isAnnotationPresent(cab.getComponentAnnotation());
            if ((aspectPresent && !cab.isInvertLogic()) || (!aspectPresent && cab.isInvertLogic())) {
                try {
                    final ComponentAspect aspectImpl = cab.getAspectClass().newInstance();
                    aspectImpl.init(getRequest(), getCurrentPage(), this);
                    aspectImpl.applyAspect();
                } catch (final Exception e) {
                    LOG.error("Error while instatiating or applying aspect '" + cab.getComponentAnnotation().getName()
                            + "': ", e);
                }
            }
        }
    }

    /**
     * The method forces a toolbar.
     * <p>
     * This method is only for <b>backward compatibility</b> and should not longer be used.<br/>
     * Use {@link EditContextUtil#setEditLayout(SlingHttpServletRequest, EditContext, String)} and
     * {@link EditContextUtil#customizeToolbar(SlingHttpServletRequest, EditContext, String)} instead.
     * </p>
     * 
     * @param isEdit
     *            <code>true</code> if the toolbar should have a edit and a delete button, <code>false</code> otherwise
     */
    public final void forceEditBar(final boolean isEdit) {
        if (getEditContext() != null && WCMMode.fromRequest(getRequest()) == WCMMode.EDIT) {
            EditContextUtil.setEditLayout(getRequest(), getEditContext(), EditLayout.EDITBAR);
            if (isEdit) {
                EditContextUtil.customizeToolbar(getRequest(), getEditContext(), new Object[] { EditAction.EDIT, EditAction.DELETE });
            }
        }
    }

    /**
     * Gets the session.
     * 
     * @return the session
     */
    public final Session getSession() {
        Session session = null;
        try {
            session = getCurrentNode().getSession();
        } catch (final RepositoryException e) {
            LOG.error("Could not retrieve session of node " + getCurrentNode());
        }
        return session;
    }

    /**
     * Gets the current node.
     * 
     * @return the currentNode
     */
    public final Node getCurrentNode() {
        return this.currentNode;
    }

    /**
     * Gets the current resource.
     * 
     * @return the resource of the request.
     */
    public final GMResource getCurrentGMResource() {
        return new GMResource(getResource());
    }

    /**
     * Gets the edits the context.
     * 
     * @return the editContext
     */
    public final EditContext getEditContext() {
        return this.editContext;
    }

    /**
     * Gets the property for the given key as String Type.
     * 
     * @param key
     *            the key
     * @return ValueMap properties
     */
    public final String getPropertyAsString(final String key) {
        return getValue(key, String.class, null);
    }

    /**
     * Gets the property for the given key as String Type. If no valid property could be found, the default value is set
     * as the result.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the value for the given key or the defaultValue
     */
    public final String getPropertyAsString(final String key,
                                            final String defaultValue) {
        return getValue(key, String.class, defaultValue);
    }

    /**
     * Gets the property for the given key as Date Type.
     * 
     * @param key
     *            the key
     * @return ValueMap properties
     */
    public final Date getPropertyAsDate(final String key) {
    	return getValue(key, Date.class, null);
    }

    /**
     * Gets the property for the given key as date Type. If no valid property could be found, the default value is set
     * as the result.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the value for the given key or the defaultValue
     */
    public final Date getPropertyAsDate(final String key,
                                        final Date defaultValue) {
    	return getValue(key, Date.class, defaultValue);
    }

    /**
     * Gets the property for the given key as PRIMITIVE boolean type. It is <code>null</code> save and returns
     * <code>false</code>, if no value was found. Beside of "true" it also returns <code>true</code> for "yes" and "on".
     * 
     * @param key
     *            the String key to check
     * @return <code>true</code> if the String can be interpreted as true
     */
    public final boolean getPropertyAsBoolean(final String key) {
        return getValue(key, Boolean.class, Boolean.FALSE);
    }

    /**
     * Gets the property as boolean.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the property as boolean
     */
    public final boolean getPropertyAsBoolean(final String key,
                                              final boolean defaultValue) {
    	return getValue(key, Boolean.class, defaultValue);
    }

    /**
     * Gets the property for the given key as Integer Type. If no valid property could be found, the default value is
     * set as the result.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the property as int
     */
    public final Integer getPropertyAsInteger(final String key,
                                              final Integer defaultValue) {
    	return getValue(key, Integer.class, defaultValue);
    }

    /**
     * Gets the property for the given key as Integer type.
     * 
     * @param key
     *            the String key to check
     * @return the Integer if key can be interpreted as Integer, null otherwise
     */
    public final Integer getPropertyAsInteger(final String key) {
        return getValue(key, Integer.class, null);
    }

    /**
     * Gets the property for the given key as in primitive. If no valid property could be found, the default value is
     * set as the result.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the property as int
     */
    public final int getPropertyAsInt(final String key,
                                      final int defaultValue) {
        return getPropertyAsInteger(key, defaultValue);
    }

    /**
     * Gets the property for the given key as int primitive.
     * 
     * @param key
     *            the String key to check
     * @return the int prinitive if key can be interpreted as Integer, 0 otherwise
     */
    public final int getPropertyAsInt(final String key) {
        return getPropertyAsInt(key, 0);
    }

    /**
     * In every concrete component, use this method to calculate your attributes.<br />
     * Use this method to fetch node-properties and to assign them to your attributes.<br />
     * Avoid to perform these calculations in the constructor. <br />
     * <br />
     * Please note that the init-method will be called by the constructor of the AbstractComponent class. No child class
     * should call it itself in order to avoid multiple initializations.
     */
    public abstract void init();

    /**
     * In every concrete component, use this method to get the resource type associated.
     * @return the resource type of the component.
     */
    public abstract String getResourceType();

    /**
     * Some Components e.g. the Meta Navigation set the WCMMode to disabled, to prevent users from editing these
     * components. In these cases the isAuthorMode method will return false. If you want to know if this is the author
     * instance, but the WMMode was temporarily set to disabled, use this method.
     * 
     * @return "true" if this is the Author CQ instance, even if the WCMMode is set to disabled. "false" otherwise.
     */
    public final boolean isAuthorInstance() {
        return this.isAuthorInstance;
    }

    /**
     * Checks if this CQ instance is running in author mode. If you are on a author system and yet this method returns
     * false, the WCMMode may be temporarily set to disabled in order to prevent the user from editing certain
     * components (e.g. Meta Navigation). Use the isAuthorInstance() method instead.
     * 
     * @return true, if is the editor or author mode, otherwise return false
     */
    public final boolean isAuthorMode() {
        return this.authorMode;
    }

    /**
     * Gets the value associated with the jcr property key.
     * @param key the jcr property name
     * @param type the type of value to cast to
     * @param defaultValue the default value when property not found or unable to be cast
     * @return the value associated with the jcr property.
     */
    private <T> T getValue(final String key, final Class<T> type, final T defaultValue) {
    	final T data = get(key, type);
    	if (data == null) {
    		return defaultValue;
    	}

    	return data;
    }

    /**
     * Checks if this component is maintained as a sub component of a specified parent component or if it is generally a
     * sub component of any parent component.
     * 
     * @param resourceTypeOfParentComponent
     *            sling:resourceType of the parent component. Null if parent component could be any component.
     * @return true, if it is a sub component
     */
    public final boolean isSubComponentOf(final String resourceTypeOfParentComponent) {
        // search for the parent component (if there is any) until the page node
        // is reached.
        Node node = this.currentNode;
        try {
            while (node != null) {
                final Node parentNode = node.getParent();
                final Property resourceType = parentNode.getProperty("sling:resourceType");
                if (resourceType != null) {
                    final String resourceTypeString = resourceType.getString();
                    if (resourceTypeString != null) {
                        if (resourceTypeString.startsWith("gmds/pages/")) {
                            // there is no parent component
                            return false;
                        } else if (resourceTypeString.startsWith("gmds/components/")
                                && resourceTypeOfParentComponent == null) {
                            // found any parent component
                            return true;
                        } else if (resourceTypeString.equals(resourceTypeOfParentComponent)) {
                            // found the defined parent component
                            return true;
                        }
                    }
                }
                node = parentNode;
            }
        } catch (final PathNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (final RepositoryException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Prints the given error message (only in author mode).
     * 
     * @param errorMessage
     *            the error message to print
     */
    public final void printErrorMessage(final String errorMessage) {
        if (isAuthorMode()) {
            final String htmlErrorMessage = "<div style=\"border:1px solid "
                    + "#FF0000; line-height:1.5em; text-align:center; color: #ff0000;\">" + errorMessage + "</div>";
            try {
                getResponse().getOutputStream().print(htmlErrorMessage);
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Prints the given error message (only in author mode). If the request contains the parameter
     * <code>showStacktrace</code> with the vale <code>true</code> additionally the stack trace is printed out.
     * 
     * @param error
     *            the error to print
     */
    public final void printErrorMessage(final Throwable error) {
        if (error != null && isAuthorMode()) {
            try {
            	getResponse().getOutputStream().print("<div style=\"border:1px solid #FF0000; line-height:1.5em; text-align:center; color: #ff0000;\">"
                        + error.toString() + "</div>");
                if (TRUE.equals(getRequest().getParameter("showStacktrace"))) {
                	getResponse().getOutputStream().print("<pre style=\"line-height:1.5em; text-align:left; font-size:1.3em;\">");
                    error.printStackTrace(new PrintWriter(getResponse().getOutputStream()));
                    getResponse().getOutputStream().print("</pre>");
                }
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * The method sets or overwrites the editbar text.
     * <p>
     * Normally the editbar label should be configured in the <b>_cq_editConfig.xml</b>
     * (<b>cq:actions="[edit,delete,-,text:&lt;label&gt;]"</b> ).<br/>
     * To overwrite the configured label <b><code>&lt;gmds:editContext toolbarText="&lt;text&gt;" /&gt;</code></b> or
     * {@link com.gm.gssm.gmds.cq.utils.components.EditContextUtil#addOrOverwriteFirstToolbarLabel(SlingHttpServletRequest, EditContext, String)}
     * can be used.
     * </p>
     * 
     * @param key
     *            String of the key from the language file
     */
    protected final void setEditbarLabel(final String key) {
        if (getEditContext() != null && WCMMode.fromRequest(getRequest()) == WCMMode.EDIT) {
            final EditConfig editConfig = getEditContext().getEditConfig();
            if (editConfig.getLayout() == EditLayout.EDITBAR) {
                editConfig.getToolbar().add(new Toolbar.Label(key));

            }
            if (this.mandatoryComp != null && this.mandatoryComp.equals(TRUE)) {
                editConfig.getToolbar().add(new Toolbar.Label("mandatory_label"));
            }
        }
    }
}
