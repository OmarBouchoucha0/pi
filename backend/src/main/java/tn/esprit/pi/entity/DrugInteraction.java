package tn.esprit.pi.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.enums.DrugSeverity;

@Entity
@Table(name = "drug_interactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_a_id", nullable = false)
    private MedicationCatalog drugA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_b_id", nullable = false)
    private MedicationCatalog drugB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrugSeverity severity;

    @Column(name = "interaction_type")
    private String interactionType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String recommendation;
}
