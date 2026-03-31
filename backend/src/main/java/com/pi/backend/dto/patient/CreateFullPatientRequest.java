package com.pi.backend.dto.patient;

/**
 * Request DTO for creating a patient with full details.
 * Creates both a User account and a Patient profile.
 */
public record CreateFullPatientRequest(
    Long tenantId,
    String firstName,
    String lastName,
    String email,
    String passwordHash,
    String medicalRecordNumber,
    String bloodType,
    String allergies,
    String chronicConditions,
    String emergencyContactName,
    String emergencyContactPhone,
    Long primaryDepartmentId
) {}
