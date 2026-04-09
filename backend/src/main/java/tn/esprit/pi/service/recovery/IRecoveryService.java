package tn.esprit.pi.service.recovery;

import java.util.List;

import tn.esprit.pi.dto.request.RecoveryCheckInRequest;
import tn.esprit.pi.dto.request.RecoveryPlanRequest;
import tn.esprit.pi.dto.response.*;

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

    // ── Forecast ──
    // Runs linear regression over existing check-in scores and projects
    // what the return score will be in the next `horizonDays` days.
    ScoreForecastResponse getScoreForecast(Long patientId, Long tenantId, int horizonDays);
}
