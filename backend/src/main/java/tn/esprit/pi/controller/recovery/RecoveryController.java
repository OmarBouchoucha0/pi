package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.recovery.RecoveryCheckInRequest;
import tn.esprit.pi.dto.recovery.RecoveryCheckInResponse;
import tn.esprit.pi.dto.recovery.RecoveryPlanRequest;
import tn.esprit.pi.dto.recovery.RecoveryPlanResponse;
import tn.esprit.pi.dto.recovery.RecoveryTrajectoryResponse;
import tn.esprit.pi.dto.recovery.ReturnScoreResponse;
import tn.esprit.pi.service.recovery.IRecoveryService;

@RestController
@RequestMapping("/recovery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Recovery Management", description = "APIs for managing patient recovery plans and trajectories")
public class RecoveryController {

    private final IRecoveryService service;

    // ── Plan management ───────────────────────────────────────────────

    // POST /pi/recovery/plan
    // Called at discharge — creates the expected recovery curve for the patient
    @PostMapping("/plan")
    @Operation(summary = "Create recovery plan", description = "Creates a new recovery plan for a patient at discharge with expected recovery curve")
    @ApiResponse(responseCode = "201", description = "Recovery plan created successfully")
    public ResponseEntity<RecoveryPlanResponse> initPlan(
            @RequestBody @Valid RecoveryPlanRequest req) {
        return ResponseEntity.status(201).body(service.initPlan(req));
    }

    // GET /pi/recovery/plan/{planId}
    @GetMapping("/plan/{planId}")
    @Operation(summary = "Get recovery plan", description = "Retrieves a specific recovery plan by its ID")
    @ApiResponse(responseCode = "200", description = "Recovery plan found")
    public ResponseEntity<RecoveryPlanResponse> getPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(service.getPlan(planId));
    }

    // GET /pi/recovery/patient/{patientId}/active-plan?tenantId=1
    @GetMapping("/patient/{patientId}/active-plan")
    @Operation(summary = "Get active plan for patient", description = "Retrieves the currently active recovery plan for a patient")
    @ApiResponse(responseCode = "200", description = "Active plan found")
    @ApiResponse(responseCode = "404", description = "No active plan found")
    public ResponseEntity<RecoveryPlanResponse> getActivePlan(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getActivePlan(patientId, tenantId));
    }

    // PUT /pi/recovery/plan/{planId}
    // Doctor adjusts the expected curve or tolerance (e.g. patient is older, recovery slower)
    @PutMapping("/plan/{planId}")
    @Operation(summary = "Update recovery plan", description = "Updates an existing recovery plan with modified expected curve or tolerance")
    @ApiResponse(responseCode = "200", description = "Recovery plan updated successfully")
    @ApiResponse(responseCode = "404", description = "Recovery plan not found")
    public ResponseEntity<RecoveryPlanResponse> updatePlan(
            @PathVariable Long planId,
            @RequestBody @Valid RecoveryPlanRequest req) {
        return ResponseEntity.ok(service.updatePlan(planId, req));
    }

    // GET /pi/recovery/active?tenantId=1
    // All active recovery plans — doctor's overview sorted by return score desc
    @GetMapping("/active")
    @Operation(summary = "Get all active plans", description = "Retrieves all active recovery plans for a tenant sorted by return score")
    @ApiResponse(responseCode = "200", description = "Active plans retrieved successfully")
    public ResponseEntity<List<RecoveryPlanResponse>> getAllActivePlans(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getAllActivePlans(tenantId));
    }

    // ── Check-in ──────────────────────────────────────────────────────

    // POST /pi/recovery/checkin
    // Patient submits vitals from home — triggers the trajectory algorithm
    @PostMapping("/checkin")
    @Operation(summary = "Submit check-in", description = "Patient submits vitals from home, triggers trajectory algorithm to compute return score")
    @ApiResponse(responseCode = "201", description = "Check-in submitted successfully")
    public ResponseEntity<RecoveryCheckInResponse> submitCheckIn(
            @RequestBody @Valid RecoveryCheckInRequest req) {
        return ResponseEntity.status(201).body(service.submitCheckIn(req));
    }

    // GET /pi/recovery/plan/{planId}/checkins
    @GetMapping("/plan/{planId}/checkins")
    @Operation(summary = "Get check-ins for plan", description = "Retrieves all check-ins submitted for a specific recovery plan")
    @ApiResponse(responseCode = "200", description = "Check-ins retrieved successfully")
    public ResponseEntity<List<RecoveryCheckInResponse>> getCheckIns(@PathVariable Long planId) {
        return ResponseEntity.ok(service.getCheckIns(planId));
    }

    // ── Analytics ─────────────────────────────────────────────────────

    // GET /pi/recovery/patient/{patientId}/trajectory?tenantId=1
    // Full trajectory: expected vs actual per vital per day — ready for a chart
    @GetMapping("/patient/{patientId}/trajectory")
    @Operation(summary = "Get recovery trajectory", description = "Retrieves expected vs actual recovery trajectory per vital per day for charting")
    @ApiResponse(responseCode = "200", description = "Trajectory retrieved successfully")
    public ResponseEntity<RecoveryTrajectoryResponse> getTrajectory(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getTrajectory(patientId, tenantId));
    }

    // GET /pi/recovery/patient/{patientId}/return-score?tenantId=1
    // Computes the return-to-hospital score + recommendation
    @GetMapping("/patient/{patientId}/return-score")
    @Operation(summary = "Get return score", description = "Computes the return-to-hospital score and recommendation for a patient")
    @ApiResponse(responseCode = "200", description = "Return score computed successfully")
    public ResponseEntity<ReturnScoreResponse> getReturnScore(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getReturnScore(patientId, tenantId));
    }

    // GET /pi/recovery/at-risk?tenantId=1&minScore=60
    // All discharged patients whose return score exceeds the threshold — daily triage list
    @GetMapping("/at-risk")
    @Operation(summary = "Get at-risk patients", description = "Retrieves all discharged patients whose return score exceeds the threshold for daily triage")
    @ApiResponse(responseCode = "200", description = "At-risk patients retrieved successfully")
    public ResponseEntity<List<ReturnScoreResponse>> getAtRisk(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "60.0") Double minScore) {
        return ResponseEntity.ok(service.getAtRiskPatients(tenantId, minScore));
    }

}
