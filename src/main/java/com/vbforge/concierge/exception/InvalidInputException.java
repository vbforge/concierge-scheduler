package com.vbforge.concierge.exception;

/**
 * Thrown when input validation fails
 */
public class InvalidInputException extends ConciergeSchedulerException {

    public InvalidInputException(String message) {
        super(message);
    }
}