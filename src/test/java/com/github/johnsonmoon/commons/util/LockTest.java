package com.github.johnsonmoon.commons.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create by xuyh at 2019/9/17 14:59.
 */
public class LockTest {
    private static Logger logger = LoggerFactory.getLogger(LockTest.class);
    private AtomicBoolean shutdown = new AtomicBoolean(false);

    @Test
    public void test() throws Exception {
        Stack<String> dataStack = new Stack<>();
        for (int i = 0; i < 200; i++) {
            dataStack.push("" + i);
        }
        CountDownLatch countDownLatch = new CountDownLatch(dataStack.size());
        Lock lock = Lock.create(3000);
        List<Thread> threads = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            final String threadName = "" + j;
            threads.add(new Thread(() -> {
                while (!shutdown.get()) {
                    sleep();
                    try {
                        lock.get();
                        if (!dataStack.isEmpty()) {
                            String data = dataStack.pop();
                            System.out.println(String.format("data: %-6s thread: %s", data, threadName));
                        }
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    } finally {
                        countDownLatch.countDown();
                        lock.release();
                    }
                }
            }, threadName));
        }
        threads.forEach(Thread::start);
        countDownLatch.await();
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }
}
