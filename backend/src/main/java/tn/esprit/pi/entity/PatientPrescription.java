package tn.esprit.pi.entity;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.PrescriptionStatus;

@Entity
@Table(name = "patient_prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientPrescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private MedicationCatalog drug;

    @Column(nullable = false)
    private Double dosage;

    @Column(nullable = false)
    private String frequency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "with_food")
    private Boolean withFood = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;
}
