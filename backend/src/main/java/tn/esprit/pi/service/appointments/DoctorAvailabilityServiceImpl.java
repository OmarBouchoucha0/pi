package tn.esprit.pi.service.appointments;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.appointmentsDTO.DoctorAvailabilityDTO;
import tn.esprit.pi.entity.appointments.DoctorAvailability;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.mapper.appointmentsMapper.DoctorAvailabilityMapper;
import tn.esprit.pi.repository.appointments.DoctorAvailabilityRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl implements IDoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorAvailabilityMapper availabilityMapper;
    private final UserRepository userRepository;

    @Override
    public DoctorAvailabilityDTO createAvailability(DoctorAvailabilityDTO dto) {
        // Fetch the doctor using standard Long ID
        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + dto.getDoctorId()));

        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(doctor)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(dto.getStatus() != null ? dto.getStatus() : tn.esprit.pi.enums.user.AvailabilityStatus.AVAILABLE)
                .build();

        DoctorAvailability savedAvailability = availabilityRepository.save(availability);
        return availabilityMapper.toDto(savedAvailability);
    }

    @Override
    public DoctorAvailabilityDTO getAvailabilityById(Long id) {
        DoctorAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found with id: " + id));
        return availabilityMapper.toDto(availability);
    }

    @Override
    public List<DoctorAvailabilityDTO> getAllAvailabilities() {
        return availabilityRepository.findAll().stream()
                .map(availabilityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorAvailabilityDTO updateAvailability(Long id, DoctorAvailabilityDTO dto) {
        DoctorAvailability existingAvailability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found with id: " + id));

        // Update times and status
        existingAvailability.setStartTime(dto.getStartTime());
        existingAvailability.setEndTime(dto.getEndTime());
        if (dto.getStatus() != null) {
            existingAvailability.setStatus(dto.getStatus());
        }

        // Update Doctor if the ID changed
        if (dto.getDoctorId() != null && !existingAvailability.getDoctor().getId().equals(dto.getDoctorId())) {
            User newDoctor = userRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + dto.getDoctorId()));
            existingAvailability.setDoctor(newDoctor);
        }

        DoctorAvailability savedAvailability = availabilityRepository.save(existingAvailability);
        return availabilityMapper.toDto(savedAvailability);
    }

    @Override
    public void deleteAvailability(Long id) {
        DoctorAvailability existingAvailability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found with id: " + id));
        availabilityRepository.delete(existingAvailability);
    }
}
