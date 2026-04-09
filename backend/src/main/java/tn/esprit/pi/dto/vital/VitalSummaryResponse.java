package tn.esprit.pi.dto.vital;

import lombok.Builder;
import lombok.Data;

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
    private Object worstStatus;
}
