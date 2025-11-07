package com.vbforge.concierge.exception;

/**
 * Thrown when a concierge already exists with the same name
 */
public class ConciergeDuplicateException extends ConciergeSchedulerException {

    public ConciergeDuplicateException(String name) {
        super(String.format("Concierge with name '%s' already exists", name));
    }
}