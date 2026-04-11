package tn.esprit.pi.controller.appointments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tn.esprit.pi.dto.appointmentsDTO.AppointmentDTO;
import tn.esprit.pi.enums.appointments.AppointmentStatus;
import tn.esprit.pi.service.appointments.IAppointmentService;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private IAppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
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
    void createAppointment_shouldReturnCreated() {
        when(appointmentService.createAppointment(any(AppointmentDTO.class))).thenReturn(appointmentDTO);

        ResponseEntity<AppointmentDTO> result = appointmentController.createAppointment(appointmentDTO);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void getAllAppointments_shouldReturnOk() {
        when(appointmentService.getAllAppointments()).thenReturn(List.of(appointmentDTO));

        ResponseEntity<List<AppointmentDTO>> result = appointmentController.getAllAppointments();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void getAppointmentById_shouldReturnOk() {
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointmentDTO);

        ResponseEntity<AppointmentDTO> result = appointmentController.getAppointmentById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void updateAppointment_shouldReturnOk() {
        when(appointmentService.updateAppointment(eq(1L), any(AppointmentDTO.class))).thenReturn(appointmentDTO);

        ResponseEntity<AppointmentDTO> result = appointmentController.updateAppointment(1L, appointmentDTO);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void deleteAppointment_shouldReturnNoContent() {
        doNothing().when(appointmentService).deleteAppointment(1L);

        ResponseEntity<Void> result = appointmentController.deleteAppointment(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(appointmentService).deleteAppointment(1L);
    }
}
