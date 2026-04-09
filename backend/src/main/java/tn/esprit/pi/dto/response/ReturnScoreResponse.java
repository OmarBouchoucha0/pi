package tn.esprit.pi.dto.response;

import java.time.LocalDate;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.RecoveryStatus;

@Data
@Builder
public class ReturnScoreResponse {
    private Long planId;
    private Long patientId;
    private String patientName;
    private Double returnScore;             // 0-100
    private String returnRecommendation;    // MONITOR / CONTACT_PATIENT / RETURN_TO_HOSPITAL
    private RecoveryStatus recoveryStatus;
    private Integer daysSinceDischarge;
    private Integer plannedDurationDays;
    private Integer consecutiveDeterioratingDays;
    private Double averageDeviation;        // mean composite deviation across all check-ins
    private Map<String, Double> worstVitals; // which vitals deviate most
    private LocalDate dischargeDate;
}
