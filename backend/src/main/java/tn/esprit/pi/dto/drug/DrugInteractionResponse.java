package tn.esprit.pi.dto.drug;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.drug.DrugSeverity;

@Data
@Builder
public class DrugInteractionResponse {
    private Long id;
    private Long drugAId;
    private String drugAName;
    private Long drugBId;
    private String drugBName;
    private DrugSeverity severity;
    private String interactionType;
    private String description;
    private String recommendation;
}
