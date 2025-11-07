package com.vbforge.concierge.exception;

/**
 * Thrown when date validation fails
 */
public class InvalidDateException extends InvalidInputException {

    public InvalidDateException(String message) {
        super(message);
    }
}