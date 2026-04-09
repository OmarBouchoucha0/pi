package tn.esprit.pi.repository.labDocuments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.labDocuments.DocumentFolder;

@Repository
public interface DocumentFolderRepository extends JpaRepository<DocumentFolder, Long> {
    List<DocumentFolder> findByPatientId(Long patientId);
}
