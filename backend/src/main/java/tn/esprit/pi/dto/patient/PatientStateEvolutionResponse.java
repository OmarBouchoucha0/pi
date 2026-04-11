package tn.esprit.pi.dto.patient;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientStateEvolutionResponse {
    private Long stateId;
    private Object state;
    private Double score;
    private Object trend;
    private LocalDateTime calculatedAt;
}
