package tn.esprit.pi.dto.intake;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.intake.IntakeStatus;

@Data
@Builder
public class IntakeLogResponse {

    private Long id;
    private Long prescriptionId;
    private String drugName;
    private String patientName;
    private LocalDateTime scheduledTime;
    private LocalDateTime takenAt;
    private IntakeStatus status;
    private Integer delayMinutes;
    private Double doseTaken;
}
