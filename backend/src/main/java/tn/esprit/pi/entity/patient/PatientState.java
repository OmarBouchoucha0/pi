package tn.esprit.pi.entity.patient;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.patient.PatientStateEnum;
import tn.esprit.pi.enums.patient.Trend;

@Entity
@Table(name = "patient_states")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientState {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientStateEnum state;

    @Column(nullable = false)
    private Double score;

    @Enumerated(EnumType.STRING)
    private Trend trend;

    @Column(length = 1000)
    private String reason;

    @CreationTimestamp
    @Column(name = "calculated_at", updatable = false)
    private LocalDateTime calculatedAt;
}
