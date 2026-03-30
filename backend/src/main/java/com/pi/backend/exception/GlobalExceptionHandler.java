package com.pi.backend.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
}
