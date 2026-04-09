package tn.esprit.pi.repository.vital;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.vital.VitalParameter;
import tn.esprit.pi.enums.vital.VitalStatus;
import tn.esprit.pi.enums.vital.VitalType;

@Repository
public interface VitalParameterRepository extends JpaRepository<VitalParameter, Long> {

    List<VitalParameter> findByPatientIdAndTenantIdOrderByRecordedAtDesc(Long patientId, Long tenantId);

    List<VitalParameter> findByPatientIdAndTenantIdAndTypeOrderByRecordedAtDesc(Long patientId, Long tenantId, VitalType type);

    List<VitalParameter> findByPatientIdAndTenantIdAndRecordedAtAfter(Long patientId, Long tenantId, LocalDateTime since);

    Optional<VitalParameter> findTopByPatientIdAndTenantIdAndTypeOrderByRecordedAtDesc(Long patientId, Long tenantId, VitalType type);

    @Query("SELECT v FROM VitalParameter v WHERE v.patient.id = :patientId AND v.tenant.id = :tenantId " +
           "AND v.recordedAt = (SELECT MAX(v2.recordedAt) FROM VitalParameter v2 " +
           "WHERE v2.patient.id = :patientId AND v2.type = v.type)")
    List<VitalParameter> findLatestPerTypeByPatientAndTenant(@Param("patientId") Long patientId, @Param("tenantId") Long tenantId);

    // ── NEW: stats (min/max/avg) for one patient + type + date range ──
    @Query("SELECT MIN(v.value), MAX(v.value), AVG(v.value), COUNT(v) " +
           "FROM VitalParameter v " +
           "WHERE v.patient.id = :patientId AND v.tenant.id = :tenantId " +
           "AND v.type = :type AND v.recordedAt BETWEEN :from AND :to")
    List<Object[]> findStatsForPatient(@Param("patientId") Long patientId,
                                       @Param("tenantId") Long tenantId,
                                       @Param("type") VitalType type,
                                       @Param("from") LocalDateTime from,
                                       @Param("to") LocalDateTime to);

    // ── NEW: all patients in tenant with at least one vital ──
    @Query("SELECT DISTINCT v.patient.id FROM VitalParameter v WHERE v.tenant.id = :tenantId")
    List<Long> findDistinctPatientIdsByTenantId(@Param("tenantId") Long tenantId);

    // ── NEW: all vitals for a tenant (for ward summary) ──
    List<VitalParameter> findByTenantId(Long tenantId);

    // ── NEW: patients with at least one CRITICAL vital ──
    @Query("SELECT DISTINCT v.patient.id FROM VitalParameter v " +
           "WHERE v.tenant.id = :tenantId AND v.status = :status")
    List<Long> findDistinctPatientIdsByTenantIdAndStatus(@Param("tenantId") Long tenantId,
                                                          @Param("status") VitalStatus status);

    // ── NEW: count vitals by status for one patient ──
    long countByPatientIdAndTenantIdAndStatus(Long patientId, Long tenantId, VitalStatus status);

    long countByPatientIdAndTenantId(Long patientId, Long tenantId);
}
