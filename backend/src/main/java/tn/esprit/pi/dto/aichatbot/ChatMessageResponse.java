package tn.esprit.pi.dto.aichatbot;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.aichatbot.MessageType;
import tn.esprit.pi.enums.aichatbot.SenderType;

@Data
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long sessionId;
    private SenderType senderType;
    private String content;
    private MessageType messageType;
    private Long replyTo;
    private LocalDateTime sentAt;
}
