package com.pi.backend.service.ai;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.ai.ChatContext;
import com.pi.backend.model.ai.ChatMessage;
import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.model.ai.enums.ChatSessionStatus;
import com.pi.backend.model.ai.enums.MessageType;
import com.pi.backend.model.ai.enums.SenderType;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.TenantRepository;
import com.pi.backend.repository.ai.ChatContextRepository;
import com.pi.backend.repository.ai.ChatMessageRepository;
import com.pi.backend.repository.ai.ChatSessionRepository;
import com.pi.backend.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing AI chat sessions and messages.
 */
@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatContextRepository chatContextRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    /**
     * Creates a new chat session for a patient.
     *
     * @param patientId the ID of the patient
     * @param tenantId  the ID of the tenant
     * @return the created ChatSession
     * @throws ResourceNotFoundException if patient or tenant not found
     */
    @Transactional
    public ChatSession createSession(Long patientId, Long tenantId) {
        User patient = userRepository.findByIdAndDeletedAtIsNull(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("User", patientId));

        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));

        ChatSession session = new ChatSession();
        session.setPatient(patient);
        session.setTenant(tenant);
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setTotalMessages(0);

        return chatSessionRepository.save(session);
    }

    /**
     * Adds a message to an existing chat session.
     *
     * @param sessionId   the ID of the session
     * @param senderType  who sent the message (PATIENT or AI)
     * @param messageType the type of message (TEXT, QUESTION, SYSTEM)
     * @param content     the message content
     * @param intent      the detected intent (nullable)
     * @param confidence  the confidence score (nullable)
     * @param replyToId   the ID of the message being replied to (nullable)
     * @return the created ChatMessage
     * @throws ResourceNotFoundException if session not found
     */
    @Transactional
    public ChatMessage addMessage(Long sessionId, SenderType senderType,
                                  MessageType messageType, String content,
                                  String intent, Float confidence, Long replyToId) {
        ChatSession session = chatSessionRepository.findByIdAndDeletedAtIsNull(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("ChatSession", sessionId));

        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setSenderType(senderType);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setIntent(intent);
        message.setConfidenceScore(confidence);

        if (replyToId != null) {
            ChatMessage replyTo = chatMessageRepository.findByIdAndDeletedAtIsNull(replyToId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", replyToId));
            message.setReplyTo(replyTo);
        }

        ChatMessage saved = chatMessageRepository.save(message);

        session.setTotalMessages(session.getTotalMessages() + 1);
        session.setLastActivityAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        return saved;
    }

    /**
     * Sets a context value for a session.
     *
     * @param sessionId the ID of the session
     * @param key       the context key
     * @param value     the context value (JSON)
     * @return the created or updated ChatContext
     * @throws ResourceNotFoundException if session not found
     */
    @Transactional
    public ChatContext setContext(Long sessionId, String key, String value) {
        ChatSession session = chatSessionRepository.findByIdAndDeletedAtIsNull(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("ChatSession", sessionId));

        ChatContext context = chatContextRepository.findBySessionIdAndKey(sessionId, key)
            .orElse(new ChatContext());

        context.setSession(session);
        context.setKey(key);
        context.setValue(value);

        return chatContextRepository.save(context);
    }

    /**
     * Gets a context value for a session.
     *
     * @param sessionId the ID of the session
     * @param key       the context key
     * @return the context value, or null if not found
     */
    @Transactional(readOnly = true)
    public String getContext(Long sessionId, String key) {
        return chatContextRepository.findBySessionIdAndKey(sessionId, key)
            .map(ChatContext::getValue)
            .orElse(null);
    }

    /**
     * Gets all messages for a session in chronological order.
     *
     * @param sessionId the ID of the session
     * @return list of messages ordered by sent time
     */
    @Transactional(readOnly = true)
    public java.util.List<ChatMessage> getMessages(Long sessionId) {
        return chatMessageRepository.findBySessionIdAndDeletedAtIsNullOrderBySentAtAsc(sessionId);
    }

    /**
     * Completes a chat session.
     *
     * @param sessionId the ID of the session
     * @throws ResourceNotFoundException if session not found
     */
    @Transactional
    public void completeSession(Long sessionId) {
        ChatSession session = chatSessionRepository.findByIdAndDeletedAtIsNull(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("ChatSession", sessionId));

        session.setStatus(ChatSessionStatus.COMPLETED);
        session.setEndedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }
}
