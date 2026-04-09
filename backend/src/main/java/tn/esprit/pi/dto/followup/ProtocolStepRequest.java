package tn.esprit.pi.dto.followup;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.pi.enums.document.ActionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolStepRequest {

    @NotNull
    @Min(1)
    private Integer dayNumber;

    @NotNull
    private ActionType actionType;

    @NotNull
    private Boolean mandatory;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double weight;
}
