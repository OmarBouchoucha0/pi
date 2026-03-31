package com.pi.backend.dto.ai;

import com.pi.backend.model.ai.enums.MessageType;
import com.pi.backend.model.ai.enums.SenderType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for adding a message to a chat session.
 */
public record AddMessageRequest(
    @NotNull(message = "Sender type is required")
    SenderType senderType,

    @NotNull(message = "Message type is required")
    MessageType messageType,

    @NotBlank(message = "Content is required")
    String content,

    String intent,
    Float confidence,
    Long replyToId
) {}
