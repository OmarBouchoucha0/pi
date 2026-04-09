package tn.esprit.pi.dto.aichatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatSessionCreateRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "tenantId is required")
    private Long tenantId;

    @NotNull(message = "patientId is required")
    private Long patientId;
}
