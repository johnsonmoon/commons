package com.github.johnsonmoon.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create by xuyh at 2019/8/19 16:51.
 */
public class Try {
    private static Logger logger = LoggerFactory.getLogger(Try.class);

    /**
     * Try without catch runtime exception.
     *
     * @param returnOperation {@link ReturnOperation}
     */
    public static <T> T tryOperation(ReturnOperation<T> returnOperation) {
        T result = null;
        try {
            result = returnOperation.operate();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Try without catch runtime exception.
     *
     * @param returnOperation {@link ReturnOperation}
     * @param msg             message throw while exception happened
     */
    public static <T> T tryOperationWithThrow(ReturnOperation<T> returnOperation, String msg) {
        T result;
        try {
            result = returnOperation.operate();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new TryException(msg, e);
        }
        return result;
    }

    /**
     * Try without catch runtime exception.
     *
     * @param operation {@link Operation}
     */
    public static void tryOperation(Operation operation) {
        try {
            operation.operate();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * Try without catch runtime exception.
     *
     * @param operation {@link Operation}
     * @param msg       message throw while exception happened
     */
    public static void tryOperationWithThrow(Operation operation, String msg) {
        try {
            operation.operate();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new TryException(msg, e);
        }
    }

    /**
     * Operation with object return
     */
    public interface ReturnOperation<T> {
        T operate();
    }

    /**
     * Operation with nothing return
     */
    public interface Operation {
        void operate();
    }

    /**
     * The exception which try operation with exception throw while exception happened.
     */
    public static class TryException extends RuntimeException {
        public TryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
