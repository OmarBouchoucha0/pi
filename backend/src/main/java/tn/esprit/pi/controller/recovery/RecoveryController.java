package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class RecoveryController {

    private final IRecoveryService service;

    // ── Plan management ───────────────────────────────────────────────

    // POST /pi/recovery/plan
    // Called at discharge — creates the expected recovery curve for the patient
    @PostMapping("/plan")
    public ResponseEntity<RecoveryPlanResponse> initPlan(
            @RequestBody @Valid RecoveryPlanRequest req) {
        return ResponseEntity.status(201).body(service.initPlan(req));
    }

    // GET /pi/recovery/plan/{planId}
    @GetMapping("/plan/{planId}")
    public ResponseEntity<RecoveryPlanResponse> getPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(service.getPlan(planId));
    }

    // GET /pi/recovery/patient/{patientId}/active-plan?tenantId=1
    @GetMapping("/patient/{patientId}/active-plan")
    public ResponseEntity<RecoveryPlanResponse> getActivePlan(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getActivePlan(patientId, tenantId));
    }

    // PUT /pi/recovery/plan/{planId}
    // Doctor adjusts the expected curve or tolerance (e.g. patient is older, recovery slower)
    @PutMapping("/plan/{planId}")
    public ResponseEntity<RecoveryPlanResponse> updatePlan(
            @PathVariable Long planId,
            @RequestBody @Valid RecoveryPlanRequest req) {
        return ResponseEntity.ok(service.updatePlan(planId, req));
    }

    // GET /pi/recovery/active?tenantId=1
    // All active recovery plans — doctor's overview sorted by return score desc
    @GetMapping("/active")
    public ResponseEntity<List<RecoveryPlanResponse>> getAllActivePlans(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getAllActivePlans(tenantId));
    }

    // ── Check-in ──────────────────────────────────────────────────────

    // POST /pi/recovery/checkin
    // Patient submits vitals from home — triggers the trajectory algorithm
    @PostMapping("/checkin")
    public ResponseEntity<RecoveryCheckInResponse> submitCheckIn(
            @RequestBody @Valid RecoveryCheckInRequest req) {
        return ResponseEntity.status(201).body(service.submitCheckIn(req));
    }

    // GET /pi/recovery/plan/{planId}/checkins
    @GetMapping("/plan/{planId}/checkins")
    public ResponseEntity<List<RecoveryCheckInResponse>> getCheckIns(@PathVariable Long planId) {
        return ResponseEntity.ok(service.getCheckIns(planId));
    }

    // ── Analytics ─────────────────────────────────────────────────────

    // GET /pi/recovery/patient/{patientId}/trajectory?tenantId=1
    // Full trajectory: expected vs actual per vital per day — ready for a chart
    @GetMapping("/patient/{patientId}/trajectory")
    public ResponseEntity<RecoveryTrajectoryResponse> getTrajectory(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getTrajectory(patientId, tenantId));
    }

    // GET /pi/recovery/patient/{patientId}/return-score?tenantId=1
    // Computes the return-to-hospital score + recommendation
    @GetMapping("/patient/{patientId}/return-score")
    public ResponseEntity<ReturnScoreResponse> getReturnScore(
            @PathVariable Long patientId, @RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getReturnScore(patientId, tenantId));
    }

    // GET /pi/recovery/at-risk?tenantId=1&minScore=60
    // All discharged patients whose return score exceeds the threshold — daily triage list
    @GetMapping("/at-risk")
    public ResponseEntity<List<ReturnScoreResponse>> getAtRisk(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "60.0") Double minScore) {
        return ResponseEntity.ok(service.getAtRiskPatients(tenantId, minScore));
    }

}
