package tn.esprit.pi.entity.ActivityLog;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activity_changes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private ActivityLog activityLog;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
}
