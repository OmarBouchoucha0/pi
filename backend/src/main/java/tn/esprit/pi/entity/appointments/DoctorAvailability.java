package tn.esprit.pi.entity.appointments;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.user.AvailabilityStatus;

@Entity
@Table(name = "doctor_availabilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status = AvailabilityStatus.AVAILABLE;
}
