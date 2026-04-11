package tn.esprit.pi.mapper.appointmentsMapper;

import org.springframework.stereotype.Component;

import tn.esprit.pi.dto.appointmentsDTO.VisitSummaryDTO;
import tn.esprit.pi.entity.appointments.VisitSummary;

@Component
public class VisitSummaryMapper {

    public VisitSummaryDTO toDto(VisitSummary summary) {
        if (summary == null) {
            return null;
        }

        return VisitSummaryDTO.builder()
                .id(summary.getId())
                .appointmentId(summary.getAppointment().getId())
                .diagnosisNotes(summary.getDiagnosisNotes())
                .treatmentPlan(summary.getTreatmentPlan())
                .loggedAt(summary.getLoggedAt())
                .build();
    }
}
