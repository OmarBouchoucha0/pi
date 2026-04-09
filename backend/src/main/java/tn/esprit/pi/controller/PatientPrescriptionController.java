package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.PatientPrescriptionRequest;
import tn.esprit.pi.dto.PatientPrescriptionResponse;
import tn.esprit.pi.enums.PrescriptionStatus;
import tn.esprit.pi.service.PatientPrescriptionService;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PatientPrescriptionController {

    private final PatientPrescriptionService patientPrescriptionService;

    // POST /api/prescriptions
    @PostMapping
    public ResponseEntity<PatientPrescriptionResponse> create(
            @RequestBody PatientPrescriptionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patientPrescriptionService.create(request));
    }

    // GET /api/prescriptions
    @GetMapping
    public ResponseEntity<List<PatientPrescriptionResponse>> getAll() {
        return ResponseEntity.ok(patientPrescriptionService.getAll());
    }

    // GET /api/prescriptions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PatientPrescriptionResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(patientPrescriptionService.getById(id));
    }

    // GET /api/prescriptions/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientPrescriptionResponse>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(patientPrescriptionService.getByPatient(patientId));
    }

    // GET /api/prescriptions/patient/{patientId}/active
    @GetMapping("/patient/{patientId}/active")
    public ResponseEntity<List<PatientPrescriptionResponse>> getActiveByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(patientPrescriptionService.getActiveByPatient(patientId));
    }

    // GET /api/prescriptions/doctor/{doctorId}
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PatientPrescriptionResponse>> getByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(patientPrescriptionService.getByDoctor(doctorId));
    }

    // PATCH /api/prescriptions/{id}/status?status=COMPLETED
    @PatchMapping("/{id}/status")
    public ResponseEntity<PatientPrescriptionResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam PrescriptionStatus status) {
        return ResponseEntity.ok(patientPrescriptionService.updateStatus(id, status));
    }

    // DELETE /api/prescriptions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientPrescriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // PUT /prescriptions/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PatientPrescriptionResponse> update(
            @PathVariable Long id,
            @RequestBody PatientPrescriptionRequest request) {
        return ResponseEntity.ok(patientPrescriptionService.update(id, request));
    }
}
