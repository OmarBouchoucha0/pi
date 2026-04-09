package tn.esprit.pi.dto.patient;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientStateResponse {
    private Long id;
    private Long patientId;
    private Long tenantId;
    private Object state;
    private Double score;
    private Object trend;
    private String reason;
    private LocalDateTime calculatedAt;
}
