package tn.esprit.pi.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.PatientStateEnum;
import tn.esprit.pi.enums.Trend;

@Data
@Builder
public class PatientStateResponse {
    private Long id;
    private Long patientId;
    private Long tenantId;
    private PatientStateEnum state;
    private Double score;
    private Trend trend;
    private String reason;
    private LocalDateTime calculatedAt;
}
