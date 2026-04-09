package tn.esprit.pi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.pi.enums.NoteType;

@Data
public class MedicalNoteRequest {

    @NotNull(message = "patientId is required")
    private Long patientId;

    @NotNull(message = "doctorId is required")
    private Long doctorId;

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "type is required")
    private NoteType type;

    @NotBlank(message = "content is required")
    private String content;

    private String diagnosisLabel;
}
