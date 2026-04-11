package tn.esprit.pi.controller.patient;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.patient.PatientStateEvolutionResponse;
import tn.esprit.pi.dto.patient.PatientStateResponse;
import tn.esprit.pi.service.patient.IPatientStateService;

@RestController
@RequestMapping("/patient-states")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Patient States", description = "APIs for managing patient health states and evolution")
public class PatientStateController {

    private final IPatientStateService service;

    // ── CRUD ──────────────────────────────────────────────────────────

    // POST /pi/patient-states/patient/{patientId}/recalculate?tenantId=1
    @PostMapping("/patient/{patientId}/recalculate")
    @Operation(summary = "Recalculate patient state", description = "Recalculates the current health state of a patient based on their latest vital parameters")
    @ApiResponse(responseCode = "200", description = "Patient state recalculated successfully")
    public ResponseEntity<PatientStateResponse> recalculate(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.recalculate(patientId, tenantId));
    }

    // GET /pi/patient-states
    @GetMapping
    @Operation(summary = "Get all patient states", description = "Retrieves all patient health states")
    @ApiResponse(responseCode = "200", description = "Patient states retrieved successfully")
    public ResponseEntity<List<PatientStateResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /pi/patient-states/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get patient state by ID", description = "Retrieves a specific patient health state by its ID")
    @ApiResponse(responseCode = "200", description = "Patient state found")
    @ApiResponse(responseCode = "404", description = "Patient state not found")
    public ResponseEntity<PatientStateResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /pi/patient-states/patient/{patientId}/latest?tenantId=1
    @GetMapping("/patient/{patientId}/latest")
    @Operation(summary = "Get latest patient state", description = "Retrieves the latest health state for a patient")
    @ApiResponse(responseCode = "200", description = "Latest patient state found")
    @ApiResponse(responseCode = "404", description = "No patient state found")
    public ResponseEntity<PatientStateResponse> getLatest(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getLatest(patientId, tenantId));
    }

    // GET /pi/patient-states/patient/{patientId}/history?tenantId=1
    @GetMapping("/patient/{patientId}/history")
    @Operation(summary = "Get patient state history", description = "Retrieves the health state history for a patient")
    @ApiResponse(responseCode = "200", description = "Patient state history retrieved successfully")
    public ResponseEntity<List<PatientStateResponse>> getHistory(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getHistory(patientId, tenantId));
    }

    // DELETE /pi/patient-states/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient state", description = "Deletes a patient health state by its ID")
    @ApiResponse(responseCode = "204", description = "Patient state deleted successfully")
    @ApiResponse(responseCode = "404", description = "Patient state not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── NEW EASY APIs ─────────────────────────────────────────────────

    // GET /pi/patient-states/patient/{patientId}/evolution?tenantId=1&days=7
    @GetMapping("/patient/{patientId}/evolution")
    @Operation(summary = "Get patient state evolution", description = "Retrieves the health state evolution over a specified number of days for charting")
    @ApiResponse(responseCode = "200", description = "Evolution data retrieved successfully")
    public ResponseEntity<List<PatientStateEvolutionResponse>> getEvolution(
            @PathVariable Long patientId,
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(service.getEvolution(patientId, tenantId, days));
    }

    // GET /pi/patient-states/patient/{patientId}/worsening-streak?tenantId=1
    @GetMapping("/patient/{patientId}/worsening-streak")
    @Operation(summary = "Get worsening streak", description = "Retrieves information about consecutive days of worsening health state")
    @ApiResponse(responseCode = "200", description = "Worsening streak data retrieved successfully")
    public ResponseEntity<Map<String, Object>> getWorseningStreak(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getWorseningStreak(patientId, tenantId));
    }

    // GET /pi/patient-states/at-risk?tenantId=1&minScore=4.0
    @GetMapping("/at-risk")
    @Operation(summary = "Get at-risk patients", description = "Retrieves all patients whose health state score exceeds the threshold")
    @ApiResponse(responseCode = "200", description = "At-risk patients retrieved successfully")
    public ResponseEntity<List<PatientStateResponse>> getAtRisk(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "4.0") Double minScore) {
        return ResponseEntity.ok(service.getAtRisk(tenantId, minScore));
    }
}
