package tn.esprit.pi.repository.labDocuments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.labDocuments.MedicalDocument;

@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {
    List<MedicalDocument> findByFolderId(Long folderId);
    List<MedicalDocument> findByPatientId(Long patientId);
}
