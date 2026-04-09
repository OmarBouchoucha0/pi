package tn.esprit.pi.controller.vital;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.vital.VitalParameterRequest;
import tn.esprit.pi.dto.vital.VitalParameterResponse;
import tn.esprit.pi.dto.vital.VitalStatsResponse;
import tn.esprit.pi.dto.vital.VitalSummaryResponse;
import tn.esprit.pi.enums.vital.VitalType;
import tn.esprit.pi.service.vital.IVitalParameterService;

@RestController
@RequestMapping("/vitals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VitalParameterController {

    private final IVitalParameterService service;

    // ── CRUD ──────────────────────────────────────────────────────────

    // POST /pi/vitals
    @PostMapping
    public ResponseEntity<VitalParameterResponse> add(@RequestBody @Valid VitalParameterRequest req) {
        return ResponseEntity.status(201).body(service.addVital(req));
    }

    // GET /pi/vitals
    @GetMapping
    public ResponseEntity<List<VitalParameterResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /pi/vitals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VitalParameterResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /pi/vitals/patient/{patientId}?tenantId=1
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VitalParameterResponse>> getByPatient(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByPatient(patientId, tenantId));
    }

    // GET /pi/vitals/patient/{patientId}/type/{type}?tenantId=1
    @GetMapping("/patient/{patientId}/type/{type}")
    public ResponseEntity<List<VitalParameterResponse>> getByType(
            @PathVariable Long patientId,
            @PathVariable VitalType type,
            @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByPatientAndType(patientId, tenantId, type));
    }

    // GET /pi/vitals/patient/{patientId}/latest?tenantId=1
    @GetMapping("/patient/{patientId}/latest")
    public ResponseEntity<List<VitalParameterResponse>> getLatest(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getLatestPerType(patientId, tenantId));
    }

    // PUT /pi/vitals/{id}
    @PutMapping("/{id}")
    public ResponseEntity<VitalParameterResponse> update(
            @PathVariable Long id, @RequestBody @Valid VitalParameterRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // DELETE /pi/vitals/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /pi/vitals/classify?value=39.5&tenantId=1&type=TEMPERATURE
    @GetMapping("/classify")
    public ResponseEntity<Map<String, String>> classify(
            @RequestParam Double value,
            @RequestParam Long tenantId,
            @RequestParam VitalType type) {
        return ResponseEntity.ok(Map.of("classification", service.classifyValue(value, tenantId, type)));
    }

    // ── NEW EASY APIs ─────────────────────────────────────────────────

    // GET /pi/vitals/patient/{patientId}/stats?tenantId=1&type=TEMPERATURE&from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
    @GetMapping("/patient/{patientId}/stats")
    public ResponseEntity<VitalStatsResponse> getStats(
            @PathVariable Long patientId,
            @RequestParam Long tenantId,
            @RequestParam VitalType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(service.getStats(patientId, tenantId, type, from, to));
    }

    // GET /pi/vitals/summary?tenantId=1
    @GetMapping("/summary")
    public ResponseEntity<List<VitalSummaryResponse>> getWardSummary(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getWardSummary(tenantId));
    }

    // GET /pi/vitals/critical-patients?tenantId=1
    @GetMapping("/critical-patients")
    public ResponseEntity<List<VitalSummaryResponse>> getCriticalPatients(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getCriticalPatients(tenantId));
    }
}
