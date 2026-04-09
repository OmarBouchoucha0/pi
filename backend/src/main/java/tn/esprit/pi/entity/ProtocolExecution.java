package tn.esprit.pi.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.enums.ExecutionStatus;

@Entity

@Table(name = "protocol_execution")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolExecution {

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    // FK → patient_protocols.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_protocol_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_execution_patient_protocol"))
    @JsonIgnore
    private PatientProtocol patientProtocol;

    // FK → protocol_steps.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_execution_step"))
    private ProtocolStep step;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @Column(name = "delay_minutes")
    private Integer delayMinutes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}
