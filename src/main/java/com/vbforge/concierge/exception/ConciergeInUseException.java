package com.vbforge.concierge.exception;

/**
 * Thrown when trying to delete a concierge with existing shift assignments
 */
public class ConciergeInUseException extends ConciergeSchedulerException {

    public ConciergeInUseException(String name) {
        super(String.format("Cannot delete concierge '%s' because they have existing shift assignments", name));
    }
}