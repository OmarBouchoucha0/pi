package tn.esprit.pi.controller.patient;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.patient.PatientStateEvolutionResponse;
import tn.esprit.pi.dto.patient.PatientStateResponse;
import tn.esprit.pi.service.patient.IPatientStateService;

@RestController
@RequestMapping("/patient-states")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientStateController {

    private final IPatientStateService service;

    // ── CRUD ──────────────────────────────────────────────────────────

    // POST /pi/patient-states/patient/{patientId}/recalculate?tenantId=1
    @PostMapping("/patient/{patientId}/recalculate")
    public ResponseEntity<PatientStateResponse> recalculate(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.recalculate(patientId, tenantId));
    }

    // GET /pi/patient-states
    @GetMapping
    public ResponseEntity<List<PatientStateResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /pi/patient-states/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PatientStateResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /pi/patient-states/patient/{patientId}/latest?tenantId=1
    @GetMapping("/patient/{patientId}/latest")
    public ResponseEntity<PatientStateResponse> getLatest(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getLatest(patientId, tenantId));
    }

    // GET /pi/patient-states/patient/{patientId}/history?tenantId=1
    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<PatientStateResponse>> getHistory(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getHistory(patientId, tenantId));
    }

    // DELETE /pi/patient-states/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── NEW EASY APIs ─────────────────────────────────────────────────

    // GET /pi/patient-states/patient/{patientId}/evolution?tenantId=1&days=7
    @GetMapping("/patient/{patientId}/evolution")
    public ResponseEntity<List<PatientStateEvolutionResponse>> getEvolution(
            @PathVariable Long patientId,
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(service.getEvolution(patientId, tenantId, days));
    }

    // GET /pi/patient-states/patient/{patientId}/worsening-streak?tenantId=1
    @GetMapping("/patient/{patientId}/worsening-streak")
    public ResponseEntity<Map<String, Object>> getWorseningStreak(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getWorseningStreak(patientId, tenantId));
    }

    // GET /pi/patient-states/at-risk?tenantId=1&minScore=4.0
    @GetMapping("/at-risk")
    public ResponseEntity<List<PatientStateResponse>> getAtRisk(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "4.0") Double minScore) {
        return ResponseEntity.ok(service.getAtRisk(tenantId, minScore));
    }
}
