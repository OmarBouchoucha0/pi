package tn.esprit.pi.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.AlertSeverity;
import tn.esprit.pi.enums.AlertStatus;

@Data
@Builder
public class AlertResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long tenantId;
    private String type;
    private String title;
    private String description;
    private AlertSeverity severity;
    private Integer priorityScore;
    private String groupKey;
    private Integer occurrenceCount;
    private AlertStatus status;
    private Integer escalationLevel;
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;
}
