package tn.esprit.pi.service.alert;

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

import tn.esprit.pi.dto.AlertRequest;
import tn.esprit.pi.dto.AlertResponse;
import tn.esprit.pi.entity.Alert;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.AlertSeverity;
import tn.esprit.pi.enums.AlertStatus;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.AlertRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;
import tn.esprit.pi.service.AlertServiceImpl;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private AlertServiceImpl alertService;

    private Tenant tenant;
    private User patient;
    private Alert alert;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder().id(1L).name("Test Tenant").build();
        patient = User.builder().id(1L).firstName("John").lastName("Doe").build();
        alert = Alert.builder()
                .id(1L)
                .patient(patient)
                .tenant(tenant)
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
    void createAlert_withPatient_shouldCreateAlert() {
        AlertRequest request = new AlertRequest();
        request.setPatientId(1L);
        request.setTenantId(1L);
        request.setType("VITAL_SIGN");
        request.setTitle("High Heart Rate");
        request.setDescription("Test description");
        request.setSeverity(AlertSeverity.HIGH);

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        AlertResponse result = alertService.createAlert(request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("High Heart Rate");
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.HIGH);
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void createAlert_withoutPatient_shouldCreateAlertWithoutPatient() {
        AlertRequest request = new AlertRequest();
        request.setTenantId(1L);
        request.setType("SYSTEM");
        request.setTitle("System Alert");
        request.setDescription("Test description");
        request.setSeverity(AlertSeverity.MEDIUM);

        Alert alertWithoutPatient = Alert.builder()
                .id(2L)
                .tenant(tenant)
                .type("SYSTEM")
                .title("System Alert")
                .description("Test description")
                .severity(AlertSeverity.MEDIUM)
                .priorityScore(50)
                .occurrenceCount(1)
                .status(AlertStatus.OPEN)
                .escalationLevel(0)
                .createdAt(LocalDateTime.now())
                .build();

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(alertRepository.save(any(Alert.class))).thenReturn(alertWithoutPatient);

        AlertResponse result = alertService.createAlert(request);

        assertThat(result).isNotNull();
        assertThat(result.getPatientId()).isNull();
    }

    @Test
    void createAlert_tenantNotFound_shouldThrowException() {
        AlertRequest request = new AlertRequest();
        request.setTenantId(99L);
        request.setType("VITAL_SIGN");
        request.setTitle("Test");
        request.setSeverity(AlertSeverity.LOW);

        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertService.createAlert(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tenant not found");
    }

    @Test
    void createAlert_patientNotFound_shouldThrowException() {
        AlertRequest request = new AlertRequest();
        request.setPatientId(99L);
        request.setTenantId(1L);
        request.setType("VITAL_SIGN");
        request.setTitle("Test");
        request.setSeverity(AlertSeverity.LOW);

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertService.createAlert(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void createAlert_deduplication_shouldIncrementOccurrenceCount() {
        AlertRequest request = new AlertRequest();
        request.setTenantId(1L);
        request.setType("VITAL_SIGN");
        request.setTitle("High Heart Rate");
        request.setSeverity(AlertSeverity.HIGH);
        request.setGroupKey("vital-1");

        Alert existingAlert = Alert.builder()
                .id(1L)
                .tenant(tenant)
                .type("VITAL_SIGN")
                .title("High Heart Rate")
                .severity(AlertSeverity.HIGH)
                .priorityScore(75)
                .groupKey("vital-1")
                .occurrenceCount(1)
                .status(AlertStatus.OPEN)
                .build();

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(alertRepository.findByGroupKeyAndStatus("vital-1", AlertStatus.OPEN))
                .thenReturn(Optional.of(existingAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(existingAlert);

        AlertResponse result = alertService.createAlert(request);

        assertThat(result.getOccurrenceCount()).isEqualTo(2);
    }

    @Test
    void createAlert_severityUpgrade_shouldUpgradeSeverity() {
        AlertRequest request = new AlertRequest();
        request.setTenantId(1L);
        request.setType("VITAL_SIGN");
        request.setTitle("High Heart Rate");
        request.setSeverity(AlertSeverity.CRITICAL);
        request.setGroupKey("vital-1");

        Alert existingAlert = Alert.builder()
                .id(1L)
                .tenant(tenant)
                .type("VITAL_SIGN")
                .title("High Heart Rate")
                .severity(AlertSeverity.HIGH)
                .priorityScore(75)
                .groupKey("vital-1")
                .occurrenceCount(1)
                .status(AlertStatus.OPEN)
                .build();

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(alertRepository.findByGroupKeyAndStatus("vital-1", AlertStatus.OPEN))
                .thenReturn(Optional.of(existingAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(existingAlert);

        AlertResponse result = alertService.createAlert(request);

        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.CRITICAL);
        assertThat(result.getPriorityScore()).isEqualTo(100);
    }

    @Test
    void getAlertById_found_shouldReturnAlert() {
        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));

        AlertResponse result = alertService.getAlertById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("High Heart Rate");
    }

    @Test
    void getAlertById_notFound_shouldThrowException() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertService.getAlertById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alert not found");
    }

    @Test
    void getAllAlerts_shouldReturnAllAlerts() {
        when(alertRepository.findAll()).thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.getAllAlerts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("High Heart Rate");
    }

    @Test
    void getAlertsByPatient_shouldReturnPatientAlerts() {
        when(alertRepository.findByPatientId(1L)).thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.getAlertsByPatient(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(1L);
    }

    @Test
    void getAlertsByTenant_shouldReturnTenantAlerts() {
        when(alertRepository.findByTenantId(1L)).thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.getAlertsByTenant(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTenantId()).isEqualTo(1L);
    }

    @Test
    void getAlertsByStatus_shouldReturnAlertsByStatus() {
        when(alertRepository.findByStatus(AlertStatus.OPEN)).thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.getAlertsByStatus(AlertStatus.OPEN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AlertStatus.OPEN);
    }

    @Test
    void getAlertsBySeverity_shouldReturnAlertsBySeverity() {
        when(alertRepository.findBySeverity(AlertSeverity.HIGH)).thenReturn(List.of(alert));

        List<AlertResponse> result = alertService.getAlertsBySeverity(AlertSeverity.HIGH);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSeverity()).isEqualTo(AlertSeverity.HIGH);
    }

    @Test
    void updateAlertStatus_shouldUpdateStatus() {
        Alert updatedAlert = Alert.builder()
                .id(1L)
                .patient(patient)
                .tenant(tenant)
                .type("VITAL_SIGN")
                .title("High Heart Rate")
                .severity(AlertSeverity.HIGH)
                .status(AlertStatus.ACKNOWLEDGED)
                .createdAt(LocalDateTime.now())
                .build();

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(updatedAlert);

        AlertResponse result = alertService.updateAlertStatus(1L, AlertStatus.ACKNOWLEDGED);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.ACKNOWLEDGED);
    }

    @Test
    void updateAlertStatus_closed_shouldSetHandledAt() {
        Alert closedAlert = Alert.builder()
                .id(1L)
                .patient(patient)
                .tenant(tenant)
                .type("VITAL_SIGN")
                .title("High Heart Rate")
                .severity(AlertSeverity.HIGH)
                .status(AlertStatus.CLOSED)
                .createdAt(LocalDateTime.now())
                .handledAt(LocalDateTime.now())
                .build();

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(closedAlert);

        AlertResponse result = alertService.updateAlertStatus(1L, AlertStatus.CLOSED);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.CLOSED);
        assertThat(result.getHandledAt()).isNotNull();
    }

    @Test
    void escalateAlert_shouldIncrementEscalationLevel() {
        Alert escalatedAlert = Alert.builder()
                .id(1L)
                .patient(patient)
                .tenant(tenant)
                .type("VITAL_SIGN")
                .title("High Heart Rate")
                .severity(AlertSeverity.HIGH)
                .status(AlertStatus.ESCALATED)
                .escalationLevel(1)
                .build();

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(escalatedAlert);

        AlertResponse result = alertService.escalateAlert(1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.ESCALATED);
        assertThat(result.getEscalationLevel()).isEqualTo(1);
    }

    @Test
    void acknowledgeAlert_shouldReturnAcknowledgedStatus() {
        Alert acknowledgedAlert = Alert.builder()
                .id(1L)
                .patient(patient)
                .tenant(tenant)
                .type("VITAL_SIGN")
                .title("High Heart Rate")
                .severity(AlertSeverity.HIGH)
                .status(AlertStatus.ACKNOWLEDGED)
                .build();

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(acknowledgedAlert);

        AlertResponse result = alertService.acknowledgeAlert(1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.ACKNOWLEDGED);
    }

    @Test
    void closeAlert_shouldReturnClosedStatus() {
        Alert closedAlert = Alert.builder()
                .id(1L)
                .patient(patient)
                .tenant(tenant)
                .type("VITAL_SIGN")
                .title("High Heart Rate")
                .severity(AlertSeverity.HIGH)
                .status(AlertStatus.CLOSED)
                .build();

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(closedAlert);

        AlertResponse result = alertService.closeAlert(1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.CLOSED);
    }

    @Test
    void deleteAlert_exists_shouldDeleteAlert() {
        when(alertRepository.existsById(1L)).thenReturn(true);
        doNothing().when(alertRepository).deleteById(1L);

        alertService.deleteAlert(1L);

        verify(alertRepository).deleteById(1L);
    }

    @Test
    void deleteAlert_notFound_shouldThrowException() {
        when(alertRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> alertService.deleteAlert(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alert not found");
    }

    @Test
    void updateAlert_shouldUpdateAllFields() {
        AlertRequest request = new AlertRequest();
        request.setPatientId(1L);
        request.setTenantId(1L);
        request.setType("MEDICATION");
        request.setTitle("Updated Title");
        request.setDescription("Updated description");
        request.setSeverity(AlertSeverity.CRITICAL);
        request.setGroupKey("med-1");

        Alert updatedAlert = Alert.builder()
                .id(1L)
                .patient(patient)
                .tenant(tenant)
                .type("MEDICATION")
                .title("Updated Title")
                .description("Updated description")
                .severity(AlertSeverity.CRITICAL)
                .priorityScore(100)
                .groupKey("med-1")
                .status(AlertStatus.OPEN)
                .build();

        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(alertRepository.save(any(Alert.class))).thenReturn(updatedAlert);

        AlertResponse result = alertService.updateAlert(1L, request);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getType()).isEqualTo("MEDICATION");
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.CRITICAL);
    }
}
