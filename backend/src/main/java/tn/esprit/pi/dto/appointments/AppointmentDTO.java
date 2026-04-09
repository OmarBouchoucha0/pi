package tn.esprit.pi.dto.appointmentsDTO;

import java.time.LocalDateTime;

import lombok.*;
import tn.esprit.pi.enums.appointments.AppointmentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;

    // Patient info
    private Long patientId;
    private String patientFullName; // e.g., "John Doe"

    // Doctor info
    private Long doctorId;
    private String doctorFullName; // e.g., "Dr. Sarah Smith"

    // Appointment details
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private String reasonForVisit;
}
