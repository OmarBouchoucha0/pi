package tn.esprit.pi.repository.patient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.patient.PatientState;
import tn.esprit.pi.enums.patient.PatientStateEnum;

@Repository
public interface PatientStateRepository extends JpaRepository<PatientState, Long> {

    List<PatientState> findByPatientIdAndTenantIdOrderByCalculatedAtDesc(Long patientId, Long tenantId);

    Optional<PatientState> findTopByPatientIdAndTenantIdOrderByCalculatedAtDesc(Long patientId, Long tenantId);

    List<PatientState> findTop2ByPatientIdAndTenantIdOrderByCalculatedAtDesc(Long patientId, Long tenantId);

    // ── NEW: evolution — states within last N days ──
    @Query("SELECT ps FROM PatientState ps " +
           "WHERE ps.patient.id = :patientId AND ps.tenant.id = :tenantId " +
           "AND ps.calculatedAt >= :since " +
           "ORDER BY ps.calculatedAt ASC")
    List<PatientState> findEvolutionSince(@Param("patientId") Long patientId,
                                          @Param("tenantId") Long tenantId,
                                          @Param("since") LocalDateTime since);

    // ── NEW: all patients in tenant above a score threshold ──
    @Query("SELECT ps FROM PatientState ps " +
           "WHERE ps.tenant.id = :tenantId AND ps.score >= :minScore " +
           "AND ps.calculatedAt = (" +
           "  SELECT MAX(ps2.calculatedAt) FROM PatientState ps2 " +
           "  WHERE ps2.patient.id = ps.patient.id AND ps2.tenant.id = :tenantId" +
           ") ORDER BY ps.score DESC")
    List<PatientState> findLatestByTenantAboveScore(@Param("tenantId") Long tenantId,
                                                     @Param("minScore") Double minScore);

    // ── NEW: worsening streak ──
    @Query("SELECT ps FROM PatientState ps " +
           "WHERE ps.patient.id = :patientId AND ps.tenant.id = :tenantId " +
           "ORDER BY ps.calculatedAt DESC")
    List<PatientState> findAllByPatientAndTenantDesc(@Param("patientId") Long patientId,
                                                      @Param("tenantId") Long tenantId);

    // ── NEW: all CRITICAL patients latest state in tenant ──
    @Query("SELECT ps FROM PatientState ps " +
           "WHERE ps.tenant.id = :tenantId AND ps.state = :state " +
           "AND ps.calculatedAt = (" +
           "  SELECT MAX(ps2.calculatedAt) FROM PatientState ps2 " +
           "  WHERE ps2.patient.id = ps.patient.id AND ps2.tenant.id = :tenantId" +
           ")")
    List<PatientState> findLatestByTenantAndState(@Param("tenantId") Long tenantId,
                                                   @Param("state") PatientStateEnum state);
}
