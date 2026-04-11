package tn.esprit.pi.controller.alert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import tn.esprit.pi.controller.AlertController;
import tn.esprit.pi.dto.AlertResponse;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.AlertSeverity;
import tn.esprit.pi.enums.AlertStatus;
import tn.esprit.pi.service.AlertService;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    private AlertResponse alertResponse;
    private User patient;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        patient = User.builder().id(1L).firstName("John").lastName("Doe").build();
        tenant = Tenant.builder().id(1L).name("Test Tenant").build();

        alertResponse = AlertResponse.builder()
                .id(1L)
                .patientId(1L)
                .patientName("John Doe")
                .tenantId(1L)
                .type("VITAL_SIGN")
                .title("High Heart Rate")
                .description("Patient heart rate exceeded threshold")
                .severity(AlertSeverity.HIGH)
                .priorityScore(75)
                .groupKey("vital-1")
                .occurrenceCount(1)
                .status(AlertStatus.OPEN)
                .escalationLevel(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createAlert_shouldReturnCreated() {
        when(alertService.createAlert(any())).thenReturn(alertResponse);

        ResponseEntity<AlertResponse> result = alertController.createAlert(new tn.esprit.pi.dto.AlertRequest());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("High Heart Rate");
    }

    @Test
    void getAllAlerts_shouldReturnOk() {
        when(alertService.getAllAlerts()).thenReturn(List.of(alertResponse));

        ResponseEntity<List<AlertResponse>> result = alertController.getAllAlerts();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void getAlertById_shouldReturnOk() {
        when(alertService.getAlertById(1L)).thenReturn(alertResponse);

        ResponseEntity<AlertResponse> result = alertController.getAlertById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void getByPatient_shouldReturnOk() {
        when(alertService.getAlertsByPatient(1L)).thenReturn(List.of(alertResponse));

        ResponseEntity<List<AlertResponse>> result = alertController.getByPatient(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void getByTenant_shouldReturnOk() {
        when(alertService.getAlertsByTenant(1L)).thenReturn(List.of(alertResponse));

        ResponseEntity<List<AlertResponse>> result = alertController.getByTenant(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void getByStatus_shouldReturnOk() {
        when(alertService.getAlertsByStatus(AlertStatus.OPEN)).thenReturn(List.of(alertResponse));

        ResponseEntity<List<AlertResponse>> result = alertController.getByStatus(AlertStatus.OPEN);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void getBySeverity_shouldReturnOk() {
        when(alertService.getAlertsBySeverity(AlertSeverity.HIGH)).thenReturn(List.of(alertResponse));

        ResponseEntity<List<AlertResponse>> result = alertController.getBySeverity(AlertSeverity.HIGH);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void updateStatus_shouldReturnOk() {
        AlertResponse updatedResponse = AlertResponse.builder()
                .id(1L)
                .status(AlertStatus.ACKNOWLEDGED)
                .build();
        when(alertService.updateAlertStatus(1L, AlertStatus.ACKNOWLEDGED)).thenReturn(updatedResponse);

        ResponseEntity<AlertResponse> result = alertController.updateStatus(1L, AlertStatus.ACKNOWLEDGED);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getStatus()).isEqualTo(AlertStatus.ACKNOWLEDGED);
    }

    @Test
    void escalate_shouldReturnOk() {
        AlertResponse escalatedResponse = AlertResponse.builder()
                .id(1L)
                .status(AlertStatus.ESCALATED)
                .escalationLevel(1)
                .build();
        when(alertService.escalateAlert(1L)).thenReturn(escalatedResponse);

        ResponseEntity<AlertResponse> result = alertController.escalate(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getEscalationLevel()).isEqualTo(1);
    }

    @Test
    void acknowledge_shouldReturnOk() {
        AlertResponse acknowledgedResponse = AlertResponse.builder()
                .id(1L)
                .status(AlertStatus.ACKNOWLEDGED)
                .build();
        when(alertService.acknowledgeAlert(1L)).thenReturn(acknowledgedResponse);

        ResponseEntity<AlertResponse> result = alertController.acknowledge(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getStatus()).isEqualTo(AlertStatus.ACKNOWLEDGED);
    }

    @Test
    void close_shouldReturnOk() {
        AlertResponse closedResponse = AlertResponse.builder()
                .id(1L)
                .status(AlertStatus.CLOSED)
                .build();
        when(alertService.closeAlert(1L)).thenReturn(closedResponse);

        ResponseEntity<AlertResponse> result = alertController.close(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getStatus()).isEqualTo(AlertStatus.CLOSED);
    }

    @Test
    void deleteAlert_shouldReturnNoContent() {
        doNothing().when(alertService).deleteAlert(1L);

        ResponseEntity<Void> result = alertController.deleteAlert(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(alertService).deleteAlert(1L);
    }

    @Test
    void updateAlert_shouldReturnOk() {
        when(alertService.updateAlert(anyLong(), any())).thenReturn(alertResponse);

        ResponseEntity<AlertResponse> result = alertController.updateAlert(1L, new tn.esprit.pi.dto.AlertRequest());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
    }
}
