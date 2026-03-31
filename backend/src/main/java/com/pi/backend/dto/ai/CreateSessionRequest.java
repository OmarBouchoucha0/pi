package com.pi.backend.dto.ai;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a new chat session.
 */
public record CreateSessionRequest(
    @NotNull(message = "Patient ID is required")
    Long patientId,

    @NotNull(message = "Tenant ID is required")
    Long tenantId
) {}
