package com.github.johnsonmoon.commons.cache;

import com.github.johnsonmoon.commons.util.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create by xuyh at 2019/7/23 11:46.
 */
public class LocalCache<KEY, VALUE> {
    private static Logger logger = LoggerFactory.getLogger(LocalCache.class);
    private Map<KEY, CacheModel> cacheMap = new ConcurrentHashMap<>();
    private Long cacheTimeMillis = 5 * 60 * 1000L;
    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private Lock lock = Lock.create(30_000);
    private CacheAcquireOperation<KEY, VALUE> cacheAcquireOperation;

    /**
     * Constructor
     */
    public LocalCache() {
        initCacheCleanTask("cache-clean-thread-" + System.currentTimeMillis());
    }

    /**
     * Constructor
     */
    public LocalCache(String name) {
        initCacheCleanTask("cache-clean-thread-" + name);
    }

    /**
     * Set cache time for each key-value pair.
     *
     * @param cacheTimeMinutes Unit: minutes
     * @return {@link LocalCache}
     */
    public LocalCache<KEY, VALUE> cacheTimeMinutes(int cacheTimeMinutes) {
        cacheTimeMillis = cacheTimeMinutes * 60 * 1000L;
        return this;
    }

    /**
     * Set cache time for each key-value pair.
     *
     * @param cacheTimeSeconds Unit: seconds
     * @return {@link LocalCache}
     */
    public LocalCache<KEY, VALUE> cacheTimeSeconds(long cacheTimeSeconds) {
        cacheTimeMillis = cacheTimeSeconds * 1000L;
        return this;
    }

    /**
     * Set cache data acquire operation.
     *
     * @param cacheAcquireOperation {@link CacheAcquireOperation}
     * @return {@link LocalCache}
     */
    public LocalCache<KEY, VALUE> cacheAcquireOperation(CacheAcquireOperation<KEY, VALUE> cacheAcquireOperation) {
        this.cacheAcquireOperation = cacheAcquireOperation;
        return this;
    }

    /**
     * Put cache into local cache.
     *
     * @param key   KEY
     * @param value VALUE
     */
    public void put(KEY key, VALUE value) {
        try {
            lock.get(3000);
            cacheMap.put(key, new CacheModel(value));
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            lock.release();
        }
    }

    /**
     * Remove cache from local cache.
     *
     * @param key KEY
     */
    public void remove(KEY key) {
        try {
            lock.get(3000);
            cacheMap.remove(key);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            lock.release();
        }
    }

    /**
     * Get cache from local cache.
     *
     * @param key KEY
     * @return VALUE
     */
    @SuppressWarnings("unchecked")
    public VALUE get(KEY key) {
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key).getObject();
        }
        if (cacheAcquireOperation != null) {
            VALUE value = null;
            try {
                value = (VALUE) cacheAcquireOperation.acquire(key);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            if (value != null) {
                put(key, value);
                return value;
            }
        }
        return null;
    }

    /**
     * Check KEY exist from local cache.
     *
     * @param key KEY
     * @return true/false
     */
    public boolean containsKey(KEY key) {
        return cacheMap.containsKey(key);
    }

    private void initCacheCleanTask(String name) {
        new Thread(() -> {
            while (!shutdown.get()) {
                sleep(10_000);
                checkCleanCache();
            }
        }, name).start();
    }

    private void checkCleanCache() {
        try {
            lock.get(6000);
            List<KEY> invalidKeys = new ArrayList<>();
            for (Map.Entry<KEY, CacheModel> entry : cacheMap.entrySet()) {
                KEY key = entry.getKey();
                CacheModel cacheModel = entry.getValue();
                if (System.currentTimeMillis() - cacheModel.getCreateTime() >= cacheTimeMillis) {
                    invalidKeys.add(key);
                }
            }
            sleep(100);
            invalidKeys.forEach(cacheMap::remove);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            lock.release();
        }
    }

    private void sleep(long timeMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeMillis);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    private class CacheModel {
        private VALUE object;
        private Long createTime;

        CacheModel(VALUE object) {
            this.createTime = System.currentTimeMillis();
            this.object = object;
        }

        VALUE getObject() {
            return object;
        }

        Long getCreateTime() {
            return createTime;
        }
    }

    public interface CacheAcquireOperation<KEY, VALUE> {
        VALUE acquire(KEY key);
    }
}
