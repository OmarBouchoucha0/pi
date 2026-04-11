package tn.esprit.pi.mapper.appointmentsMapper;

import org.springframework.stereotype.Component;

import tn.esprit.pi.dto.appointmentsDTO.DoctorAvailabilityDTO;
import tn.esprit.pi.entity.appointments.DoctorAvailability;

@Component
public class DoctorAvailabilityMapper {

    public DoctorAvailabilityDTO toDto(DoctorAvailability availability) {
        if (availability == null) {
            return null;
        }

        return DoctorAvailabilityDTO.builder()
                .id(availability.getId())
                .doctorId(availability.getDoctor().getId())
                .doctorFullName(availability.getDoctor().getFirstName() + " " + availability.getDoctor().getLastName())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .status(availability.getStatus())
                .build();
    }
}
