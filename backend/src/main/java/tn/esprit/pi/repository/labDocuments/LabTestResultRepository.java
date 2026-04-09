package tn.esprit.pi.repository.labDocuments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.labDocuments.LabTestResult;

@Repository
public interface LabTestResultRepository extends JpaRepository<LabTestResult, Long> {
    List<LabTestResult> findByPatientId(Long patientId);
    List<LabTestResult> findBySourceDocumentId(Long sourceDocumentId);
}
