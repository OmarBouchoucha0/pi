package tn.esprit.pi.controller.vital;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Vital Parameters", description = "APIs for recording and managing patient vital parameters")
public class VitalParameterController {

    private final IVitalParameterService service;

    // ── CRUD ──────────────────────────────────────────────────────────

    // POST /pi/vitals
    @PostMapping
    @Operation(summary = "Record vital parameter", description = "Records a new vital parameter measurement for a patient")
    @ApiResponse(responseCode = "201", description = "Vital parameter recorded successfully")
    public ResponseEntity<VitalParameterResponse> add(@RequestBody @Valid VitalParameterRequest req) {
        return ResponseEntity.status(201).body(service.addVital(req));
    }

    // GET /pi/vitals
    @GetMapping
    @Operation(summary = "Get all vitals", description = "Retrieves all vital parameter measurements")
    @ApiResponse(responseCode = "200", description = "Vitals retrieved successfully")
    public ResponseEntity<List<VitalParameterResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /pi/vitals/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get vital by ID", description = "Retrieves a specific vital parameter by its ID")
    @ApiResponse(responseCode = "200", description = "Vital found")
    @ApiResponse(responseCode = "404", description = "Vital not found")
    public ResponseEntity<VitalParameterResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /pi/vitals/patient/{patientId}?tenantId=1
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get vitals by patient", description = "Retrieves all vital parameters for a specific patient")
    @ApiResponse(responseCode = "200", description = "Vitals retrieved successfully")
    public ResponseEntity<List<VitalParameterResponse>> getByPatient(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByPatient(patientId, tenantId));
    }

    // GET /pi/vitals/patient/{patientId}/type/{type}?tenantId=1
    @GetMapping("/patient/{patientId}/type/{type}")
    @Operation(summary = "Get vitals by type", description = "Retrieves vital parameters for a patient filtered by vital type")
    @ApiResponse(responseCode = "200", description = "Vitals retrieved successfully")
    public ResponseEntity<List<VitalParameterResponse>> getByType(
            @PathVariable Long patientId,
            @PathVariable VitalType type,
            @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByPatientAndType(patientId, tenantId, type));
    }

    // GET /pi/vitals/patient/{patientId}/latest?tenantId=1
    @GetMapping("/patient/{patientId}/latest")
    @Operation(summary = "Get latest vitals", description = "Retrieves the latest vital parameter of each type for a patient")
    @ApiResponse(responseCode = "200", description = "Latest vitals retrieved successfully")
    public ResponseEntity<List<VitalParameterResponse>> getLatest(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getLatestPerType(patientId, tenantId));
    }

    // PUT /pi/vitals/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update vital", description = "Updates an existing vital parameter measurement")
    @ApiResponse(responseCode = "200", description = "Vital updated successfully")
    @ApiResponse(responseCode = "404", description = "Vital not found")
    public ResponseEntity<VitalParameterResponse> update(
            @PathVariable Long id, @RequestBody @Valid VitalParameterRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // DELETE /pi/vitals/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vital", description = "Deletes a vital parameter measurement by its ID")
    @ApiResponse(responseCode = "204", description = "Vital deleted successfully")
    @ApiResponse(responseCode = "404", description = "Vital not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /pi/vitals/classify?value=39.5&tenantId=1&type=TEMPERATURE
    @GetMapping("/classify")
    @Operation(summary = "Classify vital value", description = "Classifies a vital parameter value based on tenant thresholds (normal/abnormal/critical)")
    @ApiResponse(responseCode = "200", description = "Classification computed successfully")
    public ResponseEntity<Map<String, String>> classify(
            @RequestParam Double value,
            @RequestParam Long tenantId,
            @RequestParam VitalType type) {
        return ResponseEntity.ok(Map.of("classification", service.classifyValue(value, tenantId, type)));
    }

    // ── NEW EASY APIs ─────────────────────────────────────────────────

    // GET /pi/vitals/patient/{patientId}/stats?tenantId=1&type=TEMPERATURE&from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
    @GetMapping("/patient/{patientId}/stats")
    @Operation(summary = "Get vital statistics", description = "Retrieves statistical data (min, max, avg, count) for a vital type over a date range")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
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
    @Operation(summary = "Get ward summary", description = "Retrieves a summary of all patients with their latest vitals per ward")
    @ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
    public ResponseEntity<List<VitalSummaryResponse>> getWardSummary(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getWardSummary(tenantId));
    }

    // GET /pi/vitals/critical-patients?tenantId=1
    @GetMapping("/critical-patients")
    @Operation(summary = "Get critical patients", description = "Retrieves all patients with at least one critical vital parameter")
    @ApiResponse(responseCode = "200", description = "Critical patients retrieved successfully")
    public ResponseEntity<List<VitalSummaryResponse>> getCriticalPatients(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getCriticalPatients(tenantId));
    }
}
