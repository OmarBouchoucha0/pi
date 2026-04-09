package tn.esprit.pi.dto.labDocumentsDTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.pi.enums.DocumentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDocumentDTO {

    private Long id;

    // Using IDs instead of full entity objects
    private Long folderId;
    private Long patientId;

    private String fileName;
    private String fileUrl;
    private DocumentType documentType;

    // Read-only field
    private LocalDateTime uploadDate;
}
