package tn.esprit.pi.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientProtocolResponse {
    private String id;
    private Long patientId;          // ← juste l'ID
    private String patientName;      // ← prénom + nom
    private String protocolId;
    private String protocolName;
    private Long tenantId;           // ← juste l'ID
    private String tenantName;       // ← nom du tenant
    private LocalDate startDate;
    private LocalDate endDate;
    private Double complianceScore;
    private Boolean riskFlag;
    private List<ExecutionResponse> executions;
}
