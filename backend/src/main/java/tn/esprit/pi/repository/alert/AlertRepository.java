package tn.esprit.pi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.Alert;
import tn.esprit.pi.enums.AlertSeverity;
import tn.esprit.pi.enums.AlertStatus;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByPatientId(Long patientId);

    List<Alert> findByTenantId(Long tenantId);

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findBySeverity(AlertSeverity severity);

    List<Alert> findByTenantIdAndStatus(Long tenantId, AlertStatus status);

    // Pour déduplication
    Optional<Alert> findByGroupKeyAndStatus(String groupKey, AlertStatus status);

    List<Alert> findByEscalationLevelGreaterThan(Integer level);
}
