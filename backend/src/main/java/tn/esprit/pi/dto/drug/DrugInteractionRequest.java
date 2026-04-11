package tn.esprit.pi.dto.drug;

import lombok.Data;
import tn.esprit.pi.enums.drug.DrugSeverity;

@Data
public class DrugInteractionRequest {
    private Long drugAId;
    private Long drugBId;
    private DrugSeverity severity;
    private String interactionType;
    private String description;
    private String recommendation;
}
