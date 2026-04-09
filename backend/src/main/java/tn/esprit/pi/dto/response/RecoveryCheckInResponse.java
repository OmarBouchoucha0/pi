package tn.esprit.pi.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.DeviationLevel;
import tn.esprit.pi.enums.RecoveryStatus;

@Data
@Builder
public class RecoveryCheckInResponse {
    private Long id;
    private Long planId;
    private Long patientId;
    private Integer dayNumber;
    private Map<String, Double> actualVitals;
    private Map<String, Double> expectedVitals;
    private Map<String, Double> deviations;       // per-type deviation %
    private Double compositeDeviation;             // weighted overall deviation
    private Double returnScoreSnapshot;            // return score at this check-in
    private DeviationLevel deviationLevel;
    private RecoveryStatus recoveryStatus;
    private String patientNotes;
    private LocalDateTime submittedAt;
}
