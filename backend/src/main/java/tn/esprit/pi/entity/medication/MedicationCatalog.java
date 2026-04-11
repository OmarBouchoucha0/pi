package tn.esprit.pi.entity.medication;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medication_catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String molecule;
    private String category;

    @Column(name = "min_dose")
    private Double minDose;

    @Column(name = "max_dose")
    private Double maxDose;

    // Unit of dose: mg, ml, etc.
    private String unit;

    @Column(name = "frequency_per_day")
    private Integer frequencyPerDay;
}
