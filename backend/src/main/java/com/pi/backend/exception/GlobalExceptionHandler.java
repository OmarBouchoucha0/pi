package com.pi.backend.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that maps exceptions to HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ResourceNotFoundException and returns HTTP 404.
     *
     * @param ex the caught exception
     * @return response body containing error, message, resource, field, and timestamp
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = Map.of(
            "error", "Not Found",
            "message", ex.getMessage(),
            "resource", ex.getResource(),
            "field", ex.getField(),
            "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles DuplicateResourceException and returns HTTP 409.
     *
     * @param ex the caught exception
     * @return response body containing error, message, resource, field, and timestamp
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResource(DuplicateResourceException ex) {
        Map<String, Object> body = Map.of(
            "error", "Conflict",
            "message", ex.getMessage(),
            "resource", ex.getResource(),
            "field", ex.getField(),
            "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles validation failures and returns HTTP 400.
     *
     * @param ex the caught exception
     * @return response body containing error, message, per-field errors map, and timestamp
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (existing, replacement) -> existing
            ));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Validation Failed");
        body.put("message", "Invalid request data");
        body.put("errors", fieldErrors);
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles malformed JSON requests and returns HTTP 400.
     *
     * @param ex the caught exception
     * @return response body containing error, message, and timestamp
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParse(HttpMessageNotReadableException ex) {
        Map<String, Object> body = Map.of(
            "error", "Bad Request",
            "message", "Malformed JSON request",
            "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles all uncaught exceptions and returns HTTP 500.
     * Logs the full stack trace before responding.
     *
     * @param ex the caught exception
     * @return response body containing error, message, and timestamp
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        //since we are returning a error 500(server side) here we need to log it
        log.error("Unexpected error occurred", ex);
        Map<String, Object> body = Map.of(
            "error", "Internal Server Error",
            "message", "An unexpected error occurred",
            "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
