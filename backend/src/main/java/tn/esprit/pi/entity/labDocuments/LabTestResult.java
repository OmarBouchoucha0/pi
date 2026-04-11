package tn.esprit.pi.entity.labDocuments;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.User;

@Entity
@Table(name = "lab_test_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Optional link to the source document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private MedicalDocument sourceDocument;

    @Column(name = "test_name", nullable = false)
    private String testName;

    @Column(name = "test_value", nullable = false)
    private Double testValue;

    private String unit;

    @Column(name = "normal_range_min")
    private Double normalRangeMin;

    @Column(name = "normal_range_max")
    private Double normalRangeMax;

    @Column(name = "is_abnormal")
    @Builder.Default
    private Boolean isAbnormal = false;

    @CreationTimestamp
    @Column(name = "extracted_at", updatable = false)
    private LocalDateTime extractedAt;
}
