package tn.esprit.pi.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.VitalStatus;
import tn.esprit.pi.enums.VitalType;

@Data
@Builder
public class VitalParameterResponse {
    private Long id;
    private Long patientId;
    private Long tenantId;
    private VitalType type;
    private Double value;
    private String unit;
    private Double normalizedValue;
    private VitalStatus status;
    private LocalDateTime recordedAt;
}
