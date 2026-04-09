package tn.esprit.pi.entity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;

@Entity
@Table(name = "patient_protocols")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProtocol {

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_patient_protocol_protocol"))
    private FollowupProtocol protocol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Computed: done steps / total steps (0.0 to 1.0)
    @Column(name = "compliance_score")
    private Double complianceScore = 0.0;

    // True when compliance is below threshold or delays detected
    @Column(name = "risk_flag")
    private Boolean riskFlag = false;

    @OneToMany(
            mappedBy = "patientProtocol",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ProtocolExecution> executions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.riskFlag == null) this.riskFlag = false;
        if (this.complianceScore == null) this.complianceScore = 0.0;
    }
}
