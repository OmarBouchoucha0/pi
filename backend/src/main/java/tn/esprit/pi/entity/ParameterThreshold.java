package tn.esprit.pi.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.enums.VitalType;

@Entity
@Table(name = "parameter_thresholds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VitalType type;

    @Column(name = "min_value")
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "critical_min")
    private Double criticalMin;

    @Column(name = "critical_max")
    private Double criticalMax;
}
