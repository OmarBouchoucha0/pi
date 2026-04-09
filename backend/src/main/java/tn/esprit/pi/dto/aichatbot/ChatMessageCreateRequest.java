package tn.esprit.pi.dto.aichatbot;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.esprit.pi.enums.aichatbot.MessageType;
import tn.esprit.pi.enums.aichatbot.SenderType;

@Data
@Schema(description = "Request payload for creating a chat message")
public class ChatMessageCreateRequest {

    @Schema(description = "Unique identifier of the chat session this message belongs to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "sessionId is required")
    private Long sessionId;

    @Schema(description = "Type of message sender", example = "PATIENT", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PATIENT", "AI"})
    @NotNull(message = "senderType is required")
    private SenderType senderType;

    @Schema(description = "Content of the message", example = "Hello, I have a question about my medication.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "content is required")
    private String content;

    @Schema(description = "Type of message content", example = "TEXT", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"TEXT", "IMAGE", "SYSTEM"})
    @NotNull(message = "messageType is required")
    private MessageType messageType;

    @Schema(description = "ID of the message this is replying to (optional)", example = "5")
    private Long replyTo;
}
