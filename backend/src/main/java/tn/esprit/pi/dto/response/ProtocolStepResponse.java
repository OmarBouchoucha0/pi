// response/ProtocolStepResponse.java
package tn.esprit.pi.dto.response;
import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.ActionType;

@Data
@Builder
public class ProtocolStepResponse {
    private String id;
    private Integer dayNumber;
    private ActionType actionType;
    private Boolean mandatory;
    private Double weight;
}
