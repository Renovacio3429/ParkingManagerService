package org.parking.manager.service.exception;

public class ParkingFullException extends RuntimeException {
    public ParkingFullException(String message) {
        super(message);
    }
}
