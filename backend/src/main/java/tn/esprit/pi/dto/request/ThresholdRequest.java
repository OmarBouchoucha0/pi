package tn.esprit.pi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.pi.enums.VitalType;

@Data
public class ThresholdRequest {

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "type is required")
    private VitalType type;

    private Double minValue;
    private Double maxValue;
    private Double criticalMin;
    private Double criticalMax;
}
