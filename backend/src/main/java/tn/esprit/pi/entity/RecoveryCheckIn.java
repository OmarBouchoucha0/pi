package tn.esprit.pi.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.DeviationLevel;
import tn.esprit.pi.enums.RecoveryStatus;

@Entity
@Table(name = "recovery_checkins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RecoveryPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User patient;

    // Day number since discharge (computed: checkin date - discharge date)
    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    // JSON: actual vitals submitted by the patient at this check-in
    // Format: { "TEMPERATURE": 37.9, "HEART_RATE": 88, ... }
    @Column(name = "actual_vitals_json", columnDefinition = "TEXT", nullable = false)
    private String actualVitalsJson;

    // JSON: expected values for this specific day (copied from plan curve at dayNumber)
    // Format: { "TEMPERATURE": 37.4, "HEART_RATE": 80, ... }
    @Column(name = "expected_vitals_json", columnDefinition = "TEXT", nullable = false)
    private String expectedVitalsJson;

    // JSON: deviation % per vital type (actual vs expected)
    // Format: { "TEMPERATURE": 0.135, "HEART_RATE": 0.10 }
    @Column(name = "deviations_json", columnDefinition = "TEXT", nullable = false)
    private String deviationsJson;

    // Weighted composite deviation score for this check-in (0.0 = perfect, 1.0 = max deviation)
    @Column(name = "composite_deviation", nullable = false)
    private Double compositeDeviation;

    // Return-to-hospital score at this check-in snapshot (0-100)
    @Column(name = "return_score_snapshot", nullable = false)
    private Double returnScoreSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "deviation_level", nullable = false)
    private DeviationLevel deviationLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "recovery_status", nullable = false)
    private RecoveryStatus recoveryStatus;

    // Patient's subjective feeling (optional, submitted alongside vitals)
    @Column(name = "patient_notes", columnDefinition = "TEXT")
    private String patientNotes;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;
}
