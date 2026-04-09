package tn.esprit.pi.entity.recovery;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.recovery.RecoveryStatus;

@Entity
@Table(name = "recovery_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Tenant tenant;

    // The supervising doctor who created/validated this plan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User doctor;

    // The date the patient was discharged — day 0 of recovery
    @Column(name = "discharge_date", nullable = false)
    private LocalDate dischargeDate;

    // Total planned recovery duration in days
    @Column(name = "planned_duration_days", nullable = false)
    private Integer plannedDurationDays;

    // Diagnosis at discharge (e.g. "Post-acute myocardial infarction")
    @Column(name = "discharge_diagnosis", columnDefinition = "TEXT")
    private String dischargeDiagnosis;

    // JSON: baseline vital values recorded at discharge
    // Format: { "TEMPERATURE": 38.2, "HEART_RATE": 95, ... }
    @Column(name = "baseline_vitals_json", columnDefinition = "TEXT", nullable = false)
    private String baselineVitalsJson;

    // JSON: expected daily targets per vital type
    // Format: { "TEMPERATURE": [38.0, 37.7, 37.4, 37.1, 36.9, 36.8, 36.7], ... }
    // Index = days since discharge. Length = plannedDurationDays.
    @Column(name = "expected_curve_json", columnDefinition = "TEXT", nullable = false)
    private String expectedCurveJson;

    // JSON: acceptable deviation % per vital type (doctor can customise)
    // Format: { "TEMPERATURE": 0.10, "HEART_RATE": 0.15, ... }
    @Column(name = "deviation_tolerance_json", columnDefinition = "TEXT")
    private String deviationToleranceJson;

    // Whether this plan is still active (only one active plan per patient)
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    @Builder.Default
    private RecoveryStatus currentStatus = RecoveryStatus.ON_TRACK;

    // Cumulative return-to-hospital score (0-100), recalculated on each check-in
    @Column(name = "return_score")
    @Builder.Default
    private Double returnScore = 0.0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
