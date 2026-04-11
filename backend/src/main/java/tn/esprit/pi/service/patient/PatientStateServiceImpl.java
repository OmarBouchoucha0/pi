package tn.esprit.pi.service.patient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.patient.PatientStateEvolutionResponse;
import tn.esprit.pi.dto.patient.PatientStateResponse;
import tn.esprit.pi.entity.patient.PatientState;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.entity.vital.ParameterThreshold;
import tn.esprit.pi.entity.vital.VitalParameter;
import tn.esprit.pi.enums.patient.PatientStateEnum;
import tn.esprit.pi.enums.patient.Trend;
import tn.esprit.pi.enums.vital.VitalType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.patient.PatientStateRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;
import tn.esprit.pi.repository.vital.ParameterThresholdRepository;
import tn.esprit.pi.repository.vital.VitalParameterRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientStateServiceImpl implements IPatientStateService {

    private final PatientStateRepository stateRepo;
    private final VitalParameterRepository vitalRepo;
    private final ParameterThresholdRepository thresholdRepo;
    private final UserRepository userRepo;
    private final TenantRepository tenantRepo;

    // ══════════════════════════════════════
    // CRUD
    // ══════════════════════════════════════

    @Override
    public PatientStateResponse recalculate(Long patientId, Long tenantId) {
        User patient = userRepo.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        Tenant tenant = tenantRepo.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantId));

        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<VitalParameter> recentVitals = vitalRepo
                .findByPatientIdAndTenantIdAndRecordedAtAfter(patientId, tenantId, since);

        if (recentVitals.isEmpty()) {
            return saveState(patient, tenant, PatientStateEnum.STABLE, 0.0,
                    Trend.STABLE, "No vitals recorded in the last 24 hours");
        }

        double totalWeightedScore = 0, totalWeight = 0;
        StringBuilder reasons = new StringBuilder();

        for (VitalParameter vp : recentVitals) {
            Optional<ParameterThreshold> opt = thresholdRepo.findByTenantIdAndType(tenantId, vp.getType());
            if (opt.isEmpty()) continue;
            double rawScore = computeRawScore(vp.getValue(), opt.get());
            double weight   = getWeight(vp.getType());
            totalWeightedScore += rawScore * weight;
            totalWeight        += weight;
            if (rawScore >= 7) reasons.append(vp.getType().name()).append(" critique, ");
            else if (rawScore >= 4) reasons.append(vp.getType().name()).append(" anormal, ");
        }

        double finalScore = totalWeight > 0 ? totalWeightedScore / totalWeight : 0;
        PatientStateEnum state = finalScore >= 7 ? PatientStateEnum.CRITICAL
                               : finalScore >= 4 ? PatientStateEnum.AT_RISK
                               : PatientStateEnum.STABLE;
        Trend trend = computeTrend(patientId, tenantId, finalScore);

        String reasonText = reasons.toString().isEmpty()
                ? "All parameters within normal range"
                : reasons.toString().replaceAll(", $", "");
        if (trend == Trend.WORSENING) reasonText += " - Condition worsening";
        if (trend == Trend.IMPROVING) reasonText += " - Condition improving";

        PatientStateResponse response = saveState(patient, tenant, state, finalScore, trend, reasonText);
        if (state == PatientStateEnum.CRITICAL) triggerCriticalAlert(patient, tenant, reasonText);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientStateResponse> getAll() {
        return stateRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientStateResponse getLatest(Long patientId, Long tenantId) {
        return toResponse(stateRepo.findTopByPatientIdAndTenantIdOrderByCalculatedAtDesc(patientId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("No state found for patient: " + patientId)));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientStateResponse getById(Long id) {
        return toResponse(stateRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PatientState not found: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientStateResponse> getHistory(Long patientId, Long tenantId) {
        return stateRepo.findByPatientIdAndTenantIdOrderByCalculatedAtDesc(patientId, tenantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        stateRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("PatientState not found: " + id));
        stateRepo.deleteById(id);
    }

    // ══════════════════════════════════════
    // NEW EASY APIs
    // ══════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<PatientStateEvolutionResponse> getEvolution(Long patientId, Long tenantId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return stateRepo.findEvolutionSince(patientId, tenantId, since)
                .stream()
                .map(ps -> PatientStateEvolutionResponse.builder()
                        .stateId(ps.getId())
                        .state(ps.getState())
                        .score(ps.getScore())
                        .trend(ps.getTrend())
                        .calculatedAt(ps.getCalculatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getWorseningStreak(Long patientId, Long tenantId) {
        List<PatientState> states = stateRepo.findAllByPatientAndTenantDesc(patientId, tenantId);

        int streak = 0;
        for (PatientState ps : states) {
            if (ps.getTrend() == Trend.WORSENING) streak++;
            else break;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("patientId", patientId);
        result.put("worseningStreak", streak);
        result.put("alert", streak >= 3);
        result.put("message", streak >= 3
                ? "ALERT: Patient has been worsening for " + streak + " consecutive evaluations"
                : streak > 0
                    ? "Patient has been worsening for " + streak + " evaluation(s)"
                    : "No consecutive worsening detected");
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientStateResponse> getAtRisk(Long tenantId, Double minScore) {
        double threshold = minScore != null ? minScore : 4.0;
        return stateRepo.findLatestByTenantAboveScore(tenantId, threshold)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ══════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════

    private double computeRawScore(Double value, ParameterThreshold t) {
        if (t.getCriticalMin() != null && value <= t.getCriticalMin()) return 10.0;
        if (t.getCriticalMax() != null && value >= t.getCriticalMax()) return 10.0;
        if (t.getMinValue()    != null && value <= t.getMinValue())    return 5.0;
        if (t.getMaxValue()    != null && value >= t.getMaxValue())    return 5.0;
        return 0.0;
    }

    private double getWeight(VitalType type) {
        return switch (type) {
            case OXYGEN_SATURATION        -> 2.5;
            case HEART_RATE               -> 2.0;
            case BLOOD_PRESSURE_SYSTOLIC  -> 1.8;
            case BLOOD_PRESSURE_DIASTOLIC -> 1.5;
            case RESPIRATORY_RATE         -> 1.5;
            case TEMPERATURE              -> 1.2;
            case BLOOD_GLUCOSE            -> 1.0;
        };
    }

    private Trend computeTrend(Long patientId, Long tenantId, double newScore) {
        List<PatientState> last2 = stateRepo.findTop2ByPatientIdAndTenantIdOrderByCalculatedAtDesc(patientId, tenantId);
        if (last2.isEmpty()) return Trend.STABLE;
        double delta = newScore - last2.get(0).getScore();
        if (delta >= 1.5) return Trend.WORSENING;
        if (delta <= -1.5) return Trend.IMPROVING;
        return Trend.STABLE;
    }

    private PatientStateResponse saveState(User patient, Tenant tenant, PatientStateEnum state,
                                            double score, Trend trend, String reason) {
        return toResponse(stateRepo.save(PatientState.builder()
                .patient(patient).tenant(tenant)
                .state(state).score(score).trend(trend).reason(reason)
                .build()));
    }

    private void triggerCriticalAlert(User patient, Tenant tenant, String reason) {
        System.out.println("[CRITICAL ALERT] Patient " + patient.getId()
                + " - Tenant " + tenant.getId() + " - " + reason);
    }

    private PatientStateResponse toResponse(PatientState ps) {
        return PatientStateResponse.builder()
                .id(ps.getId())
                .patientId(ps.getPatient().getId())
                .tenantId(ps.getTenant().getId())
                .state(ps.getState()).score(ps.getScore())
                .trend(ps.getTrend()).reason(ps.getReason())
                .calculatedAt(ps.getCalculatedAt())
                .build();
    }
}
