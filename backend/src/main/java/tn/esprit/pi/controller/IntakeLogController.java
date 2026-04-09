package tn.esprit.pi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.IntakeLogRequest;
import tn.esprit.pi.dto.IntakeLogResponse;
import tn.esprit.pi.enums.IntakeStatus;
import tn.esprit.pi.service.IntakeLogService;

@RestController
@RequestMapping("/intake-logs")
@RequiredArgsConstructor
public class IntakeLogController {

    private final IntakeLogService intakeLogService;

    // POST /api/intake-logs
    @PostMapping
    public ResponseEntity<IntakeLogResponse> create(
            @RequestBody IntakeLogRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(intakeLogService.create(request));
    }

    // GET /api/intake-logs
    @GetMapping
    public ResponseEntity<List<IntakeLogResponse>> getAll() {
        return ResponseEntity.ok(intakeLogService.getAll());
    }

    // GET /api/intake-logs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<IntakeLogResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(intakeLogService.getById(id));
    }

    // GET /api/intake-logs/prescription/{prescriptionId}
    @GetMapping("/prescription/{prescriptionId}")
    public ResponseEntity<List<IntakeLogResponse>> getByPrescription(
            @PathVariable Long prescriptionId) {
        return ResponseEntity.ok(intakeLogService.getByPrescription(prescriptionId));
    }

    // GET /api/intake-logs/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<IntakeLogResponse>> getByStatus(
            @PathVariable IntakeStatus status) {
        return ResponseEntity.ok(intakeLogService.getByStatus(status));
    }

    // GET /api/intake-logs/prescription/{prescriptionId}/adherence
    @GetMapping("/prescription/{prescriptionId}/adherence")
    public ResponseEntity<Map<String, Object>> getAdherence(
            @PathVariable Long prescriptionId) {
        return ResponseEntity.ok(intakeLogService.getAdherenceStats(prescriptionId));
    }

    // DELETE /api/intake-logs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        intakeLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // PUT /intake-logs/{id}
    @PutMapping("/{id}")
    public ResponseEntity<IntakeLogResponse> update(
            @PathVariable Long id,
            @RequestBody IntakeLogRequest request) {
        return ResponseEntity.ok(intakeLogService.update(id, request));
    }
}
