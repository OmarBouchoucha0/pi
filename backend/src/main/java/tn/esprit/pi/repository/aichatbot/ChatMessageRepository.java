package tn.esprit.pi.repository.aichatbot;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.pi.entity.aichatbot.ChatMessage;
import tn.esprit.pi.enums.aichatbot.MessageType;
import tn.esprit.pi.enums.aichatbot.SenderType;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByDeletedAtIsNull();

    List<ChatMessage> findBySessionIdAndDeletedAtIsNullOrderBySentAtAsc(Long sessionId);

    List<ChatMessage> findBySessionIdAndSenderTypeAndDeletedAtIsNull(Long sessionId, SenderType senderType);

    List<ChatMessage> findBySessionIdAndMessageTypeAndDeletedAtIsNull(Long sessionId, MessageType messageType);
}
