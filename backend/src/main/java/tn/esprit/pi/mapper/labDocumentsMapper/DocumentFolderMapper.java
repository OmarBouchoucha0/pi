package tn.esprit.pi.mapper.labDocumentsMapper;

import org.springframework.stereotype.Component;

import tn.esprit.pi.dto.labDocumentsDTO.DocumentFolderDTO;
import tn.esprit.pi.entity.labDocuments.DocumentFolder;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.repository.user.UserRepository;

@Component
public class DocumentFolderMapper {

    private final UserRepository userRepository;

    public DocumentFolderMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public DocumentFolderDTO toDto(DocumentFolder entity) {
        if (entity == null) {
            return null;
        }

        return DocumentFolderDTO.builder()
                .id(entity.getId())
                .patientId(entity.getPatient() != null ? entity.getPatient().getId() : null)
                .categoryName(entity.getCategoryName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public DocumentFolder toEntity(DocumentFolderDTO dto) {
        if (dto == null) {
            return null;
        }

        DocumentFolder folder = new DocumentFolder();
        folder.setId(dto.getId());
        folder.setCategoryName(dto.getCategoryName());
        folder.setDescription(dto.getDescription());

        // Fetch and set the patient reference
        if (dto.getPatientId() != null) {
            User patient = userRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found with id: " + dto.getPatientId()));
            folder.setPatient(patient);
        }

        return folder;
    }
}
