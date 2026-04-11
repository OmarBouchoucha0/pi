package tn.esprit.pi.controller.medication;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.medication.PatientPrescriptionRequest;
import tn.esprit.pi.dto.medication.PatientPrescriptionResponse;
import tn.esprit.pi.enums.medication.PrescriptionStatus;
import tn.esprit.pi.service.medication.PatientPrescriptionService;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Patient Prescriptions", description = "APIs for managing patient prescriptions")
public class PatientPrescriptionController {

    private final PatientPrescriptionService patientPrescriptionService;

    // POST /api/prescriptions
    @PostMapping
    @Operation(summary = "Create prescription", description = "Creates a new patient prescription")
    @ApiResponse(responseCode = "201", description = "Prescription created successfully")
    public ResponseEntity<PatientPrescriptionResponse> create(
            @RequestBody PatientPrescriptionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patientPrescriptionService.create(request));
    }

    // GET /api/prescriptions
    @GetMapping
    @Operation(summary = "Get all prescriptions", description = "Retrieves all patient prescriptions")
    @ApiResponse(responseCode = "200", description = "Prescriptions retrieved successfully")
    public ResponseEntity<List<PatientPrescriptionResponse>> getAll() {
        return ResponseEntity.ok(patientPrescriptionService.getAll());
    }

    // GET /api/prescriptions/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by ID", description = "Retrieves a specific prescription by its ID")
    @ApiResponse(responseCode = "200", description = "Prescription found")
    @ApiResponse(responseCode = "404", description = "Prescription not found")
    public ResponseEntity<PatientPrescriptionResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(patientPrescriptionService.getById(id));
    }

    // GET /api/prescriptions/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get prescriptions by patient", description = "Retrieves all prescriptions for a specific patient")
    @ApiResponse(responseCode = "200", description = "Prescriptions retrieved successfully")
    public ResponseEntity<List<PatientPrescriptionResponse>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(patientPrescriptionService.getByPatient(patientId));
    }

    // GET /api/prescriptions/patient/{patientId}/active
    @GetMapping("/patient/{patientId}/active")
    @Operation(summary = "Get active prescriptions", description = "Retrieves all active prescriptions for a patient")
    @ApiResponse(responseCode = "200", description = "Prescriptions retrieved successfully")
    public ResponseEntity<List<PatientPrescriptionResponse>> getActiveByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(patientPrescriptionService.getActiveByPatient(patientId));
    }

    // GET /api/prescriptions/doctor/{doctorId}
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get prescriptions by doctor", description = "Retrieves all prescriptions written by a specific doctor")
    @ApiResponse(responseCode = "200", description = "Prescriptions retrieved successfully")
    public ResponseEntity<List<PatientPrescriptionResponse>> getByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(patientPrescriptionService.getByDoctor(doctorId));
    }

    // PATCH /api/prescriptions/{id}/status?status=COMPLETED
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update prescription status", description = "Updates the status of a prescription")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "404", description = "Prescription not found")
    public ResponseEntity<PatientPrescriptionResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam PrescriptionStatus status) {
        return ResponseEntity.ok(patientPrescriptionService.updateStatus(id, status));
    }

    // DELETE /api/prescriptions/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete prescription", description = "Deletes a prescription")
    @ApiResponse(responseCode = "204", description = "Prescription deleted successfully")
    @ApiResponse(responseCode = "404", description = "Prescription not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientPrescriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // PUT /prescriptions/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update prescription", description = "Updates an existing prescription")
    @ApiResponse(responseCode = "200", description = "Prescription updated successfully")
    @ApiResponse(responseCode = "404", description = "Prescription not found")
    public ResponseEntity<PatientPrescriptionResponse> update(
            @PathVariable Long id,
            @RequestBody PatientPrescriptionRequest request) {
        return ResponseEntity.ok(patientPrescriptionService.update(id, request));
    }
}
