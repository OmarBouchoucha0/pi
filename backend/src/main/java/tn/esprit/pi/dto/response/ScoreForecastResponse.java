package tn.esprit.pi.dto.response;

import java.time.LocalDate;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.RecoveryStatus;

@Data
@Builder
public class ScoreForecastResponse {

    private Long planId;
    private Long patientId;
    private String patientName;

    // Current state
    private Double currentScore;           // latest returnScore on the plan
    private RecoveryStatus currentStatus;
    private Integer daysSinceDischarge;
    private Integer plannedDurationDays;
    private Integer checkInsUsedForForecast; // how many data points the regression used

    // Regression metadata
    // slope > 0 means score is rising (getting worse)
    // slope < 0 means score is falling (getting better)
    // NOTE: slope is per check-in submission (not per calendar day),
    // because multiple check-ins can happen on day 0 during testing.
    private Double slopePerDay;            // score change per check-in submission

    // Trend label derived from slope
    private String trend;                  // WORSENING / IMPROVING / STABLE

    // Predicted score for each requested future day
    // key = "day_N" where N is days from today
    // values clamped to [0, 100]
    private Map<String, Double> forecastedScores;

    // Final recommendation based on the worst forecasted score
    private String forecastRecommendation; // MONITOR / CONTACT_PATIENT / RETURN_TO_HOSPITAL_LIKELY

    private LocalDate dischargeDate;
}
