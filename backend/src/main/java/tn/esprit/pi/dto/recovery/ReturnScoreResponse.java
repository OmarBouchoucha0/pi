package tn.esprit.pi.dto.recovery;

import java.time.LocalDate;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnScoreResponse {
    private Long planId;
    private Long patientId;
    private String patientName;
    private Double returnScore;
    private String returnRecommendation;
    private Object recoveryStatus;
    private Integer daysSinceDischarge;
    private Integer plannedDurationDays;
    private Integer consecutiveDeterioratingDays;
    private Double averageDeviation;
    private Map<String, Double> worstVitals;
    private LocalDate dischargeDate;
}
