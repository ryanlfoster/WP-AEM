/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core;

import java.util.List;

import com.aditya.gmwp.aem.wrapper.GMResource;
import com.aditya.gmwp.aem.wrapper.LevelServiceWrapper;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface QueryService {

    /**
     * Find a single resource with a given property key and value, starting from the given rootPath.
     * 
     * @param propertyKey
     *            the propertyKey
     * @param propertyValue
     *            the propertyValue
     * @param rootPath
     *            the rootPath
     * @return the resource
     */
    GMResource findByKeyValue(final String propertyKey, final String propertyValue, final String rootPath);

    /**
     * Find a list of resources with a given key and value, starting from the given rootPath.
     * 
     * @param propertyKey
     *            the propertyKey
     * @param propertyValue
     *            the propertyValue
     * @param rootPath
     *            the rootPath
     * @return the resources
     */
    List<GMResource> findAllByKeyValue(final String propertyKey, final String propertyValue, final String rootPath);

    /**
     * Find a list of resources with a given key and multiple values, starting from the given
     * rootPath.
     * 
     * @param propertyKey
     *            the propertyKey
     * @param propertyValues
     *            the propertyValues
     * @param rootPath
     *            the rootPath
     * @return the resources
     */
    List<GMResource> findAllByMultipleKeyValues(final String propertyKey, final List<String> propertyValues, final String rootPath);

    /**
     * Find a single resource with a given name, starting from the given rootPath.
     * 
     * @param nodeName
     *            the nodeName
     * @param rootPath
     *            the rootPath
     * @return the resources
     */
    GMResource findByName(final String nodeName, final String rootPath);

    /**
     * Find all resources with a given name, starting from the given rootPath.
     * 
     * @param nodeName
     *            the nodeName
     * @param rootPath
     *            the rootPath
     * @return the resources
     */
    List<GMResource> findAllByName(final String nodeName, final String rootPath);

    /**
     * Find all by name under multiple paths.
     * 
     * @param nodeName
     *            the node name
     * @param paths
     *            the paths
     * @return the list
     */
    List<GMResource> findAllByNameUnderMultiplePaths(final String nodeName, final String... paths);

    /**
     * Find the resource of a configuration page relevant for the current path, by a given constant.
     * 
     * @param currentPath
     *            the currentPath
     * @param levelConstant
     *            the levelConstant
     * @return the resource
     */
    GMResource findConfigurationPage(final String currentPath, final LevelServiceWrapper.Constants levelConstant);
}
