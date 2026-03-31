package com.pi.backend.dto.patient;

/**
 * Request DTO for updating patient medical information.
 */
public record UpdatePatientRequest(
    String bloodType,
    String allergies,
    String chronicConditions,
    String emergencyContactName,
    String emergencyContactPhone
) {}
