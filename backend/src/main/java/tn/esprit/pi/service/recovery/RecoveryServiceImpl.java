package tn.esprit.pi.service.recovery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.request.RecoveryCheckInRequest;
import tn.esprit.pi.dto.request.RecoveryPlanRequest;
import tn.esprit.pi.dto.response.*;
import tn.esprit.pi.entity.RecoveryCheckIn;
import tn.esprit.pi.entity.RecoveryPlan;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.DeviationLevel;
import tn.esprit.pi.enums.RecoveryStatus;
import tn.esprit.pi.enums.VitalType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.RecoveryCheckInRepository;
import tn.esprit.pi.repository.RecoveryPlanRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RecoveryServiceImpl implements IRecoveryService {

    private final RecoveryPlanRepository    planRepo;
    private final RecoveryCheckInRepository checkInRepo;
    private final UserRepository            userRepo;
    private final TenantRepository          tenantRepo;
    private final ObjectMapper              objectMapper;

    // ─── Vital weights for composite deviation score ───────────────────
    // Higher weight = more impact on the overall recovery score
    private static final Map<String, Double> VITAL_WEIGHTS = Map.of(
            "OXYGEN_SATURATION",        2.5,
            "HEART_RATE",               2.0,
            "BLOOD_PRESSURE_SYSTOLIC",  1.8,
            "BLOOD_PRESSURE_DIASTOLIC", 1.5,
            "RESPIRATORY_RATE",         1.5,
            "TEMPERATURE",              1.2,
            "BLOOD_GLUCOSE",            1.0
    );

    // ─── Default recovery curves per vital type ────────────────────────
    // The engine auto-generates a 7-day linear interpolation from baseline to target
    // if the doctor does not provide a custom curve.
    private static final Map<String, Double> RECOVERY_TARGETS = Map.of(
            "TEMPERATURE",              36.6,
            "HEART_RATE",               72.0,
            "BLOOD_PRESSURE_SYSTOLIC",  120.0,
            "BLOOD_PRESSURE_DIASTOLIC", 80.0,
            "OXYGEN_SATURATION",        98.0,
            "RESPIRATORY_RATE",         16.0,
            "BLOOD_GLUCOSE",            100.0
    );

    // Default tolerance: 15% deviation is acceptable
    private static final double DEFAULT_TOLERANCE = 0.15;

    // ══════════════════════════════════════════════════════════════════
    // PLAN MANAGEMENT
    // ══════════════════════════════════════════════════════════════════

    @Override
    public RecoveryPlanResponse initPlan(RecoveryPlanRequest req) {
        User patient = findUser(req.getPatientId());
        Tenant tenant = findTenant(req.getTenantId());
        User doctor = req.getDoctorId() != null ? findUser(req.getDoctorId()) : null;

        // Deactivate any existing active plan for this patient
        planRepo.deactivateExistingPlans(req.getPatientId(), req.getTenantId());

        // Build expected curve: if not provided, auto-compute linear interpolation
        Map<String, Double[]> curve = buildExpectedCurve(
                req.getBaselineVitals(),
                req.getExpectedCurve(),
                req.getPlannedDurationDays()
        );

        // Build default deviation tolerance if not provided
        Map<String, Double> tolerance = buildTolerance(req.getDeviationTolerance());

        RecoveryPlan plan = RecoveryPlan.builder()
                .patient(patient)
                .tenant(tenant)
                .doctor(doctor)
                .dischargeDate(req.getDischargeDate())
                .plannedDurationDays(req.getPlannedDurationDays())
                .dischargeDiagnosis(req.getDischargeDiagnosis())
                .baselineVitalsJson(toJson(req.getBaselineVitals()))
                .expectedCurveJson(toJson(curve))
                .deviationToleranceJson(toJson(tolerance))
                .active(true)
                .currentStatus(RecoveryStatus.ON_TRACK)
                .returnScore(0.0)
                .build();

        return toPlanResponse(planRepo.save(plan));
    }

    @Override
    @Transactional(readOnly = true)
    public RecoveryPlanResponse getPlan(Long planId) {
        return toPlanResponse(findPlan(planId));
    }

    @Override
    @Transactional(readOnly = true)
    public RecoveryPlanResponse getActivePlan(Long patientId, Long tenantId) {
        RecoveryPlan plan = planRepo.findByPatientIdAndTenantIdAndActiveTrue(patientId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active recovery plan for patient: " + patientId));
        return toPlanResponse(plan);
    }

    @Override
    public RecoveryPlanResponse updatePlan(Long planId, RecoveryPlanRequest req) {
        RecoveryPlan plan = findPlan(planId);

        if (req.getExpectedCurve() != null) {
            plan.setExpectedCurveJson(toJson(req.getExpectedCurve()));
        }
        if (req.getDeviationTolerance() != null) {
            plan.setDeviationToleranceJson(toJson(req.getDeviationTolerance()));
        }
        if (req.getDischargeDiagnosis() != null) {
            plan.setDischargeDiagnosis(req.getDischargeDiagnosis());
        }
        plan.setUpdatedAt(LocalDateTime.now());
        return toPlanResponse(planRepo.save(plan));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecoveryPlanResponse> getAllActivePlans(Long tenantId) {
        return planRepo.findByTenantIdAndActiveTrueOrderByReturnScoreDesc(tenantId)
                .stream().map(this::toPlanResponse).collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════
    // CHECK-IN SUBMISSION  —  the core algorithm
    // ══════════════════════════════════════════════════════════════════

    @Override
    public RecoveryCheckInResponse submitCheckIn(RecoveryCheckInRequest req) {
        RecoveryPlan plan = findPlan(req.getPlanId());
        User patient = findUser(req.getPatientId());

        // Compute which day this is since discharge
        int dayNumber = (int) ChronoUnit.DAYS.between(plan.getDischargeDate(), LocalDate.now());
        dayNumber = Math.max(0, Math.min(dayNumber, plan.getPlannedDurationDays() - 1));

        // Get the expected values for this specific day from the curve
        Map<String, Double[]> curve    = fromJson(plan.getExpectedCurveJson(), new TypeReference<>() {});
        Map<String, Double> expected   = extractDayFromCurve(curve, dayNumber);
        Map<String, Double> actual     = req.getActualVitals();
        Map<String, Double> tolerance  = fromJson(plan.getDeviationToleranceJson(), new TypeReference<>() {});

        // ── CORE: compute per-vital deviation ──────────────────────────
        // deviation(type) = |actual - expected| / expected
        // This is percentage deviation from the expected recovery value for day N
        Map<String, Double> deviations = computeDeviations(actual, expected);

        // ── CORE: weighted composite deviation ────────────────────────
        // compositeDeviation = Σ(deviation(t) * weight(t)) / Σ(weight(t))
        double compositeDeviation = computeCompositeDeviation(deviations);

        // ── CORE: classify deviation level ────────────────────────────
        DeviationLevel deviationLevel = classifyDeviation(compositeDeviation);

        // ── CORE: determine recovery status for this check-in ─────────
        RecoveryStatus checkInStatus = computeCheckInStatus(deviations, tolerance, compositeDeviation);

        // ── CORE: compute return score ─────────────────────────────────
        // returnScore combines:
        //   1. composite deviation weight (60%)
        //   2. days-since-discharge progression factor (20%)
        //   3. consecutive deteriorating streak penalty (20%)
        long streak = checkInRepo.countRecentDeterioratingCheckIns(plan.getId());
        double returnScore = computeReturnScore(compositeDeviation, dayNumber,
                plan.getPlannedDurationDays(), streak);

        // Persist check-in
        RecoveryCheckIn checkIn = RecoveryCheckIn.builder()
                .plan(plan)
                .patient(patient)
                .dayNumber(dayNumber)
                .actualVitalsJson(toJson(actual))
                .expectedVitalsJson(toJson(expected))
                .deviationsJson(toJson(deviations))
                .compositeDeviation(compositeDeviation)
                .returnScoreSnapshot(returnScore)
                .deviationLevel(deviationLevel)
                .recoveryStatus(checkInStatus)
                .patientNotes(req.getPatientNotes())
                .build();

        checkInRepo.save(checkIn);

        // Update the plan's current status and return score
        plan.setCurrentStatus(checkInStatus);
        plan.setReturnScore(returnScore);
        plan.setUpdatedAt(LocalDateTime.now());
        planRepo.save(plan);

        return toCheckInResponse(checkIn, actual, expected, deviations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecoveryCheckInResponse> getCheckIns(Long planId) {
        return checkInRepo.findByPlanIdOrderByDayNumberAsc(planId)
                .stream().map(ci -> toCheckInResponse(ci,
                        fromJson(ci.getActualVitalsJson(),   new TypeReference<>() {}),
                        fromJson(ci.getExpectedVitalsJson(), new TypeReference<>() {}),
                        fromJson(ci.getDeviationsJson(),     new TypeReference<>() {})))
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════
    // ANALYTICS
    // ══════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public RecoveryTrajectoryResponse getTrajectory(Long patientId, Long tenantId) {
        RecoveryPlan plan = planRepo.findByPatientIdAndTenantIdAndActiveTrue(patientId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("No active plan for patient: " + patientId));

        List<RecoveryCheckIn> checkIns = checkInRepo.findByPlanIdOrderByDayNumberAsc(plan.getId());
        int duration = plan.getPlannedDurationDays();

        Map<String, Double[]> expectedCurve = fromJson(plan.getExpectedCurveJson(), new TypeReference<>() {});

        // Build actualCurve: one entry per vital type, null for days with no check-in
        Map<String, Double[]> actualCurve = new LinkedHashMap<>();
        Double[] deviationByDay = new Double[duration];

        for (VitalType vt : VitalType.values()) {
            actualCurve.put(vt.name(), new Double[duration]);
        }

        for (RecoveryCheckIn ci : checkIns) {
            int day = ci.getDayNumber();
            if (day >= duration) continue;
            deviationByDay[day] = ci.getCompositeDeviation();
            Map<String, Double> actual = fromJson(ci.getActualVitalsJson(), new TypeReference<>() {});
            actual.forEach((type, value) -> {
                if (actualCurve.containsKey(type)) {
                    actualCurve.get(type)[day] = value;
                }
            });
        }

        int daysSince = (int) ChronoUnit.DAYS.between(plan.getDischargeDate(), LocalDate.now());
        User patient = plan.getPatient();

        return RecoveryTrajectoryResponse.builder()
                .planId(plan.getId())
                .patientId(patientId)
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .plannedDurationDays(duration)
                .daysSinceDischarge(daysSince)
                .currentStatus(plan.getCurrentStatus())
                .currentReturnScore(plan.getReturnScore())
                .expectedCurve(expectedCurve)
                .actualCurve(actualCurve)
                .deviationByDay(deviationByDay)
                .checkIns(checkIns.stream().map(ci -> toCheckInResponse(ci,
                        fromJson(ci.getActualVitalsJson(),   new TypeReference<>() {}),
                        fromJson(ci.getExpectedVitalsJson(), new TypeReference<>() {}),
                        fromJson(ci.getDeviationsJson(),     new TypeReference<>() {})))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnScoreResponse getReturnScore(Long patientId, Long tenantId) {
        RecoveryPlan plan = planRepo.findByPatientIdAndTenantIdAndActiveTrue(patientId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("No active plan for patient: " + patientId));

        long streak   = checkInRepo.countRecentDeterioratingCheckIns(plan.getId());
        double avgDev = checkInRepo.findAvgDeviationByPlan(plan.getId()).orElse(0.0);
        int daysSince = (int) ChronoUnit.DAYS.between(plan.getDischargeDate(), LocalDate.now());

        // Identify worst vitals: which type has highest average deviation
        List<RecoveryCheckIn> checkIns = checkInRepo.findByPlanIdOrderByDayNumberAsc(plan.getId());
        Map<String, Double> worstVitals = computeWorstVitals(checkIns);

        String recommendation = buildRecommendation(plan.getReturnScore(), streak);

        User patient = plan.getPatient();
        return ReturnScoreResponse.builder()
                .planId(plan.getId())
                .patientId(patientId)
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .returnScore(Math.round(plan.getReturnScore() * 100.0) / 100.0)
                .returnRecommendation(recommendation)
                .recoveryStatus(plan.getCurrentStatus())
                .daysSinceDischarge(daysSince)
                .plannedDurationDays(plan.getPlannedDurationDays())
                .consecutiveDeterioratingDays((int) streak)
                .averageDeviation(Math.round(avgDev * 10000.0) / 100.0)
                .worstVitals(worstVitals)
                .dischargeDate(plan.getDischargeDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnScoreResponse> getAtRiskPatients(Long tenantId, Double minScore) {
        double threshold = minScore != null ? minScore : 60.0;
        return planRepo.findAtRiskByTenant(tenantId, threshold)
                .stream()
                .map(plan -> getReturnScore(plan.getPatient().getId(), tenantId))
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════

    // ══════════════════════════════════════════════════════════════════
    // FORECAST
    // ══════════════════════════════════════════════════════════════════

    /**
     * Linear-regression forecast of the return score over the next horizonDays.
     *
     * HOW IT WORKS (simple words):
     *  1. Pull all check-ins for the active plan, ordered by dayNumber.
     *  2. Treat each check-in as a data point: x = dayNumber, y = returnScoreSnapshot.
     *  3. Fit a straight line through those points (least-squares regression).
     *     The line has a slope (score change per day) and an intercept.
     *  4. Plug future day numbers into the line to get predicted scores.
     *     Values are clamped to [0, 100].
     *
     * Minimum 2 check-ins needed. If fewer exist, we return current score flat.
     */
    @Override
    @Transactional(readOnly = true)
    public ScoreForecastResponse getScoreForecast(Long patientId, Long tenantId, int horizonDays) {

        RecoveryPlan plan = planRepo.findByPatientIdAndTenantIdAndActiveTrue(patientId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active recovery plan for patient: " + patientId));

        List<RecoveryCheckIn> checkIns =
                checkInRepo.findByPlanIdOrderByDayNumberAsc(plan.getId());

        int daysSince = (int) ChronoUnit.DAYS.between(plan.getDischargeDate(), LocalDate.now());
        User patient  = plan.getPatient();

        // Build x/y arrays for regression.
        // We use submission ORDER (0,1,2,...) as x — NOT dayNumber.
        // Reason: if multiple check-ins fall on the same day (e.g. all on day 0
        // during testing), dayNumber gives identical x values which makes the
        // regression denominator = 0 and the slope = 0 (flat line, wrong).
        // Using index always gives distinct x values, so the trend is always
        // computed correctly regardless of how many check-ins share the same day.
        double[] x = new double[checkIns.size()];
        for (int i = 0; i < checkIns.size(); i++) { x[i] = i; }
        double[] y = checkIns.stream()
                .mapToDouble(RecoveryCheckIn::getReturnScoreSnapshot).toArray();

        // Run regression -> [intercept, slope]
        double[] coeffs   = linearRegression(x, y);
        double intercept  = coeffs[0];
        double slope      = coeffs[1];

        // Project scores for each future check-in step.
        // x continues from where the last check-in left off.
        // e.g. if there were 5 check-ins (indices 0-4), the next ones are 5, 6, 7.
        Map<String, Double> forecasted = new LinkedHashMap<>();
        double worstForecast = plan.getReturnScore();
        int lastIndex = checkIns.size(); // next check-in would be at this index

        for (int d = 1; d <= horizonDays; d++) {
            double futureIndex = lastIndex + d - 1;
            double predicted   = intercept + slope * futureIndex;
            predicted = Math.max(0.0, Math.min(100.0, predicted));
            predicted = Math.round(predicted * 100.0) / 100.0;
            forecasted.put("day_" + d, predicted);
            if (predicted > worstForecast) worstForecast = predicted;
        }

        // Classify trend from slope
        String trend;
        if      (slope >  1.5) trend = "WORSENING";
        else if (slope < -1.5) trend = "IMPROVING";
        else                   trend = "STABLE";

        // Recommendation based on worst projected score
        String recommendation;
        if      (worstForecast >= 70) recommendation = "RETURN_TO_HOSPITAL_LIKELY";
        else if (worstForecast >= 40) recommendation = "CONTACT_PATIENT";
        else                          recommendation = "MONITOR";

        return ScoreForecastResponse.builder()
                .planId(plan.getId())
                .patientId(patientId)
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .currentScore(Math.round(plan.getReturnScore() * 100.0) / 100.0)
                .currentStatus(plan.getCurrentStatus())
                .daysSinceDischarge(daysSince)
                .plannedDurationDays(plan.getPlannedDurationDays())
                .checkInsUsedForForecast(checkIns.size())
                .slopePerDay(Math.round(slope * 1000.0) / 1000.0) // slope per check-in submission
                .trend(trend)
                .forecastedScores(forecasted)
                .forecastRecommendation(recommendation)
                .dischargeDate(plan.getDischargeDate())
                .build();
    }

    /**
     * Ordinary least-squares linear regression.
     * Returns double[]{intercept, slope} for the line y = intercept + slope*x.
     *
     * Formula:
     *   slope     = ( n*Σ(x*y) - Σx*Σy ) / ( n*Σ(x²) - (Σx)² )
     *   intercept = ( Σy - slope*Σx )    / n
     *
     * Edge cases:
     *   - 0 points  -> intercept=0, slope=0
     *   - 1 point   -> intercept=y[0], slope=0  (flat line through the single point)
     *   - all x equal (vertical line) -> slope=0, intercept=mean(y)
     */
    private double[] linearRegression(double[] x, double[] y) {
        int n = x.length;
        if (n == 0) return new double[]{0.0, 0.0};
        if (n == 1) return new double[]{y[0], 0.0};

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX  += x[i];
            sumY  += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        double denom = n * sumX2 - sumX * sumX;
        if (denom == 0) return new double[]{sumY / n, 0.0}; // all x identical

        double slope     = (n * sumXY - sumX * sumY) / denom;
        double intercept = (sumY - slope * sumX) / n;
        return new double[]{intercept, slope};
    }

    // CORE ALGORITHM METHODS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Auto-generate expected recovery curves using linear interpolation.
     * For each vital type: values go from baseline (day 0) to target (day N-1).
     * If doctor provided a custom curve for a type, use that instead.
     */
    private Map<String, Double[]> buildExpectedCurve(Map<String, Double> baseline,
                                                      Map<String, Double[]> doctorCurve,
                                                      int days) {
        Map<String, Double[]> result = new LinkedHashMap<>();
        for (VitalType vt : VitalType.values()) {
            String key = vt.name();
            if (doctorCurve != null && doctorCurve.containsKey(key)) {
                result.put(key, doctorCurve.get(key));
            } else {
                double start  = baseline.getOrDefault(key, RECOVERY_TARGETS.getOrDefault(key, 0.0));
                double target = RECOVERY_TARGETS.getOrDefault(key, start);
                Double[] curve = new Double[days];
                for (int d = 0; d < days; d++) {
                    // Linear interpolation: start + (target - start) * (d / (days - 1))
                    curve[d] = days > 1
                            ? start + (target - start) * ((double) d / (days - 1))
                            : target;
                    curve[d] = Math.round(curve[d] * 100.0) / 100.0;
                }
                result.put(key, curve);
            }
        }
        return result;
    }

    private Map<String, Double> buildTolerance(Map<String, Double> provided) {
        Map<String, Double> t = new LinkedHashMap<>();
        for (VitalType vt : VitalType.values()) {
            t.put(vt.name(), provided != null && provided.containsKey(vt.name())
                    ? provided.get(vt.name())
                    : DEFAULT_TOLERANCE);
        }
        return t;
    }

    private Map<String, Double> extractDayFromCurve(Map<String, Double[]> curve, int day) {
        Map<String, Double> result = new LinkedHashMap<>();
        curve.forEach((type, values) -> {
            int idx = Math.min(day, values.length - 1);
            result.put(type, values[idx]);
        });
        return result;
    }

    /**
     * Per-vital deviation = |actual - expected| / |expected|
     * Clamped to [0, 1] to avoid infinity on zero-expected edge cases.
     */
    private Map<String, Double> computeDeviations(Map<String, Double> actual,
                                                   Map<String, Double> expected) {
        Map<String, Double> deviations = new LinkedHashMap<>();
        expected.forEach((type, exp) -> {
            Double act = actual.get(type);
            if (act != null && exp != null && exp != 0.0) {
                double dev = Math.abs(act - exp) / Math.abs(exp);
                deviations.put(type, Math.min(dev, 1.0));
            }
        });
        return deviations;
    }

    /**
     * Composite deviation = Σ(deviation[t] * weight[t]) / Σ(weight[t])
     * Uses clinical weights — oxygen and heart rate matter more than glucose.
     */
    private double computeCompositeDeviation(Map<String, Double> deviations) {
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        for (Map.Entry<String, Double> e : deviations.entrySet()) {
            double w = VITAL_WEIGHTS.getOrDefault(e.getKey(), 1.0);
            weightedSum += e.getValue() * w;
            totalWeight += w;
        }
        return totalWeight > 0 ? weightedSum / totalWeight : 0.0;
    }

    private DeviationLevel classifyDeviation(double composite) {
        if (composite < 0.10) return DeviationLevel.NONE;
        if (composite < 0.25) return DeviationLevel.LOW;
        if (composite < 0.50) return DeviationLevel.MODERATE;
        if (composite < 0.75) return DeviationLevel.HIGH;
        return DeviationLevel.CRITICAL;
    }

    /**
     * Recovery status for this check-in:
     *   - ON_TRACK:      all vitals within tolerance
     *   - DELAYED:       some vitals exceed tolerance but composite < 0.40
     *   - DETERIORATING: composite >= 0.40 or any vital far exceeds tolerance
     */
    private RecoveryStatus computeCheckInStatus(Map<String, Double> deviations,
                                                 Map<String, Double> tolerance,
                                                 double composite) {
        long outOfTolerance = deviations.entrySet().stream()
                .filter(e -> e.getValue() > tolerance.getOrDefault(e.getKey(), DEFAULT_TOLERANCE))
                .count();

        if (composite >= 0.40 || outOfTolerance >= 3) return RecoveryStatus.DETERIORATING;
        if (outOfTolerance >= 1 || composite >= 0.15)  return RecoveryStatus.DELAYED;
        return RecoveryStatus.ON_TRACK;
    }

    /**
     * Return-to-hospital score  (0–100):
     *
     *   base     = compositeDeviation * 60      (max 60 pts — deviation from plan)
     *   progress = progressFactor * 20           (max 20 pts — are they worse late in recovery?)
     *   streak   = streakPenalty * 20            (max 20 pts — consecutive bad check-ins)
     *
     *   progressFactor = (dayNumber / plannedDays) * compositeDeviation
     *   streakPenalty  = min(streak / 3, 1.0)   — 3 bad in a row = max penalty
     */
    private double computeReturnScore(double compositeDeviation, int dayNumber,
                                       int plannedDays, long streak) {
        double base           = compositeDeviation * 60.0;
        double progressFactor = ((double) dayNumber / Math.max(plannedDays, 1)) * compositeDeviation;
        double progressScore  = progressFactor * 20.0;
        double streakPenalty  = Math.min((double) streak / 3.0, 1.0);
        double streakScore    = streakPenalty * 20.0;
        return Math.min(base + progressScore + streakScore, 100.0);
    }

    private String buildRecommendation(double returnScore, long streak) {
        if (returnScore >= 70 || streak >= 3) return "RETURN_TO_HOSPITAL";
        if (returnScore >= 40 || streak >= 2) return "CONTACT_PATIENT";
        return "MONITOR";
    }

    private Map<String, Double> computeWorstVitals(List<RecoveryCheckIn> checkIns) {
        Map<String, List<Double>> deviationsByType = new LinkedHashMap<>();
        for (RecoveryCheckIn ci : checkIns) {
            Map<String, Double> devs = fromJson(ci.getDeviationsJson(), new TypeReference<>() {});
            devs.forEach((type, dev) ->
                    deviationsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(dev));
        }
        Map<String, Double> avgByType = new LinkedHashMap<>();
        deviationsByType.forEach((type, devs) -> {
            double avg = devs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            avgByType.put(type, Math.round(avg * 10000.0) / 100.0);
        });
        return avgByType.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    // ══════════════════════════════════════════════════════════════════
    // MAPPERS & HELPERS
    // ══════════════════════════════════════════════════════════════════

    private RecoveryPlanResponse toPlanResponse(RecoveryPlan p) {
        int daysSince = (int) ChronoUnit.DAYS.between(p.getDischargeDate(), LocalDate.now());
        User patient  = p.getPatient();
        return RecoveryPlanResponse.builder()
                .id(p.getId())
                .patientId(patient.getId())
                .patientName(patient.getFirstName() + " " + patient.getLastName())
                .tenantId(p.getTenant().getId())
                .doctorId(p.getDoctor() != null ? p.getDoctor().getId() : null)
                .doctorName(p.getDoctor() != null
                        ? p.getDoctor().getFirstName() + " " + p.getDoctor().getLastName() : null)
                .dischargeDate(p.getDischargeDate())
                .plannedDurationDays(p.getPlannedDurationDays())
                .daysSinceDischarge(daysSince)
                .dischargeDiagnosis(p.getDischargeDiagnosis())
                .baselineVitals(fromJson(p.getBaselineVitalsJson(), new TypeReference<>() {}))
                .expectedCurve(fromJson(p.getExpectedCurveJson(), new TypeReference<>() {}))
                .deviationTolerance(fromJson(p.getDeviationToleranceJson(), new TypeReference<>() {}))
                .active(p.getActive())
                .currentStatus(p.getCurrentStatus())
                .returnScore(p.getReturnScore())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private RecoveryCheckInResponse toCheckInResponse(RecoveryCheckIn ci,
                                                       Map<String, Double> actual,
                                                       Map<String, Double> expected,
                                                       Map<String, Double> deviations) {
        return RecoveryCheckInResponse.builder()
                .id(ci.getId())
                .planId(ci.getPlan().getId())
                .patientId(ci.getPatient().getId())
                .dayNumber(ci.getDayNumber())
                .actualVitals(actual)
                .expectedVitals(expected)
                .deviations(deviations)
                .compositeDeviation(Math.round(ci.getCompositeDeviation() * 10000.0) / 100.0)
                .returnScoreSnapshot(Math.round(ci.getReturnScoreSnapshot() * 100.0) / 100.0)
                .deviationLevel(ci.getDeviationLevel())
                .recoveryStatus(ci.getRecoveryStatus())
                .patientNotes(ci.getPatientNotes())
                .submittedAt(ci.getSubmittedAt())
                .build();
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (Exception e) { throw new RuntimeException("JSON serialization error", e); }
    }

    private <T> T fromJson(String json, TypeReference<T> type) {
        if (json == null || json.isBlank()) return null;
        try { return objectMapper.readValue(json, type); }
        catch (Exception e) { throw new RuntimeException("JSON deserialization error", e); }
    }

    private RecoveryPlan findPlan(Long id) {
        return planRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RecoveryPlan not found: " + id));
    }

    private User findUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private Tenant findTenant(Long id) {
        return tenantRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));
    }
}
