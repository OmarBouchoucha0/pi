package tn.esprit.pi.dto;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.DrugSeverity;

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
