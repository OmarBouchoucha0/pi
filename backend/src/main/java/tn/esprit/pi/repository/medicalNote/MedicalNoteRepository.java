package tn.esprit.pi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.MedicalNote;
import tn.esprit.pi.enums.NoteType;

@Repository
public interface MedicalNoteRepository extends JpaRepository<MedicalNote, Long> {

    List<MedicalNote> findByPatientIdAndTenantIdOrderByCreatedAtDesc(Long patientId, Long tenantId);

    List<MedicalNote> findByPatientIdAndTenantIdAndTypeOrderByCreatedAtDesc(Long patientId, Long tenantId, NoteType type);

    List<MedicalNote> findByDoctorIdAndTenantIdOrderByCreatedAtDesc(Long doctorId, Long tenantId);

    @Query("SELECT n FROM MedicalNote n WHERE n.patient.id = :patientId AND n.tenant.id = :tenantId " +
           "ORDER BY n.createdAt DESC")
    List<MedicalNote> findRecentByPatient(@Param("patientId") Long patientId,
                                           @Param("tenantId") Long tenantId);
}
