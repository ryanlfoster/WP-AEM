/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the
 * NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aditya.gmwp.aem.wrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

/**
 * wrapper for resolving deep property.
 */
public class DeepResolvingValueMap implements ValueMap {

    private final ValueMap base;

    private final Map<String, Resource> cache = new HashMap<String, Resource>();

    private final Resource resource;

    /**
     * @param resource
     *            _
     * @param base
     *            _
     */
    DeepResolvingValueMap(final Resource resource, final ValueMap base) {
        this.base = base;
        this.resource = resource;
    }

    /**
     * {@inheritDoc}
     */
    public final void clear() {
        this.base.clear();
    }

    /**
     * {@inheritDoc}
     */
    public final boolean containsKey(final Object key) {
        return this.base.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean containsValue(final Object value) {
        return this.base.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    public final Set<Entry<String, Object>> entrySet() {
        return this.base.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    public final Object get(final Object key) {
        return this.base.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public final <T> T get(final String name,
                           final Class<T> type) {
        final int index = name.lastIndexOf('/');
        if (index == -1) {
            return this.base.get(name, type);
        }
        final String path = this.resource.getPath() + '/' + name.substring(0, index);
        return ResourceUtil.getValueMap(getResource(path)).get(name.substring(index + 1), type);
    }

    /**
     * {@inheritDoc}
     */
    public final <T> T get(final String name,
                           final T defaultValue) {
        final int index = name.lastIndexOf('/');
        if (index == -1) {
            return this.base.get(name, defaultValue);
        }
        final String path = this.resource.getPath() + '/' + name.substring(0, index);
        return ResourceUtil.getValueMap(getResource(path)).get(name.substring(index + 1), defaultValue);
    }

    /**
     * @param path
     *            _
     * @return _
     */
    private Resource getResource(final String path) {
        Resource rsrc = this.cache.get(path);
        if (rsrc == null) {
            rsrc = this.resource.getResourceResolver().getResource(path);
            if (rsrc != null) {
                this.cache.put(path, rsrc);
            }
        }
        return rsrc;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isEmpty() {
        return this.base.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public final Set<String> keySet() {
        return this.base.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public final Object put(final String key,
                            final Object value) {
        return this.base.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public final void putAll(final Map<? extends String, ?> t) {
        this.base.putAll(t);
    }

    /**
     * {@inheritDoc}
     */
    public final Object remove(final Object key) {
        return this.base.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public final int size() {
        return this.base.size();
    }

    /**
     * {@inheritDoc}
     */
    public final Collection<Object> values() {
        return this.base.values();
    }
}