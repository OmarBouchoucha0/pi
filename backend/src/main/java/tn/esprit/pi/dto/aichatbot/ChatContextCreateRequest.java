package tn.esprit.pi.dto.aichatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatContextCreateRequest {

    @NotNull(message = "sessionId is required")
    private Long sessionId;

    @NotBlank(message = "key is required")
    private String key;

    private String value;
}
