package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.medicalNote.MedicalNoteRequest;
import tn.esprit.pi.dto.medicalNote.MedicalNoteResponse;
import tn.esprit.pi.enums.NoteType;
import tn.esprit.pi.service.vitals.IMedicalNoteService;

@RestController
@RequestMapping("/medical-notes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Medical Notes", description = "APIs for managing medical notes and clinical documentation")
public class MedicalNoteController {

    private final IMedicalNoteService service;

    // POST /pi/medical-notes
    @PostMapping
    @Operation(summary = "Create medical note", description = "Creates a new medical note")
    @ApiResponse(responseCode = "201", description = "Medical note created successfully")
    public ResponseEntity<MedicalNoteResponse> create(@RequestBody @Valid MedicalNoteRequest req) {
        return ResponseEntity.status(201).body(service.create(req));
    }

    // GET /pi/medical-notes
    @GetMapping
    @Operation(summary = "Get all medical notes", description = "Retrieves all medical notes")
    @ApiResponse(responseCode = "200", description = "Medical notes retrieved successfully")
    public ResponseEntity<List<MedicalNoteResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /pi/medical-notes/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get medical note by ID", description = "Retrieves a specific medical note by its ID")
    @ApiResponse(responseCode = "200", description = "Medical note found")
    @ApiResponse(responseCode = "404", description = "Medical note not found")
    public ResponseEntity<MedicalNoteResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /pi/medical-notes/patient/{patientId}?tenantId=1
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get medical notes by patient", description = "Retrieves all medical notes for a specific patient")
    @ApiResponse(responseCode = "200", description = "Medical notes retrieved successfully")
    public ResponseEntity<List<MedicalNoteResponse>> getByPatient(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByPatient(patientId, tenantId));
    }

    // GET /pi/medical-notes/patient/{patientId}/type/{type}?tenantId=1
    @GetMapping("/patient/{patientId}/type/{type}")
    @Operation(summary = "Get medical notes by type", description = "Retrieves medical notes for a patient filtered by note type")
    @ApiResponse(responseCode = "200", description = "Medical notes retrieved successfully")
    public ResponseEntity<List<MedicalNoteResponse>> getByPatientAndType(
            @PathVariable Long patientId,
            @PathVariable NoteType type,
            @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByPatientAndType(patientId, tenantId, type));
    }

    // GET /pi/medical-notes/doctor/{doctorId}?tenantId=1
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get medical notes by doctor", description = "Retrieves all medical notes written by a specific doctor")
    @ApiResponse(responseCode = "200", description = "Medical notes retrieved successfully")
    public ResponseEntity<List<MedicalNoteResponse>> getByDoctor(
            @PathVariable Long doctorId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByDoctor(doctorId, tenantId));
    }

    // PUT /pi/medical-notes/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update medical note", description = "Updates an existing medical note")
    @ApiResponse(responseCode = "200", description = "Medical note updated successfully")
    @ApiResponse(responseCode = "404", description = "Medical note not found")
    public ResponseEntity<MedicalNoteResponse> update(
            @PathVariable Long id, @RequestBody @Valid MedicalNoteRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // DELETE /pi/medical-notes/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medical note", description = "Deletes a medical note")
    @ApiResponse(responseCode = "204", description = "Medical note deleted successfully")
    @ApiResponse(responseCode = "404", description = "Medical note not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
