package com.vbforge.concierge.exception;

public class SnapshotAlreadyExistsException extends RuntimeException {
    public SnapshotAlreadyExistsException(String message) {
        super(message);
    }
}