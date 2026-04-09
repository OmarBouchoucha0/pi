package tn.esprit.pi.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.NoteType;

@Data
@Builder
public class MedicalNoteResponse {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private Long tenantId;
    private NoteType type;
    private String content;
    private String diagnosisLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
