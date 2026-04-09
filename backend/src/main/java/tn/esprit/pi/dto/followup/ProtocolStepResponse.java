package tn.esprit.pi.dto.followup;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolStepResponse {
    private String id;
    private Integer dayNumber;
    private Object actionType;
    private Boolean mandatory;
    private Double weight;
}
