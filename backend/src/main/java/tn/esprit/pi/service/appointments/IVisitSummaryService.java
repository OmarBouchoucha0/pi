package tn.esprit.pi.service.appointments;

import java.util.List;

import tn.esprit.pi.dto.appointmentsDTO.VisitSummaryDTO;

public interface IVisitSummaryService {
    VisitSummaryDTO createVisitSummary(VisitSummaryDTO dto);
    VisitSummaryDTO getVisitSummaryById(Long id);
    VisitSummaryDTO getVisitSummaryByAppointmentId(Long appointmentId);
    List<VisitSummaryDTO> getAllVisitSummaries();
    VisitSummaryDTO updateVisitSummary(Long id, VisitSummaryDTO dto);
    void deleteVisitSummary(Long id);
}
