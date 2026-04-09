package tn.esprit.pi.dto.labDocumentsDTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFolderDTO {

    private Long id;

    // We only pass the ID of the patient to avoid exposing the whole User entity
    private Long patientId;

    private String categoryName;
    private String description;

    // Read-only field, usually ignored on creation
    private LocalDateTime createdAt;
}
