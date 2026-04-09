package tn.esprit.pi.service.appointments;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.appointmentsDTO.AppointmentDTO;
import tn.esprit.pi.entity.appointments.Appointment;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.mapper.appointmentsMapper.AppointmentMapper;
import tn.esprit.pi.repository.appointments.AppointmentRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;

    @Override
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        // Fetch users using UUIDs
        User patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + dto.getPatientId()));

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + dto.getDoctorId()));

        // Build and save entity
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(dto.getStatus())
                .reasonForVisit(dto.getReasonForVisit())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return appointmentMapper.toDto(savedAppointment);
    }

    @Override
    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        return appointmentMapper.toDto(appointment);
    }

    @Override
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO updateAppointment(Long id, AppointmentDTO dto) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        // Update basic fields
        existingAppointment.setStartTime(dto.getStartTime());
        existingAppointment.setEndTime(dto.getEndTime());
        existingAppointment.setStatus(dto.getStatus());
        existingAppointment.setReasonForVisit(dto.getReasonForVisit());


        //Update Patient if the UUID changed
        if (dto.getPatientId() != null && !existingAppointment.getPatient().getId().equals(dto.getPatientId())) {
            User newPatient = userRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + dto.getPatientId()));
            existingAppointment.setPatient(newPatient);
        }

        // Update Doctor if the UUID changed
        if (dto.getDoctorId() != null && !existingAppointment.getDoctor().getId().equals(dto.getDoctorId())) {
            User newDoctor = userRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + dto.getDoctorId()));
            existingAppointment.setDoctor(newDoctor);
        }

        Appointment savedAppointment = appointmentRepository.save(existingAppointment);

        return appointmentMapper.toDto(savedAppointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointmentRepository.delete(existingAppointment);
    }
}
