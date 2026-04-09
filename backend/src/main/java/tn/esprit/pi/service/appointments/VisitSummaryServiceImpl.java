package tn.esprit.pi.service.appointments;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.appointmentsDTO.VisitSummaryDTO;
import tn.esprit.pi.entity.appointments.Appointment;
import tn.esprit.pi.entity.appointments.VisitSummary;
import tn.esprit.pi.mapper.appointmentsMapper.VisitSummaryMapper;
import tn.esprit.pi.repository.appointments.AppointmentRepository;
import tn.esprit.pi.repository.appointments.VisitSummaryRepository;

@Service
@RequiredArgsConstructor
public class VisitSummaryServiceImpl implements IVisitSummaryService {

    private final VisitSummaryRepository visitSummaryRepository;
    private final VisitSummaryMapper visitSummaryMapper;
    private final AppointmentRepository appointmentRepository;

    @Override
    public VisitSummaryDTO createVisitSummary(VisitSummaryDTO dto) {
        // 1. Validate that the Appointment exists
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + dto.getAppointmentId()));

        // 2. Optional but recommended: Check if a summary already exists for this appointment
        if (visitSummaryRepository.findByAppointmentId(dto.getAppointmentId()).isPresent()) {
            throw new RuntimeException("A visit summary already exists for appointment ID: " + dto.getAppointmentId());
        }

        // 3. Build and save the entity
        VisitSummary summary = VisitSummary.builder()
                .appointment(appointment)
                .diagnosisNotes(dto.getDiagnosisNotes())
                .treatmentPlan(dto.getTreatmentPlan())
                .build();

        VisitSummary savedSummary = visitSummaryRepository.save(summary);
        return visitSummaryMapper.toDto(savedSummary);
    }

    @Override
    public VisitSummaryDTO getVisitSummaryById(Long id) {
        VisitSummary summary = visitSummaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit Summary not found with id: " + id));
        return visitSummaryMapper.toDto(summary);
    }

    @Override
    public VisitSummaryDTO getVisitSummaryByAppointmentId(Long appointmentId) {
        VisitSummary summary = visitSummaryRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Visit Summary not found for appointment id: " + appointmentId));
        return visitSummaryMapper.toDto(summary);
    }

    @Override
    public List<VisitSummaryDTO> getAllVisitSummaries() {
        return visitSummaryRepository.findAll().stream()
                .map(visitSummaryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VisitSummaryDTO updateVisitSummary(Long id, VisitSummaryDTO dto) {
        VisitSummary existingSummary = visitSummaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit Summary not found with id: " + id));

        // Update the text fields
        existingSummary.setDiagnosisNotes(dto.getDiagnosisNotes());
        existingSummary.setTreatmentPlan(dto.getTreatmentPlan());

        // Update Appointment if the ID changed (rare, but good to handle)
        if (dto.getAppointmentId() != null && !existingSummary.getAppointment().getId().equals(dto.getAppointmentId())) {
            Appointment newAppointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + dto.getAppointmentId()));
            existingSummary.setAppointment(newAppointment);
        }

        VisitSummary savedSummary = visitSummaryRepository.save(existingSummary);
        return visitSummaryMapper.toDto(savedSummary);
    }

    @Override
    public void deleteVisitSummary(Long id) {
        VisitSummary existingSummary = visitSummaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit Summary not found with id: " + id));
        visitSummaryRepository.delete(existingSummary);
    }
}
