package com.pi.backend.dto;

import com.pi.backend.model.TenantStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a new tenant.
 */
public record CreateTenantRequest(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Status is required") TenantStatus status) {
}
