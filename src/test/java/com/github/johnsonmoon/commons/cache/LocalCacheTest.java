package com.github.johnsonmoon.commons.cache;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Create by xuyh at 2019/9/17 15:38.
 */
public class LocalCacheTest {
    private static Logger logger = LoggerFactory.getLogger(LocalCacheTest.class);

    private LocalCache<String, String> localCache =
            new LocalCache<String, String>("test-local-cache")
                    .cacheTimeSeconds(30)
                    .cacheAcquireOperation(key -> key + "-" + System.currentTimeMillis());

    @Test
    public void test() {
        localCache.put("abc", "def");

        sleep(20_000);

        System.out.println(localCache.containsKey("abc"));

        sleep(20_000);
        System.out.println("----------------------");
        System.out.println(localCache.get("def"));
        System.out.println(localCache.get("ghi"));

        sleep(20_000);
        System.out.println("----------------------");
        System.out.println(localCache.containsKey("abc"));
        System.out.println(localCache.containsKey("def"));
        System.out.println(localCache.containsKey("ghi"));

        sleep(30_000);
        System.out.println("----------------------");
        System.out.println(localCache.containsKey("abc"));
        System.out.println(localCache.containsKey("def"));
        System.out.println(localCache.containsKey("ghi"));
    }

    @Test
    public void cacheGetTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println(localCache.get(i + "-name"));
        }
        sleep(10_000);
        System.out.println("----------------------");
        for (int i = 0; i < 10; i++) {
            System.out.println(localCache.get(i + "-name"));
        }
        sleep(30_000);
        System.out.println("----------------------");
        for (int i = 0; i < 10; i++) {
            System.out.println(localCache.get(i + "-name"));
        }
    }

    @Test
    public void manyTest() {
        for (int i = 0; i < 10000; i++) {
            System.out.println(localCache.get(i + "-name"));
        }
        sleep(40_000);
        System.out.println("----------------------");
        for (int i = 0; i < 10000; i++) {
            System.out.println(localCache.get(i + "-name"));
        }
    }

    private void sleep(long timeMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeMillis);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }
}
