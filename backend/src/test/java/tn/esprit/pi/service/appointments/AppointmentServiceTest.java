package tn.esprit.pi.service.appointments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.pi.dto.appointmentsDTO.AppointmentDTO;
import tn.esprit.pi.entity.appointments.Appointment;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.appointments.AppointmentStatus;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.mapper.appointmentsMapper.AppointmentMapper;
import tn.esprit.pi.repository.appointments.AppointmentRepository;
import tn.esprit.pi.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User patient;
    private User doctor;
    private Appointment appointment;
    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        patient = User.builder().id(1L).firstName("John").lastName("Doe").email("john@test.com").build();
        doctor = User.builder().id(2L).firstName("Dr. Smith").lastName("Smith").email("smith@test.com").build();

        appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .startTime(LocalDateTime.of(2024, 6, 10, 9, 0))
                .endTime(LocalDateTime.of(2024, 6, 10, 9, 30))
                .status(AppointmentStatus.SCHEDULED)
                .reasonForVisit("Checkup")
                .build();

        appointmentDTO = AppointmentDTO.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(2L)
                .startTime(LocalDateTime.of(2024, 6, 10, 9, 0))
                .endTime(LocalDateTime.of(2024, 6, 10, 9, 30))
                .status(AppointmentStatus.SCHEDULED)
                .reasonForVisit("Checkup")
                .build();
    }

    @Test
    void createAppointment_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.createAppointment(appointmentDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_patientNotFound() {
        AppointmentDTO dto = AppointmentDTO.builder()
                .patientId(99L)
                .doctorId(2L)
                .build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void createAppointment_doctorNotFound() {
        AppointmentDTO dto = AppointmentDTO.builder()
                .patientId(1L)
                .doctorId(99L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Doctor not found");
    }

    @Test
    void getAppointmentById_found() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDTO);

        AppointmentDTO result = appointmentService.getAppointmentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getAppointmentById_notFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getAppointmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment not found");
    }

    @Test
    void getAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(appointmentMapper.toDto(appointment)).thenReturn(appointmentDTO);

        List<AppointmentDTO> result = appointmentService.getAllAppointments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void updateAppointment_success() {
        AppointmentDTO updateDTO = AppointmentDTO.builder()
                .patientId(1L)
                .doctorId(2L)
                .startTime(LocalDateTime.of(2024, 6, 11, 10, 0))
                .endTime(LocalDateTime.of(2024, 6, 11, 10, 30))
                .status(AppointmentStatus.COMPLETED)
                .reasonForVisit("Follow-up")
                .build();

        Appointment updatedAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .startTime(LocalDateTime.of(2024, 6, 11, 10, 0))
                .endTime(LocalDateTime.of(2024, 6, 11, 10, 30))
                .status(AppointmentStatus.COMPLETED)
                .reasonForVisit("Follow-up")
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(updatedAppointment);
        when(appointmentMapper.toDto(updatedAppointment)).thenReturn(updateDTO);

        AppointmentDTO result = appointmentService.updateAppointment(1L, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void updateAppointment_patientNotFound() {
        AppointmentDTO updateDTO = AppointmentDTO.builder()
                .patientId(99L)
                .doctorId(2L)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.updateAppointment(1L, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void deleteAppointment_exists() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        doNothing().when(appointmentRepository).delete(appointment);

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void deleteAppointment_notFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.deleteAppointment(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment not found");
    }
}
