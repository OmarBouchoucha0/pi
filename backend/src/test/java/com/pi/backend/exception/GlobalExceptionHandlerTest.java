package com.pi.backend.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Unit tests for {@link GlobalExceptionHandler}. Verifies that each exception
 * handler returns the correct HTTP status and response body structure.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    /**
     * Verifies that ResourceNotFoundException returns 404 with expected body fields.
     */
    @Test
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Patient", 999L);

        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Patient", response.getBody().get("resource"));
        assertNotNull(response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    /**
     * Verifies that ResourceNotFoundException with field returns 404 with field details.
     */
    @Test
    void handleResourceNotFound_withField_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "email", "test@test.com");

        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User", response.getBody().get("resource"));
        assertEquals("email", response.getBody().get("field"));
    }

    /**
     * Verifies that DuplicateResourceException returns 409 with expected body fields.
     */
    @Test
    void handleDuplicateResource_returns409() {
        DuplicateResourceException ex = new DuplicateResourceException("User", "email", "test@test.com");

        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateResource(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conflict", response.getBody().get("error"));
        assertEquals("User", response.getBody().get("resource"));
        assertEquals("email", response.getBody().get("field"));
    }

    /**
     * Verifies that validation errors return 400 with field-level error details.
     */
    @Test
    void handleValidation_returns400() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().get("error"));
        assertEquals("Invalid request data", response.getBody().get("message"));
        assertNotNull(response.getBody().get("errors"));
        assertNotNull(response.getBody().get("timestamp"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("must not be blank", errors.get("fieldName"));
    }

    /**
     * Verifies that malformed JSON returns 400 with appropriate error message.
     */
    @Test
    void handleJsonParse_returns400() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<Map<String, Object>> response = handler.handleJsonParse(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Malformed JSON request", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    /**
     * Verifies that unexpected exceptions return 500 with generic error message.
     */
    @Test
    void handleGeneric_returns500() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("An unexpected error occurred", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}
