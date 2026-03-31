package com.pi.backend.controller.patient;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.backend.dto.patient.CreateEmptyPatientRequest;
import com.pi.backend.dto.patient.CreateFullPatientRequest;
import com.pi.backend.dto.patient.PatientResponse;
import com.pi.backend.dto.patient.UpdatePatientRequest;
import com.pi.backend.model.user.Patient;
import com.pi.backend.service.user.PatientService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing patient records.
 * Provides endpoints for creating, retrieving, updating, and deleting patients.
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Creates a patient with full details (User + Patient).
     *
     * @param request the patient creation request
     * @return the created patient
     */
    @PostMapping("/full")
    public ResponseEntity<PatientResponse> createFullPatient(@RequestBody CreateFullPatientRequest request) {
        Patient patient = patientService.createPatientWithUser(
            request.tenantId(), request.firstName(), request.lastName(),
            request.email(), request.passwordHash(),
            request.medicalRecordNumber(), request.bloodType(),
            request.allergies(), request.chronicConditions(),
            request.emergencyContactName(), request.emergencyContactPhone(),
            request.primaryDepartmentId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(patient));
    }

    /**
     * Creates an empty patient (User + empty Patient profile).
     *
     * @param request the patient creation request
     * @return the created patient
     */
    @PostMapping("/empty")
    public ResponseEntity<PatientResponse> createEmptyPatient(@RequestBody CreateEmptyPatientRequest request) {
        Patient patient = patientService.createEmptyPatient(
            request.tenantId(), request.firstName(), request.lastName(),
            request.email(), request.passwordHash()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(patient));
    }

    /**
     * Retrieves all patients.
     *
     * @return list of all patients
     */
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        List<PatientResponse> response = patients.stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a patient by ID.
     *
     * @param id the patient ID
     * @return the patient
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(toResponse(patient));
    }

    /**
     * Updates a patient's medical information.
     *
     * @param id the patient ID
     * @param request the update request
     * @return the updated patient
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id,
                                                         @RequestBody UpdatePatientRequest request) {
        Patient patient = patientService.updatePatient(
            id, request.bloodType(), request.allergies(),
            request.chronicConditions(), request.emergencyContactName(),
            request.emergencyContactPhone()
        );
        return ResponseEntity.ok(toResponse(patient));
    }

    /**
     * Soft deletes a patient.
     *
     * @param id the patient ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converts a Patient entity to a PatientResponse DTO.
     */
    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
            patient.getId(),
            patient.getUser().getId(),
            patient.getUser().getFirstName(),
            patient.getUser().getLastName(),
            patient.getUser().getEmail(),
            patient.getMedicalRecordNumber(),
            patient.getBloodType(),
            patient.getAllergies(),
            patient.getChronicConditions(),
            patient.getEmergencyContactName(),
            patient.getEmergencyContactPhone(),
            patient.getPrimaryDepartment() != null ? patient.getPrimaryDepartment().getId() : null,
            patient.getCreatedAt()
        );
    }
}
