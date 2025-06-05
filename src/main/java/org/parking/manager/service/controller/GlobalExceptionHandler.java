package org.parking.manager.service.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.ApiError;
import org.parking.manager.service.exception.DuplicateEntryException;
import org.parking.manager.service.exception.ParkingFullException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateEntryException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ParkingFullException.class)
    public ResponseEntity<ApiError> handleFull(ParkingFullException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiError> handleValidation(Exception ex) {
        String msg = "Invalid data: " + ex.getMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex) {
        log.error("Internal error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.value(), message));
    }
}
