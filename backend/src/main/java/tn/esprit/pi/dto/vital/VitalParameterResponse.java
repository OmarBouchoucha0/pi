package tn.esprit.pi.dto.vital;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VitalParameterResponse {
    private Long id;
    private Long patientId;
    private Long tenantId;
    private Object type;
    private Double value;
    private String unit;
    private Double normalizedValue;
    private Object status;
    private LocalDateTime recordedAt;
}
