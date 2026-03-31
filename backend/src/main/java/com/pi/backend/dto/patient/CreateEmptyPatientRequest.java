package com.pi.backend.dto.patient;

/**
 * Request DTO for creating an empty patient.
 * Creates a User account with an empty Patient profile.
 */
public record CreateEmptyPatientRequest(
    Long tenantId,
    String firstName,
    String lastName,
    String email,
    String passwordHash
) {}
