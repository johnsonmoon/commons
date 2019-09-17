package com.github.johnsonmoon.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create by xuyh at 2019/9/6 00:26.
 */
public class Lock {
    private static Logger logger = LoggerFactory.getLogger(Lock.class);
    private AtomicBoolean locked = new AtomicBoolean(false);
    private long defaultTimeout = Long.MAX_VALUE;

    /**
     * Create a lock
     *
     * @return {@link Lock}
     */
    public static Lock create() {
        return new Lock();
    }

    /**
     * Create a lock.
     *
     * @param defaultTimeout default timeout, Unit: millis
     * @return {@link Lock}
     */
    public static Lock create(long defaultTimeout) {
        Lock lock = new Lock();
        lock.defaultTimeout = defaultTimeout;
        return lock;
    }

    /**
     * Set default lock acquire timeout.
     *
     * @param defaultTimeout default time out setting
     */
    public Lock defaultTimeout(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
        return this;
    }

    /**
     * Get lock, wait util default timeout or lock acquired.
     */
    public void get() {
        get(defaultTimeout);
    }

    /**
     * Get lock, wait util timeout or lock acquired.
     *
     * @param timeout wait util timeout, Unit: millis
     */
    public void get(long timeout) {
        long start = System.currentTimeMillis();
        while (locked.get()) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            if (System.currentTimeMillis() - start >= timeout) {
                break;
            }
        }
        try {
            locked.set(true);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * Release lock.
     */
    public void release() {
        try {
            locked.set(false);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
