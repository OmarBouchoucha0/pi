package tn.esprit.pi.entity.activityLog.ActivityLog;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.enums.document.ActionType;
import tn.esprit.pi.enums.patient.RiskLevel;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User UUID or email
    @Column(name = "actor_identifier", nullable = false)
    private String actorIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    // e.g. "PATIENT", "VITAL_PARAMETER", "ALERT"
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    @Builder.Default
    private RiskLevel riskLevel = RiskLevel.LOW;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "activityLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityChange> changes;
}
