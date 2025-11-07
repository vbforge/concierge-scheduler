package com.vbforge.concierge.exception;

/**
 * Base exception for all custom exceptions in the application
 */
public class ConciergeSchedulerException extends RuntimeException {

    public ConciergeSchedulerException(String message) {
        super(message);
    }

    public ConciergeSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
