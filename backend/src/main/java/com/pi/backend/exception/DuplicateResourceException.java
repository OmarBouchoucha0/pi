package com.pi.backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a resource already exists. Returns HTTP 409.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    private final String resource;
    private final String field;
    private final Object value;

    /**
     * Creates a DuplicateResourceException for a resource that already exists.
     *
     * @param resource the resource type name
     * @param field    the field name that has a duplicate value
     * @param value    the duplicate value
     */
    public DuplicateResourceException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resource, field, value));
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
     * Returns the field name that has a duplicate value.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the duplicate value.
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
