package tn.esprit.pi.dto.followup;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientProtocolResponse {
    private String id;
    private Long patientId;
    private String patientName;
    private String protocolId;
    private String protocolName;
    private Long tenantId;
    private String tenantName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double complianceScore;
    private Boolean riskFlag;
    private List<ExecutionResponse> executions;
}
