package tn.esprit.pi.service.appointments;

import java.util.List;

import tn.esprit.pi.dto.appointmentsDTO.DoctorAvailabilityDTO;

public interface IDoctorAvailabilityService {
    DoctorAvailabilityDTO createAvailability(DoctorAvailabilityDTO dto);
    DoctorAvailabilityDTO getAvailabilityById(Long id);
    List<DoctorAvailabilityDTO> getAllAvailabilities();
    DoctorAvailabilityDTO updateAvailability(Long id, DoctorAvailabilityDTO dto);
    void deleteAvailability(Long id);
}
