package tn.esprit.pi.dto.medicalNote;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicalNoteResponse {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private Long tenantId;
    private Object type;
    private String content;
    private String diagnosisLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
