package tn.esprit.pi.dto.recovery;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.recovery.DeviationLevel;
import tn.esprit.pi.enums.recovery.RecoveryStatus;

@Data
@Builder
public class RecoveryCheckInResponse {
    private Long id;
    private Long planId;
    private Long patientId;
    private Integer dayNumber;
    private Map<String, Double> actualVitals;
    private Map<String, Double> expectedVitals;
    private Map<String, Double> deviations;
    private Double compositeDeviation;
    private Double returnScoreSnapshot;
    private DeviationLevel deviationLevel;
    private RecoveryStatus recoveryStatus;
    private String patientNotes;
    private LocalDateTime submittedAt;
}
