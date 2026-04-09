package tn.esprit.pi.service.labDocuments;

import java.util.List;

import tn.esprit.pi.dto.labDocumentsDTO.DocumentFolderDTO;

public interface DocumentFolderService {
    DocumentFolderDTO createFolder(DocumentFolderDTO dto);
    DocumentFolderDTO getFolderById(Long id);
    List<DocumentFolderDTO> getAllFolders();
    List<DocumentFolderDTO> getFoldersByPatientId(Long patientId);
    DocumentFolderDTO updateFolder(Long id, DocumentFolderDTO dto);
    void deleteFolder(Long id);
}
