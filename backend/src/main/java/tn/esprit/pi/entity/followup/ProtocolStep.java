package tn.esprit.pi.entity.followup;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.enums.document.ActionType;

@Entity
@Table(name = "protocol_steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolStep {

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    // FK → followup_protocols.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_step_protocol"))
    @JsonIgnore
    private FollowupProtocol protocol;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(nullable = false)
    private Boolean mandatory;

    @Column(nullable = false)
    private Double weight;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}
