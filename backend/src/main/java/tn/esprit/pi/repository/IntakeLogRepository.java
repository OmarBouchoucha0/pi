package tn.esprit.pi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.IntakeLog;
import tn.esprit.pi.enums.IntakeStatus;

@Repository
public interface IntakeLogRepository extends JpaRepository<IntakeLog, Long> {
    List<IntakeLog> findByPrescriptionId(Long prescriptionId);
    List<IntakeLog> findByStatus(IntakeStatus status);
    List<IntakeLog> findByPrescriptionIdAndStatus(Long prescriptionId, IntakeStatus status);
    // Pour calculer le taux d'adhérence
    long countByPrescriptionIdAndStatus(Long prescriptionId, IntakeStatus status);
}
