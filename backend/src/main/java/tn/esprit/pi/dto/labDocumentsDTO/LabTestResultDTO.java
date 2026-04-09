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
public class LabTestResultDTO {

    private Long id;

    // Relationship IDs
    private Long patientId;
    private Long sourceDocumentId; // Optional as per your entity

    // Data fields
    private String testName;
    private Double testValue;
    private String unit;
    private Double normalRangeMin;
    private Double normalRangeMax;
    private Boolean isAbnormal;

    // Read-only field
    private LocalDateTime extractedAt;
}
