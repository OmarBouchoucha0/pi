package tn.esprit.pi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.pi.enums.VitalType;

@Data
public class VitalParameterRequest {

    @NotNull(message = "patientId is required")
    private Long patientId;

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "type is required")
    private VitalType type;

    @NotNull(message = "value is required")
    private Double value;

    private String unit;
}
