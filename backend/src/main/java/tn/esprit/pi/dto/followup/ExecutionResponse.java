package tn.esprit.pi.dto.followup;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecutionResponse {
    private String id;
    private String stepId;
    private Object actionType;
    private Integer dayNumber;
    private Object status;
    private Integer delayMinutes;
    private LocalDateTime completedAt;
}
