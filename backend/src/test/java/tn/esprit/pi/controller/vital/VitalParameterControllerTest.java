package tn.esprit.pi.controller.vital;

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

import tn.esprit.pi.dto.vital.VitalParameterResponse;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.vital.VitalStatus;
import tn.esprit.pi.enums.vital.VitalType;
import tn.esprit.pi.service.vital.IVitalParameterService;

@ExtendWith(MockitoExtension.class)
class VitalParameterControllerTest {

    @Mock
    private IVitalParameterService service;

    @InjectMocks
    private VitalParameterController controller;

    private VitalParameterResponse vitalResponse;
    private User patient;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        patient = User.builder().id(1L).firstName("John").lastName("Doe").build();
        tenant = Tenant.builder().id(1L).name("Test Tenant").build();

        vitalResponse = VitalParameterResponse.builder()
                .id(1L)
                .patientId(1L)
                .tenantId(1L)
                .type(VitalType.HEART_RATE)
                .value(75.0)
                .unit("bpm")
                .status(VitalStatus.NORMAL)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void add_shouldReturnCreated() {
        when(service.addVital(any())).thenReturn(vitalResponse);

        ResponseEntity<VitalParameterResponse> result = controller.add(new tn.esprit.pi.dto.vital.VitalParameterRequest());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void getAll_shouldReturnOk() {
        when(service.getAll()).thenReturn(List.of(vitalResponse));

        ResponseEntity<List<VitalParameterResponse>> result = controller.getAll();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void getById_shouldReturnOk() {
        when(service.getById(1L)).thenReturn(vitalResponse);

        ResponseEntity<VitalParameterResponse> result = controller.getById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void getByPatient_shouldReturnOk() {
        when(service.getByPatient(1L, 1L)).thenReturn(List.of(vitalResponse));

        ResponseEntity<List<VitalParameterResponse>> result = controller.getByPatient(1L, 1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void getByType_shouldReturnOk() {
        when(service.getByPatientAndType(1L, 1L, VitalType.HEART_RATE)).thenReturn(List.of(vitalResponse));

        ResponseEntity<List<VitalParameterResponse>> result = controller.getByType(1L, VitalType.HEART_RATE, 1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getLatest_shouldReturnOk() {
        when(service.getLatestPerType(1L, 1L)).thenReturn(List.of(vitalResponse));

        ResponseEntity<List<VitalParameterResponse>> result = controller.getLatest(1L, 1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void update_shouldReturnOk() {
        when(service.update(eq(1L), any())).thenReturn(vitalResponse);

        ResponseEntity<VitalParameterResponse> result = controller.update(1L, new tn.esprit.pi.dto.vital.VitalParameterRequest());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void delete_shouldReturnNoContent() {
        doNothing().when(service).delete(1L);

        ResponseEntity<Void> result = controller.delete(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
