// response/ExecutionResponse.java
package tn.esprit.pi.dto.response;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.ActionType;
import tn.esprit.pi.enums.ExecutionStatus;
@Data
@Builder
public class
ExecutionResponse {
    private String id;
    private String stepId;
    private ActionType actionType;
    private Integer dayNumber;
    private ExecutionStatus status;
    private Integer delayMinutes;
    private LocalDateTime completedAt;
}
