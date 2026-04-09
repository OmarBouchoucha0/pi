package tn.esprit.pi.dto.appointmentsDTO;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitSummaryDTO {

    private Long id;

    // Link to the specific appointment
    private Long appointmentId;

    private String diagnosisNotes;
    private String treatmentPlan;
    private LocalDateTime loggedAt;
}
