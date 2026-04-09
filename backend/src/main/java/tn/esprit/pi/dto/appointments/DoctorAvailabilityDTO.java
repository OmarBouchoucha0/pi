package tn.esprit.pi.dto.appointmentsDTO;

import java.time.LocalDateTime;

import lombok.*;
import tn.esprit.pi.enums.user.AvailabilityStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAvailabilityDTO {

    private Long id;

    private Long doctorId;
    private String doctorFullName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AvailabilityStatus status;
}
