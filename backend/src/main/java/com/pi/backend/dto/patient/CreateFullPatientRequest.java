package com.pi.backend.dto.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating a patient with full details.
 * Creates both a User account and a Patient profile.
 */
public record CreateFullPatientRequest(
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
    String password,

    String medicalRecordNumber,
    String bloodType,
    String allergies,
    String chronicConditions,
    String emergencyContactName,
    String emergencyContactPhone,
    Long primaryDepartmentId
) {}
