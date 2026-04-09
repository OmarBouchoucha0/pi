package tn.esprit.pi.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.PatientStateEnum;

@Data
@Builder
public class AiExplanationResponse {
    private Long patientId;
    private PatientStateEnum currentState;
    private Double currentScore;
    private String explanation;
    private LocalDateTime generatedAt;
}
