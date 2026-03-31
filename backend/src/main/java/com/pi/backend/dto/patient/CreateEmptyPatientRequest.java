package com.pi.backend.dto.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating an empty patient.
 * Creates a User account with an empty Patient profile.
 */
public record CreateEmptyPatientRequest(
    @NotNull(message = "Tenant ID is required")
    Long tenantId,

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    String email,

    @NotBlank(message = "Password is required")
    String passwordHash
) {}
