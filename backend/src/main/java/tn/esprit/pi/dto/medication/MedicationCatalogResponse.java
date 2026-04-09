package tn.esprit.pi.dto.medication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicationCatalogResponse {
    private Long id;
    private String name;
    private String description;
    private String molecule;
    private String category;
    private Double minDose;
    private Double maxDose;
    private String unit;
    private Integer frequencyPerDay;
}
