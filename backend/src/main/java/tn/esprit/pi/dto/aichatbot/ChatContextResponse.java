package tn.esprit.pi.dto.aichatbot;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatContextResponse {

    private Long id;
    private Long sessionId;
    private String key;
    private String value;
    private LocalDateTime createdAt;
}
