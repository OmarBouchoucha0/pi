package tn.esprit.pi.service.labDocuments;

import java.util.List;

import tn.esprit.pi.dto.labDocumentsDTO.MedicalDocumentDTO;

public interface MedicalDocumentService {
    MedicalDocumentDTO createDocument(MedicalDocumentDTO dto);
    MedicalDocumentDTO getDocumentById(Long id);
    List<MedicalDocumentDTO> getAllDocuments();
    List<MedicalDocumentDTO> getDocumentsByFolderId(Long folderId);
    List<MedicalDocumentDTO> getDocumentsByPatientId(Long patientId);
    MedicalDocumentDTO updateDocument(Long id, MedicalDocumentDTO dto);
    void deleteDocument(Long id);
}
