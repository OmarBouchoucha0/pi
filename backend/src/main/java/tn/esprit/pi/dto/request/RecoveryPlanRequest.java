package tn.esprit.pi.dto.request;

import java.time.LocalDate;
import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecoveryPlanRequest {

    @NotNull(message = "patientId is required")
    private Long patientId;

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    private Long doctorId;

    @NotNull(message = "dischargeDate is required")
    private LocalDate dischargeDate;

    @NotNull(message = "plannedDurationDays is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer plannedDurationDays;

    private String dischargeDiagnosis;

    // Baseline vitals at discharge: { "TEMPERATURE": 38.2, "HEART_RATE": 95 }
    @NotNull(message = "baselineVitals is required")
    private Map<String, Double> baselineVitals;

    // Optional: doctor provides a custom expected curve per type
    // If not provided, the engine computes it automatically from baseline + clinical defaults
    private Map<String, Double[]> expectedCurve;

    // Optional: custom deviation tolerance per type (default: 15% for all)
    private Map<String, Double> deviationTolerance;
}
