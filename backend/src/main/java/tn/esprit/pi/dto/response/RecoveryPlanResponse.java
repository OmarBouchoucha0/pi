package tn.esprit.pi.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.RecoveryStatus;

@Data
@Builder
public class RecoveryPlanResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long tenantId;
    private Long doctorId;
    private String doctorName;
    private LocalDate dischargeDate;
    private Integer plannedDurationDays;
    private Integer daysSinceDischarge;
    private String dischargeDiagnosis;
    private Map<String, Double> baselineVitals;
    private Map<String, Double[]> expectedCurve;
    private Map<String, Double> deviationTolerance;
    private Boolean active;
    private RecoveryStatus currentStatus;
    private Double returnScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
