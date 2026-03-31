package com.pi.backend.repository.ai;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ChatMessage;
import com.pi.backend.model.ai.enums.MessageType;
import com.pi.backend.model.ai.enums.SenderType;

/**
 * Repository for managing ChatMessage entities.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionIdOrderBySentAtAsc(Long sessionId);

    List<ChatMessage> findBySessionIdAndDeletedAtIsNullOrderBySentAtAsc(Long sessionId);

    List<ChatMessage> findBySessionIdAndSenderType(Long sessionId, SenderType senderType);

    List<ChatMessage> findBySessionIdAndSenderTypeAndDeletedAtIsNull(Long sessionId, SenderType senderType);

    List<ChatMessage> findBySessionIdAndMessageType(Long sessionId, MessageType messageType);

    List<ChatMessage> findBySessionIdAndIntent(Long sessionId, String intent);

    List<ChatMessage> findBySessionIdAndSenderTypeAndMessageType(Long sessionId, SenderType senderType, MessageType messageType);

    Optional<ChatMessage> findByIdAndDeletedAtIsNull(Long id);

    long countBySessionId(Long sessionId);

    long countBySessionIdAndSenderType(Long sessionId, SenderType senderType);
}
