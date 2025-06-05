package org.parking.manager.service.exception;

public class DuplicateEntryException extends RuntimeException {
    public DuplicateEntryException(String message) {
        super(message);
    }
}
