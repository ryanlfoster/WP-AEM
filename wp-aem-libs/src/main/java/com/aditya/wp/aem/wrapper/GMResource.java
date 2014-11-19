/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.wrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class GMResource {

    private static final String NON_EXISTING_RESOURCE = "Resource is not existing";

    private static final Logger LOG = LoggerFactory.getLogger(GMResource.class);

    private final Resource resource;

    private Map<String, String> propMapCache = null;

    /**
     * Instantiates a new gM resource.
     * 
     * @param resource
     *            the resource
     */
    public GMResource(final Resource resource) {
        this.resource = resource;
    }

    /**
     * Give back an empty {@link GMResource} object, to avoid giving back null references.
     * 
     * @return the gM resource
     */
    public static GMResource emptyGMResource() {
        return new GMResource(null);
    }

    /**
     * Gets the resource.
     * 
     * @return the resource
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * To node.
     * 
     * @return the node
     */
    public Node toNode() {
        if (isExisting()) {
            return this.resource.adaptTo(Node.class);
        } else {
            return null;
        }

    }

    /**
     * To node.
     * 
     * @return the node
     */
    public GMNode toGMNode() {
        return new GMNode(toNode());
    }

    /**
     * The method searches for resources of a defined sling resource type below a defined resource. the current resource
     * 
     * @param wantedResourceType
     *            the wanted sling resource type
     * @return a list of resources of the wanted sling resouce type
     */
    public List<GMResource> findSubResourcesByType(final String wantedResourceType) {
        final List<GMResource> result = new ArrayList<GMResource>();
        if (isExisting()) {
            for (final GMResource actualResource : getChildren()) {
                if (wantedResourceType.equalsIgnoreCase(actualResource.getResourceType())) {
                    result.add(actualResource);
                }
                result.addAll(actualResource.findSubResourcesByType(wantedResourceType));
            }
        }
        return result;
    }

    /**
     * The method searches for resources of a defined array of sling resource types below a defined resource.
     * 
     * @param wantedResourceTypes
     *            the wanted sling resource types
     * @return a list of resources of the wanted sling resouce type
     */
    public List<GMResource> findSubResourcesByTypes(final String[] wantedResourceTypes) {

        final List<GMResource> resultResourceList = new ArrayList<GMResource>();
        if (isExisting()) {
            for (final GMResource actualResource : getChildren()) {
                for (final String wantedSlingResourceType : wantedResourceTypes) {
                    if (wantedSlingResourceType.equalsIgnoreCase(actualResource.getResourceType())) {
                        resultResourceList.add(actualResource);
                        break;
                    }
                }
                resultResourceList.addAll(actualResource.findSubResourcesByTypes(wantedResourceTypes));
            }
        }

        return resultResourceList;
    }

    /**
     * Checks if the given resource is of a certain type.
     * 
     * @param type
     *            The Type (a Java String, representing the sling:resourceType)
     * @return true if the sling:resourceType is a paragraph system or subtype, false otherwise.
     */
    public boolean isOfType(final String type) {
        return isExisting() && StringUtils.isNotBlank(type)
                && (type.equals(this.resource.getResourceType()) || type.equals(this.resource.getResourceSuperType()));
    }

    /**
     * Reads the adressed content-element from the content.
     * 
     * @param <T>
     *            the class
     * @param qualident
     *            the atom's cqualident
     * @param contentClass
     *            the atom's content-class
     * @return the requested value from the content
     */
    public <T> T getValue(final String qualident,
                          final Class<T> contentClass) {
        final ValueMap properties = getDeepResolvingValueMap();

        try {
            return properties.get(qualident, contentClass);
        } catch (final Exception e) {
            final String msg = "Error occured: Qualident '" + qualident + "' does not exist in '"
                    + this.resource.getPath() + "'.";
            LOG.error(msg);
            return null;
        }

    }

    /**
     * Gets the value map.
     * 
     * @return the value map
     */
    private ValueMap getDeepResolvingValueMap() {
        if (isNotExisting()) {
            return new ValueMapDecorator(new HashMap<String, Object>());
        } else {
            return new DeepResolvingValueMap(this.resource, ResourceUtil.getValueMap(this.resource));
        }
    }

    /**
     * Returns <code>true</code> if the resource is an existing resource.
     * 
     * @return <code>true</code> if <code>res</code> is to be considered an existing resource.
     */
    public boolean isExisting() {
        return this.resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(this.resource.getResourceType());
    }

    /**
     * Returns <code>true</code> if the resource is a not existing resource.
     * 
     * @return <code>true</code> if <code>res</code> is to be considered not an existing resource.
     */
    public boolean isNotExisting() {
        return !isExisting();
    }

    /**
     * List the children of the type {@link GMResource} as iterator.
     * 
     * @return the iterator
     */
    public Iterator<GMResource> listGMChildren() {
        return getChildren().iterator();
    }

    /**
     * Gets the children of type {@link GMResource} in a {@link List}.
     * 
     * @return the children
     */
    public List<GMResource> getChildren() {
        final List<GMResource> children = new ArrayList<GMResource>();
        if (isExisting()) {
            for (final Iterator<Resource> iterator = this.resource.listChildren(); iterator.hasNext();) {
                children.add(new GMResource(iterator.next()));
            }
        }
        return children;
    }

    /**
     * Adapt to.
     * 
     * @param <AdapterType>
     *            the generic type
     * @param arg0
     *            the arg0
     * @return the adapter type
     */
    public <AdapterType> AdapterType adaptTo(final Class<AdapterType> arg0) {
        if (isExisting()) {
            return this.resource.adaptTo(arg0);
        } else {
            return null;
        }

    }

    /**
     * Gets the child.
     * 
     * @param arg0
     *            the arg0
     * @return the child
     */
    public GMResource getChild(final String arg0) {
        if (isExisting()) {
            return new GMResource(this.resource.getChild(arg0));
        } else {
            return null;
        }
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        if (isExisting()) {
            return this.resource.getName();
        } else {
            return StringUtils.EMPTY;
        }

    }

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    public GMResource getParent() {
        if (isExisting()) {
            return new GMResource(this.resource.getParent());
        } else {
            return null;
        }
    }

    /**
     * Gets the path.
     * 
     * @return the path
     */
    public String getPath() {
        if (isExisting()) {
            return this.resource.getPath();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Gets the resource metadata.
     * 
     * @return the resource metadata
     */
    public ResourceMetadata getResourceMetadata() {
        if (isExisting()) {
            return this.resource.getResourceMetadata();
        } else {
            return null;
        }
    }

    /**
     * Gets the resource resolver.
     * 
     * @return the resource resolver
     */
    public ResourceResolver getResourceResolver() {
        if (isExisting()) {
            return this.resource.getResourceResolver();
        } else {
            return null;
        }
    }

    /**
     * Gets the resource super type.
     * 
     * @return the resource super type
     */
    public String getResourceSuperType() {
        if (isExisting()) {
            return this.resource.getResourceSuperType();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Gets the resource type.
     * 
     * @return the resource type
     */
    public String getResourceType() {
        if (isExisting()) {
            return this.resource.getResourceType();
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Checks if is resource type.
     * 
     * @param arg0
     *            the arg0
     * @return true, if is resource type
     */
    public boolean isResourceType(final String arg0) {
        if (isExisting()) {
            return this.resource.isResourceType(arg0);
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * List children.
     * 
     * @return the iterator
     */
    public Iterator<GMResource> listChildren() {
        return listGMChildren();
    }

    /**
     * Gets a property of the resource as String.
     * 
     * @param key
     *            the property key
     * @return the property value as String
     */
    public String getPropertyAsString(final String key) {
        return getPropertyAsString(key, null);
    }

    /**
     * Gets a property of the resource as String.
     * 
     * @param key
     *            the property key
     * @param defaultValue
     *            the defaultValue
     * @return the property value as String, defaultValue if the property does not exist
     */
    public String getPropertyAsString(final String key,
                                      final String defaultValue) {
        if (isNotExisting()) {
            return defaultValue;
        }
        final ValueMap props = this.resource.adaptTo(ValueMap.class);
        if (props == null) {
            return defaultValue;
        }
        return props.get(key, defaultValue);
    }

    /**
     * Gets a property of the resource as Date.
     * 
     * @param key
     *            the property key
     * @return the property value as Date
     */
    public Date getPropertyAsDate(final String key) {
        return getPropertyAsDate(key, null);
    }

    /**
     * Gets a property of the resource as Date.
     * 
     * @param key
     *            the property key
     * @param defaultValue
     *            the defaultValue
     * @return the property value as Date, defaultValue if the property does not exist
     */
    public Date getPropertyAsDate(final String key,
                                  final Date defaultValue) {
        if (isNotExisting()) {
            return defaultValue;
        }
        final ValueMap props = this.resource.adaptTo(ValueMap.class);
        if (props == null) {
            return defaultValue;
        }
        return props.get(key, defaultValue);
    }

    /**
     * Gets a property of the resource as String.
     * 
     * @param key
     *            the property key
     * @return the property value as String
     */
    public Boolean getPropertyAsBoolean(final String key) {
        return getPropertyAsBoolean(key, Boolean.FALSE);
    }

    /**
     * Gets a property of the resource as String.
     * 
     * @param key
     *            the property key
     * @param defaultValue
     *            the defaultValue
     * @return the property value as String, defaultValue if the property does not exist
     */
    public Boolean getPropertyAsBoolean(final String key,
                                        final Boolean defaultValue) {
        if (isNotExisting()) {
            return defaultValue;
        }
        final ValueMap props = this.resource.adaptTo(ValueMap.class);
        if (props == null) {
            return defaultValue;
        }
        return props.get(key, defaultValue);
    }

    /**
     * Get a property of the resource as Integer.
     * 
     * @param key
     *            the property key
     * @return the property value as Integer
     */
    public Integer getPropertyAsInt(final String key) {
        return getPropertyAsInt(key, null);
    }

    /**
     * Gets a property of the resource as Integer.
     * 
     * @param key
     *            the property key
     * @param defaultValue
     *            the defaultValue
     * @return the property value as Integer, defaultValue if the property does not exist
     */
    public Integer getPropertyAsInt(final String key,
                                    final Integer defaultValue) {
        if (isNotExisting()) {
            return defaultValue;
        }
        final ValueMap props = this.resource.adaptTo(ValueMap.class);
        if (props == null) {
            return defaultValue;
        }
        return props.get(key, defaultValue);
    }

    /**
     * Get a property of the resource as Integer.
     * 
     * @param key
     *            the property key
     * @return the property value as Integer
     */
    public Long getPropertyAsLong(final String key) {
        return getPropertyAsLong(key, null);
    }

    /**
     * Gets a property of the resource as Integer.
     * 
     * @param key
     *            the property key
     * @param defaultValue
     *            the defaultValue
     * @return the property value as Integer, defaultValue if the property does not exist
     */
    public Long getPropertyAsLong(final String key,
                                  final Long defaultValue) {
        if (isNotExisting()) {
            return defaultValue;
        }
        final ValueMap props = this.resource.adaptTo(ValueMap.class);
        if (props == null) {
            return defaultValue;
        }
        return props.get(key, defaultValue);
    }

    /**
     * Gets the property as double.
     * 
     * @param key
     *            the key
     * @return the property as double
     */
    public Double getPropertyAsDouble(final String key) {
        return getPropertyAsDouble(key, null);
    }

    /**
     * Gets the property as double.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the property as double
     */
    public Double getPropertyAsDouble(final String key,
                                      final Double defaultValue) {
        if (isNotExisting()) {
            return defaultValue;
        }
        final ValueMap props = this.resource.adaptTo(ValueMap.class);
        if (props == null) {
            return defaultValue;
        }
        return props.get(key, defaultValue);
    }

    /**
     * Gets the property as string array.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the property as string array
     */
    public String[] getPropertyAsStringArray(final String key,
                                             final String[] defaultValue) {
        if (isNotExisting()) {
            return defaultValue;
        }
        final ValueMap props = this.resource.adaptTo(ValueMap.class);
        if (props == null) {
            return defaultValue;
        }
        return props.get(key, defaultValue);
    }

    /**
     * Gets the property as string array.
     * 
     * @param key
     *            the key
     * @return the property as string array
     */
    public String[] getPropertyAsStringArray(final String key) {
        return getPropertyAsStringArray(key, new String[0]);
    }

    /**
     * Gets the containing page of the underlying resource.
     * 
     * @return the containing page
     */
    public Page getContainingPage() {
        if (isExisting()) {
            final PageManager man = getPageManager();
            if (man != null) {
                return man.getContainingPage(this.resource);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Gets the page manager.
     * 
     * @return the page manager
     */
    private PageManager getPageManager() {
        if (isExisting()) {
            return getResourceResolver().adaptTo(PageManager.class);
        } else {
            return null;
        }

    }

    /**
     * To externalizer.
     * 
     * @return the externalizer
     */
    public Externalizer toExternalizer() {
        if (isExisting()) {
            return getResourceResolver().adaptTo(Externalizer.class);
        } else {
            return null;
        }

    }

    /**
     * Gets the full external url.
     * 
     * @param request
     *            current request
     * @return the full external url
     */
    public String getFullExternalUrl(final SlingHttpServletRequest request) {
        if (isExisting()) {
            final String path = getContainingPage().getPath();
            return toExternalizer().absoluteLink(request, request.getScheme(), path);
        } else {
            return StringUtils.EMPTY;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (isExisting()) {
            return getPath();
        } else {
            return NON_EXISTING_RESOURCE;
        }
    }

    /**
     * Return the properties of the resource as a map of (property name, property)-pairs. Only single values get
     * returned.
     * 
     * @return the property map
     */
    public Map<String, String> getProp() {
        if (this.propMapCache != null) {
            return this.propMapCache;
        }
        this.propMapCache = toGMNode().getProp();

        return this.propMapCache;
    }
}
