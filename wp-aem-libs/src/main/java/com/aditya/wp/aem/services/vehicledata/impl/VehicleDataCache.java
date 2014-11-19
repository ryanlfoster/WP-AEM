/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.vehicledata.data.VehicleData;
import com.aditya.gmwp.aem.utils.PersistentCacheUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
final class VehicleDataCache {

    /**
     * A wrapper object for caching VehicleData. Stores the actual data and some meta-information for cache-management.
     */
    static class CachedVehicleData implements Serializable {

        /**
         * UID for serialization.
         */
        private static final long serialVersionUID = 2L;

        /**
         * The number of uses of this cached data, gets incremented when <code>use()</code> is called.
         */
        private int hits = 0;

        /**
         * The time-stamp of the last usage of this data, gets updated when <code>use()</code> is called.
         */
        private long lastAccessed = 0;

        /**
         * The time-stamp of the last failed update of the data, gets set when <code>updateFailed()</code> is called.
         */
        private long lastFailedUpdate = -1;

        /**
         * A read-write-lock for internal synchronization of all meta-data members.
         */
        private final ReentrantReadWriteLock metaDataLock = new ReentrantReadWriteLock();

        /**
         * When the stored vehicle-data has been updated (or initially created).
         */
        private long updateTime;

        /**
         * The stored data.
         */
        private VehicleData vehicleData;

        /**
         * A read-write-lock for internal synchronization of the vehicleData member.
         */
        private final ReentrantReadWriteLock vehicleDataLock = new ReentrantReadWriteLock();

        /**
         * Creates a new instance.
         * 
         * @param vehicleData
         *            the data to be stored.
         */
        CachedVehicleData(final VehicleData vehicleData) {

            this.vehicleDataLock.writeLock().lock();
            try {
                this.vehicleData = vehicleData;
            } finally {
                this.vehicleDataLock.writeLock().unlock();
            }

            this.metaDataLock.writeLock().lock();
            try {
                this.updateTime = System.currentTimeMillis();
            } finally {
                this.metaDataLock.writeLock().unlock();
            }
        }

        /**
         * Returns the vehicle data.
         * 
         * @return the stored vehicle data.
         */
        VehicleData getVehicleData() {
            VehicleData vd = null;
            this.vehicleDataLock.readLock().lock();
            try {
                vd = this.vehicleData;
            } finally {
                this.vehicleDataLock.readLock().unlock();
            }
            return vd;
        }

        /**
         * Invalidates the cached data by setting the update-time to "long ago", but keeps the old data until it is
         * actually updated.
         */
        void invalidate() {
            this.metaDataLock.writeLock().lock();
            try {
                this.updateTime = Long.MIN_VALUE;
            } finally {
                this.metaDataLock.writeLock().unlock();
            }
        }

        /**
         * Returns whether vehicle data is outdated.
         * 
         * @param maxAgeMillis
         *            the maximal age of the data in milliseconds.
         * @return whether the stored data is out-dated.
         */
        boolean isOutDated(final long maxAgeMillis) {
            boolean isOutDated = false;
            this.metaDataLock.readLock().lock();
            try {
                isOutDated = this.updateTime + maxAgeMillis < System.currentTimeMillis();
            } finally {
                this.metaDataLock.readLock().unlock();
            }
            return isOutDated;
        }

        /**
         * Returns, whether an update of the data should be retried. Will always return true, if the previous update did
         * not fail, so it has to be called in combination with <code>isOutDated()</code>. If the previous updated has
         * failed, it will return true when this happened longer ago then <code>retryUpdateInterval</code>.
         * 
         * @param retryUpdateInterval
         *            the interval in when update-retries should be made when the previous update has failed
         * @return whether an update of the data should be retried.
         */
        boolean shouldRetryUpdate(final long retryUpdateInterval) {
            boolean shouldRetry;
            this.metaDataLock.readLock().lock();
            try {
                if (this.lastFailedUpdate == -1) {
                    // update has not failed earlier, go ahead.
                    shouldRetry = true;
                } else {
                    // update has failed earlier, only retry updating when a certain time has passed since
                    // last update.
                    shouldRetry = this.lastFailedUpdate + retryUpdateInterval < System.currentTimeMillis();
                }
            } finally {
                this.metaDataLock.readLock().unlock();
            }
            return shouldRetry;
        }

        /**
         * Returns string-representation of the object.
         * 
         * @return a string-representation of the object.
         */
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            this.metaDataLock.readLock().lock();
            this.vehicleDataLock.readLock().lock();
            try {
                builder.append("CachedVehicleData for ").append(this.vehicleData.getBrand()).append("/")
                        .append(this.vehicleData.getMarket()).append("/").append(this.vehicleData.getLanguage());
                builder.append(" Last usage: ").append(new Date(this.lastAccessed));
                builder.append(" Number of uses: ").append(this.hits);
            } finally {
                this.metaDataLock.readLock().unlock();
                this.vehicleDataLock.readLock().unlock();
            }
            return builder.toString();
        }

        /**
         * Updates the vehicle-data. Also sets caching meta-information.
         * 
         * @param vehicleData
         *            the new data.
         */
        void updateData(final VehicleData vehicleData) {
            this.metaDataLock.writeLock().lock();
            this.vehicleDataLock.writeLock().lock();
            try {
                this.vehicleData = vehicleData;
                this.hits = 0;
                this.updateTime = System.currentTimeMillis();
                this.lastAccessed = 0;
                this.lastFailedUpdate = -1;
            } finally {
                this.metaDataLock.writeLock().unlock();
                this.vehicleDataLock.writeLock().unlock();
            }
        }

        /**
         * Has to be called when an update of the data has failed. This is important to be able to determine when
         * updating the data should be retied the next time.
         */
        void updateFailed() {
            this.metaDataLock.writeLock().lock();
            try {
                this.lastFailedUpdate = System.currentTimeMillis();
            } finally {
                this.metaDataLock.writeLock().unlock();
            }
        }

        /**
         * Should be called when the cached data is used in order to update caching usage statistics.
         */
        void use() {
            this.metaDataLock.writeLock().lock();
            try {
                this.hits++;
                this.lastAccessed = System.currentTimeMillis();
            } finally {
                this.metaDataLock.writeLock().unlock();
            }
        }
    }

    /**
     * A key for accessing data in the cache.
     */
    static final class CacheKey implements Serializable, ReadWriteLock {

        /**
         * UID for serialization.
         */
        private static final long serialVersionUID = 1L;

        private final String key;

        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        /**
         * Creates a new instance.
         * 
         * @param skey
         *            a key-string containing brand, country and language.
         */
        public CacheKey(final String skey) {
            this.key = skey;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object o) {
            if (o instanceof CacheKey) {
                return ((CacheKey) o).key.equals(this.key);
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        /**
         * Returns the internal key.
         * 
         * @return the internal string representation of this key.
         */
        String internalKey() {
            return this.key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public java.util.concurrent.locks.Lock readLock() {
            return this.lock.readLock();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "CacheKey{" + this.key + "}";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Lock writeLock() {
            return this.lock.writeLock();
        }
    }

    private static final long A_SECOND = 1000L;

    private static final String CRX_CACHED_DATA_KEY = "CachedVehicleData";

    static final long DEFAULT_CACHED_VEHICLE_DATA_MAX_AGE = A_SECOND * 60L * 5L; // 5 min.

    static final long DEFAULT_CACHED_VEHICLE_DATA_RETRY_INTERVAL = A_SECOND * 30L; // 30 sec.

    private static final Logger LOG = LoggerFactory.getLogger(VehicleDataCache.class);

    private final ReadWriteLock allCacheKeysLock = new ReentrantReadWriteLock();

    private Map<CacheKey, CachedVehicleData> cache = new HashMap<CacheKey, CachedVehicleData>();

    private long cachedVehicleDataMaxAge = DEFAULT_CACHED_VEHICLE_DATA_MAX_AGE;

    private long cachedVehicleDataRetryInterval = DEFAULT_CACHED_VEHICLE_DATA_RETRY_INTERVAL;

    private final Map<String, CacheKey> cacheKeys = new HashMap<String, CacheKey>();

    /**
     * Returns all cache keys.
     * 
     * @return all cache keys.
     */
    synchronized List<CacheKey> getAllCacheKeys() {
        ArrayList<CacheKey> allCacheKeys = new ArrayList<CacheKey>();
        try {
            this.allCacheKeysLock.readLock().lock();
            allCacheKeys.addAll(this.cacheKeys.values());
        } finally {
            this.allCacheKeysLock.readLock().unlock();
        }
        return allCacheKeys;
    }

    /**
     * Returns a cache key.
     * 
     * @param pageHandleUpToLanguageLevel
     *            The handle of the currently requested page, trimmed to the language-level.
     * @return the key for accessing data in the cache
     */
    synchronized CacheKey getCacheKey(final String pageHandleUpToLanguageLevel) {

        try {
            this.allCacheKeysLock.readLock().lock();
            CacheKey key = this.cacheKeys.get(pageHandleUpToLanguageLevel);
            if (null == key) {
                // upgrade lock:
                this.allCacheKeysLock.readLock().unlock(); // must unlock first to obtain writelock
                this.allCacheKeysLock.writeLock().lock();
                try {
                    // test again if it has been created/added in the meantime...
                    key = this.cacheKeys.get(pageHandleUpToLanguageLevel);
                    if (null == key) {
                        key = new CacheKey(pageHandleUpToLanguageLevel);
                        this.cacheKeys.put(pageHandleUpToLanguageLevel, key);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Created new cache-key " + key);
                        }
                    }
                } finally {
                    // downgrade lock:
                    this.allCacheKeysLock.readLock().lock(); // reacquire read without giving up write lock
                    this.allCacheKeysLock.writeLock().unlock(); // unlock write, still hold read
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Delivering cache-key " + key);
            }
            return key;
        } finally {
            this.allCacheKeysLock.readLock().unlock();
        }
    }

    /**
     * Fetches the vehicle-data for the given brand, country and language. This method does not care for
     * synchronization/locking, this has to be done by the caller (read lock should be acquired).
     * 
     * @param key
     *            the key for finding the data
     * @param allowOutdated
     *            whether the data should even be returned when it is already outdated.
     * @return VehicleData or null, if not present or outdated.
     */
    VehicleData getVehicleData(final CacheKey key,
                               final boolean allowOutdated) {
        CachedVehicleData data = this.cache.get(key);
        if (null != data && !allowOutdated && data.isOutDated(this.cachedVehicleDataMaxAge)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found data for " + key + ", but it is out-dated. Caching-info: " + data.toString());
            }
            data = null;
        }
        if (null != data) {
            if (LOG.isDebugEnabled()) {
                data.use();
                LOG.debug("Delivering data for " + key + " with allowOutdated = " + allowOutdated + ". Caching-info: "
                        + data.toString());
            }
            return data.getVehicleData();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Not delivering any data for " + key + " with allowOutdated = " + allowOutdated);
        }
        return null;
    }

    /**
     * Returns whether the data is present in the cache, no matter whether up-to-date or not. This method does not care
     * for synchronization/locking, this has to be done by the caller (read lock should be acquired).
     * 
     * @param key
     *            the key for finding the data in the cache.
     * @return whether the data is present in the cache, no matter whether up-to-date or not.
     */
    boolean hasVehicleData(final CacheKey key) {
        return this.cache.get(key) != null;
    }

    /**
     * Invalidates the data which is identified by the key, the data will be considered to be out-dated from then on.
     * Usually, there is no need to call this method explicitly, because the up-to-date check will be performed
     * automatically when a vehicle-data is fetched from the cache. This method does not care for
     * synchronization/locking, this has to be done by the caller (write lock should be acquired).
     * 
     * @param key
     *            the key that identifies the vehicle-data that will be invalidated.
     */
    void invalidate(final CacheKey key) {
        final CachedVehicleData data = this.cache.get(key);
        if (null != data) {
            data.invalidate();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invalidated data for " + key);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot invalidate data for " + key + ", not data found.");
            }
        }
    }

    /**
     * Invalidates all entries in the cache so that they are considered out-dated. This methods cares for
     * synchronization/locking, the caller should not hold any locks.
     */
    void invalidateAll() {
        this.allCacheKeysLock.writeLock().lock();
        try {
            final Set<Map.Entry<CacheKey, CachedVehicleData>> allEntries = this.cache.entrySet();
            for (Map.Entry<CacheKey, CachedVehicleData> entry : allEntries) {
                entry.getKey().writeLock().lock();
                try {
                    entry.getValue().invalidate();
                } finally {
                    entry.getKey().writeLock().unlock();
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Invalidated data for " + entry.getKey());
                }
            }
        } finally {
            this.allCacheKeysLock.writeLock().unlock();
        }
        LOG.info("All cached vehicle-data has been invalidated.");
    }

    /**
     * Loads all vehicle data that has previously been persisted.
     * 
     * @param crxSession
     *            a crx-session
     */
    @SuppressWarnings("unchecked")
    synchronized void loadPersisted(final Session crxSession) {
        this.cache = (Map<CacheKey, CachedVehicleData>) PersistentCacheUtil.loadPersistedObject(CRX_CACHED_DATA_KEY, crxSession);
        if (null == this.cache) {
            LOG.warn("No persisted vehicle-data could be loaded into the cache.");
            this.cache = new HashMap<CacheKey, CachedVehicleData>();
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("Vehicle-Data-Cache has been filled with persisted data."
                        + "Number of loaded Vehicle-Data objects: " + this.cache.size());
            }
        }

        // remove all cache entries with keys that were constructed using the old logic
        // "<brand>/<country>/<lang>".
        this.allCacheKeysLock.writeLock().lock();
        try {
            final ArrayList<CacheKey> doomedEntries = new ArrayList<CacheKey>();
            for (Map.Entry<CacheKey, CachedVehicleData> entry : this.cache.entrySet()) {
                if (!entry.getKey().key.startsWith("/content")) {
                    LOG.warn("Obsolete cache-entry with key '" + entry.getKey().key + "' will be removed.");
                    doomedEntries.add(entry.getKey());
                }
            }
            for (CacheKey cacheKey : doomedEntries) {
                this.cache.remove(cacheKey);
            }
        } finally {
            this.allCacheKeysLock.writeLock().unlock();
        }
    }

    /**
     * Stores in the information, that an attempt to update the vehicle-data with the given key has failed. Thus, the
     * data will temporarily be considered as valid again, because no newer version is available. This method does not
     * care for synchronization/locking, this has to be done by the caller (write lock should be acquired).
     * 
     * @param key
     *            the caching-key if the data.
     * @param errorMessage
     *            a message that informs about the reason for the failed update
     */
    void notifyFailedUpdate(final CacheKey key,
                            final String errorMessage) {
        final CachedVehicleData data = this.cache.get(key);
        if (data == null) {
            throw new IllegalArgumentException("No vehicle-data present in cache for key " + key);
        }
        data.updateFailed();
        data.getVehicleData().setErrorMessage(errorMessage);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received update-failure-notfication for data " + key);
        }
    }

    /**
     * Persists all currently stored vehicle-data to the CRX.
     * 
     * @param crxSession
     *            a crx-session
     */
    synchronized void persist(final Session crxSession) {
        PersistentCacheUtil.persistObject(CRX_CACHED_DATA_KEY, (Serializable) this.cache, crxSession);
    }

    /**
     * Sets, how long cached vehicle-data is considered to be up-to-date.
     * 
     * @param maxAgeMillis
     *            the max age in milliseconds.
     */
    void setCachedVehicleDataMaxAge(final long maxAgeMillis) {
        this.cachedVehicleDataMaxAge = maxAgeMillis;
    }

    /**
     * Sets the interval which shall be used for reload-attempts when refreshing a vehicle-data has failed previously.
     * 
     * @param intervalMillis
     *            the interval in milliseconds
     */
    void setCachedVehicleDataReloadRetryInterval(final long intervalMillis) {
        this.cachedVehicleDataRetryInterval = intervalMillis;
    }

    /**
     * Returns whether the cached vehicle data with is identified by the given key should be updated. This method does
     * not care for synchronization/locking, this has to be done by the caller (read lock should be acquired).
     * 
     * @param key
     *            the key to access to data in the cache
     * @return whether the data with the given key should be updated.
     */
    boolean shouldBeUpdated(final CacheKey key) {
        final CachedVehicleData data = this.cache.get(key);
        if (null != data && data.isOutDated(this.cachedVehicleDataMaxAge)
                && data.shouldRetryUpdate(this.cachedVehicleDataRetryInterval)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returing 'true' for question whether data " + key + " should be updated.");
            }
            return true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Returing 'false' for question whether data " + key + " should be updated.");
        }
        return false;
    }

    /**
     * Stores or updates the given data in the cache. This method does not care for synchronization/locking, this has to
     * be done by the caller (write lock should be acquired).
     * 
     * @param key
     *            the key for accessing the data in the cache.
     * @param vehicleData
     *            the data to be stored.
     */
    void storeOrUpdate(final CacheKey key,
                       final VehicleData vehicleData) {
        CachedVehicleData data = this.cache.get(key);
        if (null == data) {
            data = new CachedVehicleData(vehicleData);
            this.cache.put(key, data);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Data for " + key + " has been stored in cache.");
            }
        } else {
            data.updateData(vehicleData);
            data.getVehicleData().setErrorMessage(null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Data " + key + " has been updated.");
            }
        }
    }
}