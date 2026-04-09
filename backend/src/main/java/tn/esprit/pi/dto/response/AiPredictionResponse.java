package tn.esprit.pi.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiPredictionResponse {
    private Long patientId;
    private String prediction;
    private String confidence;
    private String reasoning;
    private String recommendedActions;
    private LocalDateTime generatedAt;
}
