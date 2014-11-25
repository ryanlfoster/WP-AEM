/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;



/**
 * This class provides helper methods to read or write data that is stored in CRX for persistent
 * caching. Please note that all methods use synchronized code and should thus only be used rarely
 * (e.g. load data during service-activation and store it during service-deactivation). Do not use
 * the methods in this class in code that is executed in each user-request! All data will be stored
 * in separate nodes below /var/wp/cache.
 * 
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class PersistentCacheUtil {

    private static final String CACHING_PATH = "var/wp/cache";

    private static final Logger LOG = LoggerFactory.getLogger(PersistentCacheUtil.class);

    private static final String MIMETYPE_JAVA_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    /**
     * Fetches the node in which cached data is stored from the CRX.
     * 
     * @param crxSession
     *            the crx session
     * @param createIfNotExistent
     *            whether nodes should be created when not existent
     * @return the node, may be null if not existent and "create"-flag is false.
     * @throws RepositoryException
     *             when fetching or creating nodes from/in the CRX fails
     */
    private static synchronized Node getCacheRootNode(final Session crxSession,
                                                      final boolean createIfNotExistent) throws RepositoryException {
        Node n = crxSession.getRootNode();
        final String[] nodeNames = CACHING_PATH.split("\\/");
        for (int i = 0; i < nodeNames.length; i++) {
            if (!n.hasNode(nodeNames[i])) {
                if (i == 0) {
                    throw new RepositoryException("1st-level node " + nodeNames[i] + " not found! Repository broken?");
                } else if (createIfNotExistent) {
                    n.addNode(nodeNames[i], JcrConstants.NT_FOLDER);
                    n.getSession().save();
                } else {
                    return null;
                }
            }
            n = n.getNode(nodeNames[i]);
        }
        return n;
    }

    /**
     * Load data.
     * 
     * @param cacheNodeName
     *            the name of the crx-node in which the data has been stored.
     * @param crxSession
     *            a session that can be used to access to crx.
     * @return An input-stream for the data of the property, or null if there is no data or the data
     *         cannot be accessed.
     */
    public static InputStream loadData(final String cacheNodeName,
                                       final Session crxSession) {
        InputStream in = null;
        try {
            Node cacheNode = getCacheRootNode(crxSession, false);
            if (null == cacheNode || !cacheNode.hasNode(cacheNodeName)) {
                return null;
            }
            cacheNode = cacheNode.getNode(cacheNodeName);
            if (cacheNode.hasNode(JcrConstants.JCR_CONTENT)) {
                cacheNode = cacheNode.getNode(JcrConstants.JCR_CONTENT);
                final Property prop = cacheNode.getProperty(JcrConstants.JCR_DATA);
                in = prop.getBinary().getStream();
            }
        } catch (PathNotFoundException e) {
            // that is ok.
            return null;
        } catch (RepositoryException e) {
            LOG.error("Unable to load cached data from " + CACHING_PATH + "/" + cacheNodeName + ". Cause: ", e);
            return null;
        }
        return in;
    }

    /**
     * De-serializes the binary content that has been stored in the cache-node with given name and
     * returns it as an object.
     * 
     * @param cacheNodeName
     *            the name of the crx node where the data has been stored previously.
     * @param crxSession
     *            a session to access the crx.
     * @return a de-serialized object or null if no data has been stored below the node with the
     *         given name or if de-serialization fails.
     * @see #persistObject(String, Serializable, Session)
     */
    public static Serializable loadPersistedObject(final String cacheNodeName,
                                                   final Session crxSession) {
        final InputStream in = loadData(cacheNodeName, crxSession);
        if (null != in) {
            try {
                final ObjectInputStream oin = new ObjectInputStream(in);
                try {
                    return (Serializable) oin.readObject();
                } finally {
                    oin.close();
                }
            } catch (IOException e) {
                LOG.error("Unable to de-serialize data from cache-node '" + cacheNodeName + "'. Cause: ", e);
            } catch (ClassNotFoundException e) {
                LOG.error("Unable to de-serialize data from cache-node '" + cacheNodeName + "'. Cause: ", e);
            }
        }
        return null;
    }

    /**
     * Persist data.
     * 
     * @param cacheNodeName
     *            the name of the crx node in which the data should be stored.
     * @param in
     *            an in-stream that delivers the data to be stored
     * @param dataMimeType
     *            The mime-type of the data that is delivered in the in-stream.
     * @param crxSession
     *            a session to access to crx
     */
    public static void persistData(final String cacheNodeName,
                                   final InputStream in,
                                   final String dataMimeType,
                                   final Session crxSession) {
        Node cacheNode = null;
        try {
            cacheNode = getCacheRootNode(crxSession, true);
            if (!cacheNode.hasNode(cacheNodeName)) {
                cacheNode = cacheNode.addNode(cacheNodeName, JcrConstants.NT_FILE);
                final Node contentNode = cacheNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
                contentNode.setProperty(JcrConstants.JCR_DATA, in);
                contentNode.setProperty(JcrConstants.JCR_MIMETYPE, dataMimeType);
                contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
                cacheNode.getParent().getSession().save();
            } else {
                cacheNode = cacheNode.getNode(cacheNodeName);
                cacheNode = cacheNode.getNode(JcrConstants.JCR_CONTENT);
                cacheNode.setProperty(JcrConstants.JCR_DATA, in);
                cacheNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
                cacheNode.getSession().save();
            }
        } catch (RepositoryException e) {
            LOG.error("Failed to persist data in " + CACHING_PATH + "/" + cacheNodeName + ". Cause: " + e);
            return;
        }
    }

    /**
     * Stores the given serializable object in the CRX. Please note that only one object can be
     * stored per <code>cacheNodeName</code>, calling this method several times for the same
     * <code>cacheNodeName</code> will override the previously stored data. In order to store
     * several objects, they have to be wrapped in a container-object (e.g. a list).
     * 
     * @param cacheNodeName
     *            the name of the crx node in which the data should be stored.
     * @param data
     *            a serializable object that contains the data to be written
     * @param crxSession
     *            a session to access to crx
     * @see #loadPersistedObject(String, Session)
     */
    public static void persistObject(final String cacheNodeName,
                                     final Serializable data,
                                     final Session crxSession) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream oout = new ObjectOutputStream(baos);
            try {
                oout.writeObject(data);
            } finally {
                oout.close();
            }
        } catch (IOException e) {
            LOG.error("Unable to convert the given serializable to a byte-array. Cause: ", e);
        }
        persistData(cacheNodeName, new ByteArrayInputStream(baos.toByteArray()), MIMETYPE_JAVA_SERIALIZED_OBJECT, crxSession);
    }

    /**
     * private constructor, all methods are static.
     */
    private PersistentCacheUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }

    /**
     * Delete given node from cache.
     * 
     * @param cacheNodeName
     *            the name of the crx node in which the data should be stored.
     * @param crxSession
     *            a session to access to crx
     * @see #deleteObject(String, Session)
     */
    public static void deleteObject(final String cacheNodeName,
                                    final Session crxSession) {

        Node cacheNode = null;

        try {
            cacheNode = getCacheRootNode(crxSession, true);

            if (cacheNode != null) {
                Node deleteCacheNode = cacheNode.getNode(cacheNodeName);
                if (deleteCacheNode != null) {
                    deleteCacheNode.remove();
                    cacheNode.getSession().save();
                }
            }
        } catch (RepositoryException e) {
            LOG.error("Error deleting the cache node");
        }

    }
}