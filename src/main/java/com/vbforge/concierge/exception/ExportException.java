package com.vbforge.concierge.exception;

/**
 * Thrown when export operation fails
 */
public class ExportException extends ConciergeSchedulerException {

    public ExportException(String format, Throwable cause) {
        super(String.format("Failed to export schedule to %s format", format), cause);
    }

    public ExportException(String message) {
        super(message);
    }
}