package tn.esprit.pi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    private AlertResponse toResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .patientId(alert.getPatient() != null ? alert.getPatient().getId() : null)
                .patientName(alert.getPatient() != null
                        ? alert.getPatient().getFirstName() + " " + alert.getPatient().getLastName()
                        : null)
                .tenantId(alert.getTenant().getId())
                .type(alert.getType())
                .title(alert.getTitle())
                .description(alert.getDescription())
                .severity(alert.getSeverity())
                .priorityScore(alert.getPriorityScore())
                .groupKey(alert.getGroupKey())
                .occurrenceCount(alert.getOccurrenceCount())
                .status(alert.getStatus())
                .escalationLevel(alert.getEscalationLevel())
                .createdAt(alert.getCreatedAt())
                .handledAt(alert.getHandledAt())
                .build();
    }

    private int computePriorityScore(AlertSeverity severity) {
        return switch (severity) {
            case CRITICAL -> 100;
            case HIGH     -> 75;
            case MEDIUM   -> 50;
            case LOW      -> 25;
        };
    }

    @Override
    public AlertResponse createAlert(AlertRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + request.getTenantId()));

        User patient = null;
        if (request.getPatientId() != null) {
            patient = userRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));
        }

        // Déduplication : si même groupKey et statut OPEN → incrémenter
        if (request.getGroupKey() != null) {
            Optional<Alert> existing = alertRepository
                    .findByGroupKeyAndStatus(request.getGroupKey(), AlertStatus.OPEN);
            if (existing.isPresent()) {
                Alert dup = existing.get();
                dup.setOccurrenceCount(dup.getOccurrenceCount() + 1);
                if (request.getSeverity().ordinal() > dup.getSeverity().ordinal()) {
                    dup.setSeverity(request.getSeverity());
                    dup.setPriorityScore(computePriorityScore(request.getSeverity()));
                }
                return toResponse(alertRepository.save(dup));
            }
        }

        int score = request.getPriorityScore() != null
                ? request.getPriorityScore()
                : computePriorityScore(request.getSeverity());

        Alert alert = Alert.builder()
                .patient(patient)
                .tenant(tenant)
                .type(request.getType())
                .title(request.getTitle())
                .description(request.getDescription())
                .severity(request.getSeverity())
                .priorityScore(score)
                .groupKey(request.getGroupKey())
                .occurrenceCount(1)
                .status(AlertStatus.OPEN)
                .escalationLevel(0)
                .build();

        return toResponse(alertRepository.save(alert));
    }

    @Override
    public AlertResponse getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
        return toResponse(alert);
    }

    @Override
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AlertResponse> getAlertsByPatient(Long patientId) {
        return alertRepository.findByPatientId(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AlertResponse> getAlertsByTenant(Long tenantId) {
        return alertRepository.findByTenantId(tenantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AlertResponse> getAlertsByStatus(AlertStatus status) {
        return alertRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AlertResponse> getAlertsBySeverity(AlertSeverity severity) {
        return alertRepository.findBySeverity(severity)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public AlertResponse updateAlertStatus(Long id, AlertStatus status) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
        alert.setStatus(status);
        if (status == AlertStatus.CLOSED || status == AlertStatus.ACKNOWLEDGED) {
            alert.setHandledAt(LocalDateTime.now());
        }
        return toResponse(alertRepository.save(alert));
    }

    @Override
    public AlertResponse escalateAlert(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
        alert.setEscalationLevel(alert.getEscalationLevel() + 1);
        alert.setStatus(AlertStatus.ESCALATED);
        return toResponse(alertRepository.save(alert));
    }

    @Override
    public AlertResponse acknowledgeAlert(Long id) {
        return updateAlertStatus(id, AlertStatus.ACKNOWLEDGED);
    }

    @Override
    public AlertResponse closeAlert(Long id) {
        return updateAlertStatus(id, AlertStatus.CLOSED);
    }

    @Override
    public void deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alert not found: " + id);
        }
        alertRepository.deleteById(id);
    }
    @Override
    public AlertResponse updateAlert(Long id, AlertRequest request) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));

        User patient = null;
        if (request.getPatientId() != null) {
            patient = userRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));
        }

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + request.getTenantId()));

        alert.setPatient(patient);
        alert.setTenant(tenant);
        alert.setType(request.getType());
        alert.setTitle(request.getTitle());
        alert.setDescription(request.getDescription());
        alert.setSeverity(request.getSeverity());
        alert.setPriorityScore(computePriorityScore(request.getSeverity()));
        alert.setGroupKey(request.getGroupKey());

        return toResponse(alertRepository.save(alert));
    }
}
