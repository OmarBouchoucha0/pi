package tn.esprit.pi.dto;

import lombok.Data;
import tn.esprit.pi.enums.AlertSeverity;

@Data
public class AlertRequest {
    private Long patientId;
    private Long tenantId;
    private String type;
    private String title;
    private String description;
    private AlertSeverity severity;
    private Integer priorityScore;
    private String groupKey;
}
