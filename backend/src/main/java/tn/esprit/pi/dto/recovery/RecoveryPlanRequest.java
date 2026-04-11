package tn.esprit.pi.dto.recovery;

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

    private Map<String, Double> baselineVitals;

    private Map<String, Double[]> expectedCurve;

    private Map<String, Double> deviationTolerance;
}
