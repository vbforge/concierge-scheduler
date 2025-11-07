package com.vbforge.concierge.exception;

/**
 * Thrown when snapshot creation or restoration fails
 */
public class SnapshotException extends ConciergeSchedulerException {

    public SnapshotException(String message) {
        super(message);
    }

    public SnapshotException(String message, Throwable cause) {
        super(message, cause);
    }
}