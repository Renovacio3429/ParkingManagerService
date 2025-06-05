package org.parking.manager.service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parking.manager.service.exception.DuplicateEntryException;
import org.parking.manager.service.exception.ParkingFullException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.BindException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleDuplicate_shouldReturnConflict() {
        var ex = new DuplicateEntryException("Already exists");
        var response = handler.handleDuplicate(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Already exists");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    void handleFull_shouldReturnBadRequest() {
        var ex = new ParkingFullException("Parking is full");
        var response = handler.handleFull(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Parking is full");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleNotFound_shouldReturnNotFound() {
        var ex = new EntityNotFoundException("Not found");
        var response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Not found");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void handleBadRequest_shouldReturnBadRequest() {
        var ex = new IllegalArgumentException("Bad input");
        var response = handler.handleBadRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Bad input");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleValidation_shouldReturnBadRequestForMethodArgumentNotValidException() {
        var ex = mock(MethodArgumentNotValidException.class);
        var response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Invalid data");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleValidation_shouldReturnBadRequestForBindException() {
        var ex = mock(BindException.class);
        var response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Invalid data");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleAny_shouldReturnInternalServerError() {
        var ex = new Exception("Some error");
        var response = handler.handleAny(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Internal server error");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
