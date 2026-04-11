package tn.esprit.pi.service;

import java.util.List;

import tn.esprit.pi.dto.AlertRequest;
import tn.esprit.pi.dto.AlertResponse;
import tn.esprit.pi.enums.AlertSeverity;
import tn.esprit.pi.enums.AlertStatus;

public interface AlertService {
    AlertResponse createAlert(AlertRequest request);
    AlertResponse getAlertById(Long id);
    List<AlertResponse> getAllAlerts();
    List<AlertResponse> getAlertsByPatient(Long patientId);
    List<AlertResponse> getAlertsByTenant(Long tenantId);
    List<AlertResponse> getAlertsByStatus(AlertStatus status);
    List<AlertResponse> getAlertsBySeverity(AlertSeverity severity);
    AlertResponse updateAlertStatus(Long id, AlertStatus status);
    AlertResponse escalateAlert(Long id);
    AlertResponse acknowledgeAlert(Long id);
    AlertResponse closeAlert(Long id);
    void deleteAlert(Long id);
    AlertResponse updateAlert(Long id, AlertRequest request);
}
