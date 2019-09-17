package com.github.johnsonmoon.commons.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Create by xuyh at 2019/9/17 19:54.
 */
public class TryTest {
    private static Logger logger = LoggerFactory.getLogger(TryTest.class);

    @Test
    public void test() {
        Try.tryOperation(() -> success());
        String result = Try.tryOperation(() -> succeeded());
        System.out.println(result);

        try {
            Try.tryOperationWithThrow(() -> fail(), "error!");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            String result2 = Try.tryOperationWithThrow(() -> failed(), "error!");
            System.out.println(result2);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }


    private void fail() {
        System.out.println("failed!");
        throw new RuntimeException("failed!");
    }

    private String failed() {
        System.out.println("failed!");
        throw new RuntimeException("failed!");
    }

    private void success() {
        System.out.println("succeeded!");
    }

    private String succeeded() {
        System.out.println("succeeded!");
        return "success!";
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }
}
