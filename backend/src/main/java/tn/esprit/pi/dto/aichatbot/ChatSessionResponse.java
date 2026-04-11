package tn.esprit.pi.dto.aichatbot;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.aichatbot.ChatSessionStatus;

@Data
@Builder
public class ChatSessionResponse {

    private Long id;
    private String name;
    private ChatSessionStatus status;
    private Long patientId;
    private String patientName;
    private Long totalMessages;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime lastActivityAt;
}
