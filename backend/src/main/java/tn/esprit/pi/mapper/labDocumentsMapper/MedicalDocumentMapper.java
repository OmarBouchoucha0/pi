package tn.esprit.pi.mapper.labDocumentsMapper;

import org.springframework.stereotype.Component;

import tn.esprit.pi.dto.labDocumentsDTO.MedicalDocumentDTO;
import tn.esprit.pi.entity.labDocuments.DocumentFolder;
import tn.esprit.pi.entity.labDocuments.MedicalDocument;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.repository.labDocuments.DocumentFolderRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Component
public class MedicalDocumentMapper {

    private final DocumentFolderRepository folderRepository;
    private final UserRepository userRepository;

    public MedicalDocumentMapper(DocumentFolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    public MedicalDocumentDTO toDto(MedicalDocument entity) {
        if (entity == null) {
            return null;
        }

        return MedicalDocumentDTO.builder()
                .id(entity.getId())
                .folderId(entity.getFolder() != null ? entity.getFolder().getId() : null)
                .patientId(entity.getPatient() != null ? entity.getPatient().getId() : null)
                .fileName(entity.getFileName())
                .fileUrl(entity.getFileUrl())
                .documentType(entity.getDocumentType())
                .uploadDate(entity.getUploadDate())
                .build();
    }

    public MedicalDocument toEntity(MedicalDocumentDTO dto) {
        if (dto == null) {
            return null;
        }

        MedicalDocument document = new MedicalDocument();
        document.setId(dto.getId());
        document.setFileName(dto.getFileName());
        document.setFileUrl(dto.getFileUrl());
        document.setDocumentType(dto.getDocumentType());

        if (dto.getFolderId() != null) {
            DocumentFolder folder = folderRepository.findById(dto.getFolderId())
                    .orElseThrow(() -> new RuntimeException("Folder not found with id: " + dto.getFolderId()));
            document.setFolder(folder);
        }

        if (dto.getPatientId() != null) {
            User patient = userRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found with id: " + dto.getPatientId()));
            document.setPatient(patient);
        }

        return document;
    }
}
