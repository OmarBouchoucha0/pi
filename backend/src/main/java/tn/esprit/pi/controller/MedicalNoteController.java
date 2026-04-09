package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.request.MedicalNoteRequest;
import tn.esprit.pi.dto.response.MedicalNoteResponse;
import tn.esprit.pi.enums.NoteType;
import tn.esprit.pi.service.vitals.IMedicalNoteService;

@RestController
@RequestMapping("/medical-notes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicalNoteController {

    private final IMedicalNoteService service;

    // POST /pi/medical-notes
    @PostMapping
    public ResponseEntity<MedicalNoteResponse> create(@RequestBody @Valid MedicalNoteRequest req) {
        return ResponseEntity.status(201).body(service.create(req));
    }

    // GET /pi/medical-notes
    @GetMapping
    public ResponseEntity<List<MedicalNoteResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /pi/medical-notes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MedicalNoteResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /pi/medical-notes/patient/{patientId}?tenantId=1
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalNoteResponse>> getByPatient(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByPatient(patientId, tenantId));
    }

    // GET /pi/medical-notes/patient/{patientId}/type/{type}?tenantId=1
    @GetMapping("/patient/{patientId}/type/{type}")
    public ResponseEntity<List<MedicalNoteResponse>> getByPatientAndType(
            @PathVariable Long patientId,
            @PathVariable NoteType type,
            @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByPatientAndType(patientId, tenantId, type));
    }

    // GET /pi/medical-notes/doctor/{doctorId}?tenantId=1
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalNoteResponse>> getByDoctor(
            @PathVariable Long doctorId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByDoctor(doctorId, tenantId));
    }

    // PUT /pi/medical-notes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MedicalNoteResponse> update(
            @PathVariable Long id, @RequestBody @Valid MedicalNoteRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // DELETE /pi/medical-notes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
