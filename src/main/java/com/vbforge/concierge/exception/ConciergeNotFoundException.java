package com.vbforge.concierge.exception;

/**
 * Thrown when a concierge is not found
 */
public class ConciergeNotFoundException extends ResourceNotFoundException {

    public ConciergeNotFoundException(Long id) {
        super("Concierge", "id", id);
    }

    public ConciergeNotFoundException(String name) {
        super("Concierge", "name", name);
    }
}