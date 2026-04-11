package tn.esprit.pi.entity.followup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "followup_protocols")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowupProtocol {

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "duration_days")
    private Integer durationDays;

    // For versioning support
    @Column(nullable = false)
    private Integer version;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "protocol",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ProtocolStep> steps = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        if (this.version == null) this.version = 1;
        if (this.isActive == null) this.isActive = false;
    }
}
