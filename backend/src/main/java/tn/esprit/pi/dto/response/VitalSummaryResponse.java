package tn.esprit.pi.dto.response;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.VitalStatus;

@Data
@Builder
public class VitalSummaryResponse {
    private Long patientId;
    private String patientFirstName;
    private String patientLastName;
    private Long totalVitals;
    private Long normalCount;
    private Long warningCount;
    private Long criticalCount;
    private VitalStatus worstStatus;
}
