package tn.esprit.pi.mapper.appointmentsMapper;

import org.springframework.stereotype.Component;

import tn.esprit.pi.dto.appointmentsDTO.AppointmentDTO;
import tn.esprit.pi.entity.appointments.Appointment;

@Component
public class AppointmentMapper {

    // Convert Entity to DTO (For returning data to the client)
    public AppointmentDTO toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return AppointmentDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                // Adjust getFirstName() / getLastName() based on your actual User entity fields
                .patientFullName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName())
                .doctorId(appointment.getDoctor().getId())
                .doctorFullName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .reasonForVisit(appointment.getReasonForVisit())
                .build();
    }

    // Note: Converting DTO to Entity (for creation/updates) usually happens
    // in the Service layer because you need to fetch the actual User entities
    // from the UserRepository using the patientId and doctorId.
}
