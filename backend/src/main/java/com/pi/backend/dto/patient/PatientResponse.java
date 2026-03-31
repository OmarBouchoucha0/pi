package com.pi.backend.dto.patient;

import java.time.LocalDateTime;

/**
 * Response DTO for patient data.
 */
public record PatientResponse(
    Long id,
    Long userId,
    String firstName,
    String lastName,
    String email,
    String medicalRecordNumber,
    String bloodType,
    String allergies,
    String chronicConditions,
    String emergencyContactName,
    String emergencyContactPhone,
    Long primaryDepartmentId,
    LocalDateTime createdAt
) {}
