package com.pi.backend.dto;

import com.pi.backend.model.TenantStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for adding a message to a chat session.
 */
public record AddMessageRequest(
        @NotNull(message = "Name is required") String name,
        @NotNull(message = "Status is required") TenantStatus status) {
}
