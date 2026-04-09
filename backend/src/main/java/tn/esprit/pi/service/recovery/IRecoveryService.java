package tn.esprit.pi.service.recovery;

import java.util.List;

import tn.esprit.pi.dto.recovery.RecoveryCheckInRequest;
import tn.esprit.pi.dto.recovery.RecoveryCheckInResponse;
import tn.esprit.pi.dto.recovery.RecoveryPlanRequest;
import tn.esprit.pi.dto.recovery.RecoveryPlanResponse;
import tn.esprit.pi.dto.recovery.RecoveryTrajectoryResponse;
import tn.esprit.pi.dto.recovery.ReturnScoreResponse;

public interface IRecoveryService {

    // ── Plan management ──
    RecoveryPlanResponse initPlan(RecoveryPlanRequest request);
    RecoveryPlanResponse getPlan(Long planId);
    RecoveryPlanResponse getActivePlan(Long patientId, Long tenantId);
    RecoveryPlanResponse updatePlan(Long planId, RecoveryPlanRequest request);
    List<RecoveryPlanResponse> getAllActivePlans(Long tenantId);

    // ── Check-in ──
    RecoveryCheckInResponse submitCheckIn(RecoveryCheckInRequest request);
    List<RecoveryCheckInResponse> getCheckIns(Long planId);

    // ── Analytics ──
    RecoveryTrajectoryResponse getTrajectory(Long patientId, Long tenantId);
    ReturnScoreResponse getReturnScore(Long patientId, Long tenantId);
    List<ReturnScoreResponse> getAtRiskPatients(Long tenantId, Double minScore);
}
