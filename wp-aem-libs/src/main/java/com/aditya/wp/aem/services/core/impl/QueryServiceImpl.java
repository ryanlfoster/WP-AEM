/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.util.ISO9075;

import com.aditya.gmwp.aem.services.config.LevelService;
import com.aditya.gmwp.aem.services.core.AbstractService;
import com.aditya.gmwp.aem.services.core.JcrService;
import com.aditya.gmwp.aem.services.core.QueryService;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.aditya.gmwp.aem.wrapper.LevelServiceWrapper;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;



/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Component(name = "com.aditya.gmwp.aem.services.core.QueryService", label = "GMWP Query Service", enabled = true, metatype = false)
@Service(QueryService.class)
@Properties({ @Property(name = "service.description", value = "QueryService Implementation") })
public class QueryServiceImpl extends AbstractService<QueryServiceImpl> implements QueryService {

    private static final String PATH = "path";
    private static final String PROPERTY = "property";
    private static final String PROPERTY_VALUE = "property.value";
    private static final String VALUE = "value";
    private static final String DOT = ".";
    private static final String UNDERSCORE = "_";
    private static final String NODE_NAME = "nodename";
    
    private static final String MULTIPLE_RESULTS_WARNING = "Expected single result, multiple results obtained instead for: ";

    @Reference
    private final JcrService jcrService = null;

    @Reference
    private final LevelService levelService = null;

    @Reference
    private QueryBuilder queryBuilder;

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.core.QueryService#findAllByKeyValue(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public final List<GMResource> findAllByKeyValue(final String propertyKey,
                                                    final String propertyValue,
                                                    final String rootPath) {

        final Map<String, String> map = new HashMap<String, String>();
        map.put(PATH, rootPath);
        map.put(PROPERTY, propertyKey);
        map.put(PROPERTY_VALUE, propertyValue);

        final Query query = this.queryBuilder.createQuery(PredicateGroup.create(map), this.jcrService.getAdminSession());
        query.setHitsPerPage(0);

        return createResourcesList(query.getResult().getHits());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.core.QueryService#findAllByMultipleKeyValues(java.lang.String,
     * java.util.List, java.lang.String)
     */

    @Override
    public final List<GMResource> findAllByMultipleKeyValues(final String propertyKey,
                                                             final List<String> propertyValues,
                                                             final String rootPath) {

        final Map<String, String> map = new HashMap<String, String>();
        map.put(PATH, rootPath);
        map.put(PROPERTY, propertyKey);

        int i = 1;
        for (String propertyValue : propertyValues) {
            map.put(PROPERTY + DOT + i + UNDERSCORE + VALUE, propertyValue);
            i++;
        }

        final Query query = this.queryBuilder.createQuery(PredicateGroup.create(map), this.jcrService.getAdminSession());
        query.setHitsPerPage(0);

        return createResourcesList(query.getResult().getHits());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.core.QueryService#findByKeyValue(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public final GMResource findByKeyValue(final String propertyKey,
                                           final String propertyValue,
                                           final String rootPath) {

        final Map<String, String> map = new HashMap<String, String>();
        map.put(PATH, rootPath);
        map.put(PROPERTY, propertyKey);
        map.put(PROPERTY_VALUE, propertyValue);

        final Query query = this.queryBuilder.createQuery(PredicateGroup.create(map), this.jcrService.getAdminSession());

        final List<Hit> hits = query.getResult().getHits();

        if (hits.size() == 0) {
            return GMResource.emptyGMResource();
        }

        if (hits.size() > 1) {
            getLog(this).warn(MULTIPLE_RESULTS_WARNING + map);
        }

        final Hit result = hits.get(0);

        try {
            return new GMResource(result.getResource());
        } catch (final RepositoryException e) {
            getLog(this).error(e.getMessage(), e);
        }

        return GMResource.emptyGMResource();
    }

    /**
     * Search by name and path.
     * 
     * @param nodeName
     *            the nodeName
     * @param rootPath
     *            the rootPath
     * @return hits
     */
    private List<Hit> findByNameQuery(final String nodeName,
                                      final String rootPath) {
        final Map<String, String> map = new HashMap<String, String>();
        map.put(PATH, rootPath);
        map.put(NODE_NAME, ISO9075.encode(nodeName));

        final Query query = this.queryBuilder.createQuery(PredicateGroup.create(map), this.jcrService.getAdminSession());
        query.setHitsPerPage(0);

        final SearchResult result = query.getResult();
        return result.getHits();
    }

    /**
     * Find resources with a given name by searching under multiple paths.
     * 
     * @param nodeName
     *            the node name
     * @param paths
     *            the paths
     * @return the list
     */
    private List<Hit> findByNameQueryAndPaths(final String nodeName,
                                              final String[] paths) {

        if (paths == null) {
            return new ArrayList<Hit>();
        }

        final Map<String, String> map = new HashMap<String, String>();
        map.put(NODE_NAME, ISO9075.encode(nodeName));
        map.put("group.p.or", "true");

        for (int i = 0; i < paths.length; i++) {
            map.put("group." + i + "_" + PATH, paths[i]);
        }

        final Query query = this.queryBuilder.createQuery(PredicateGroup.create(map), this.jcrService.getAdminSession());
        query.setHitsPerPage(0);

        final SearchResult result = query.getResult();
        return result.getHits();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.core.QueryService#findByName(java.lang.String,
     * java.lang.String)
     */
    @Override
    public final GMResource findByName(final String nodeName,
                                       final String rootPath) {
        final List<Hit> hits = findByNameQuery(nodeName, rootPath);

        if (hits.size() == 0) {
            return GMResource.emptyGMResource();
        }

        if (hits.size() > 1) {
            getLog(this).warn(MULTIPLE_RESULTS_WARNING + rootPath + ", " + nodeName);
        }

        final Hit result = hits.get(0);
        try {
            return new GMResource(result.getResource());
        } catch (final RepositoryException e) {
            getLog(this).error(e.getMessage(), e);
        }

        return GMResource.emptyGMResource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.core.QueryService#findAllByName(java.lang.String,
     * java.lang.String)
     */
    @Override
    public final List<GMResource> findAllByName(final String nodeName,
                                                final String rootPath) {
        return createResourcesList(findByNameQuery(nodeName, rootPath));
    }

    /**
     * Converts a list of hits into a list of resources
     * 
     * @param hits
     * @return
     */
    private List<GMResource> createResourcesList(final List<Hit> hits) {
        final List<GMResource> resources = new ArrayList<GMResource>();

        for (final Hit hit : hits) {
            try {
                resources.add(new GMResource(hit.getResource()));
            } catch (final RepositoryException e) {
                getLog(this).error(e.getMessage(), e);
            }
        }

        return resources;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.core.QueryService#findAllByNameUnderMultiplePaths(java.lang.
     * String, java.lang.String[])
     */
    @Override
    public final List<GMResource> findAllByNameUnderMultiplePaths(final String nodeName,
                                                                  final String... paths) {
        return createResourcesList(findByNameQueryAndPaths(nodeName, paths));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.core.QueryService#findConfigurationPage(java.lang.String,
     * com.gm.gssm.gmds.cq.utils.LevelServiceWrapper.Constants)
     */
    @Override
    public final GMResource findConfigurationPage(final String currentPath, final LevelServiceWrapper.Constants levelConstant) {
        final LevelServiceWrapper levelServiceWrapper = new LevelServiceWrapper(this.jcrService.getResourceResolver(), this.levelService);
        final GMResource configurationPage = new GMResource(levelServiceWrapper.getRelevantConfigurationPageByType(levelConstant, currentPath));

        return configurationPage;
    }
}
