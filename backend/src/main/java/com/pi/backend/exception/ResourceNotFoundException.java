package com.pi.backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found. Returns HTTP 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resource;
    private final String field;
    private final Object value;

    /**
     * Creates a ResourceNotFoundException for a resource not found by ID.
     *
     * @param resource the resource type name
     * @param id       the ID that was not found
     */
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s not found with id: %d", resource, id));
        this.resource = resource;
        this.field = "id";
        this.value = id;
    }

    /**
     * Creates a ResourceNotFoundException for a resource not found by a specific field.
     *
     * @param resource the resource type name
     * @param field    the field name that was searched
     * @param value    the value that was not found
     */
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: %s", resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    /**
     * Returns the resource type name.
     */
    public String getResource() {
        return resource;
    }

    /**
     * Returns the field name that was searched.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the value that was not found.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the current timestamp at the time this method is called.
     * Note: This does NOT return the time the exception was created.
     */
    public LocalDateTime getTimestamp() {
        return LocalDateTime.now();
    }
}
