package tn.esprit.pi.dto;

import lombok.Data;

@Data
public class MedicationCatalogRequest {
    private String name;
    private String description;
    private String molecule;
    private String category;
    private Double minDose;
    private Double maxDose;
    private String unit;
    private Integer frequencyPerDay;
}
