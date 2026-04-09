package tn.esprit.pi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.enums.IntakeStatus;

@Entity
@Table(name = "intake_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntakeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private PatientPrescription prescription;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntakeStatus status = IntakeStatus.TAKEN;

    // Minutes late (negative = early)
    @Column(name = "delay_minutes")
    private Integer delayMinutes;

    @Column(name = "dose_taken")
    private Double doseTaken;
}
