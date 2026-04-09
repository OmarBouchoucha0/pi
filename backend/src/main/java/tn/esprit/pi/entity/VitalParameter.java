package tn.esprit.pi.entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.VitalStatus;
import tn.esprit.pi.enums.VitalType;

@Entity
@Table(name = "vital_parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalParameter {

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
    private VitalType type;

    @Column(nullable = false)
    private Double value;

    @Column(length = 20)
    private String unit;

    @Column(name = "normalized_value")
    private Double normalizedValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VitalStatus status;

    @CreationTimestamp
    @Column(name = "recorded_at", updatable = false)
    private LocalDateTime recordedAt;
}
