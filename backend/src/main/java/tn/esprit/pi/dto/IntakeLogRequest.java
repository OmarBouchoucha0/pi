package tn.esprit.pi.dto;

import java.time.LocalDateTime;

import lombok.Data;
import tn.esprit.pi.enums.IntakeStatus;

@Data
public class IntakeLogRequest {
    private Long prescriptionId;
    private LocalDateTime scheduledTime;
    private LocalDateTime takenAt;
    private IntakeStatus status;
    private Integer delayMinutes;
    private Double doseTaken;
}
