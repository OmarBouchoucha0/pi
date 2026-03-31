package com.pi.backend.controller.ai;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.backend.dto.ai.AddMessageRequest;
import com.pi.backend.dto.ai.CreateSessionRequest;
import com.pi.backend.model.ai.ChatMessage;
import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.service.ai.ChatSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing AI chat sessions.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    /**
     * Creates a new chat session for a patient.
     *
     * @param request the session creation request
     * @return the created session
     */
    @PostMapping("/sessions")
    public ResponseEntity<ChatSession> createSession(@Valid @RequestBody CreateSessionRequest request) {
        ChatSession session = chatSessionService.createSession(request.patientId(), request.tenantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    /**
     * Adds a message to an existing chat session.
     *
     * @param sessionId the ID of the session
     * @param request   the message request
     * @return the created message
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ChatMessage> addMessage(@PathVariable Long sessionId,
                                                  @Valid @RequestBody AddMessageRequest request) {
        ChatMessage message = chatSessionService.addMessage(
            sessionId, request.senderType(), request.messageType(),
            request.content(), request.intent(), request.confidence(), request.replyToId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}
