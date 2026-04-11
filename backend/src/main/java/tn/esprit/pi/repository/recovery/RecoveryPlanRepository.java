package tn.esprit.pi.repository.recovery;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.recovery.RecoveryPlan;
import tn.esprit.pi.enums.recovery.RecoveryStatus;

@Repository
public interface RecoveryPlanRepository extends JpaRepository<RecoveryPlan, Long> {

    // Find active plan for a patient (only one should exist at a time)
    Optional<RecoveryPlan> findByPatientIdAndTenantIdAndActiveTrue(Long patientId, Long tenantId);

    // All plans for a patient (including past ones)
    List<RecoveryPlan> findByPatientIdAndTenantIdOrderByCreatedAtDesc(Long patientId, Long tenantId);

    // All active plans for a tenant — for the doctor's overview
    List<RecoveryPlan> findByTenantIdAndActiveTrueOrderByReturnScoreDesc(Long tenantId);

    // Patients whose return score exceeds a threshold — triage list
    @Query("SELECT rp FROM RecoveryPlan rp WHERE rp.tenant.id = :tenantId " +
           "AND rp.active = true AND rp.returnScore >= :minScore " +
           "ORDER BY rp.returnScore DESC")
    List<RecoveryPlan> findAtRiskByTenant(@Param("tenantId") Long tenantId,
                                           @Param("minScore") Double minScore);

    // Plans by current status
    List<RecoveryPlan> findByTenantIdAndActiveTrueAndCurrentStatus(Long tenantId, RecoveryStatus status);

    // Deactivate any existing active plan before creating a new one
    @Query("UPDATE RecoveryPlan rp SET rp.active = false WHERE rp.patient.id = :patientId AND rp.tenant.id = :tenantId AND rp.active = true")
    @org.springframework.data.jpa.repository.Modifying
    void deactivateExistingPlans(@Param("patientId") Long patientId, @Param("tenantId") Long tenantId);
}
