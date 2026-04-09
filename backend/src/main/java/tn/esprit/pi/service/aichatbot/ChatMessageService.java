package tn.esprit.pi.service.aichatbot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.aichatbot.ChatMessageCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatMessageResponse;
import tn.esprit.pi.entity.aichatbot.ChatMessage;
import tn.esprit.pi.entity.aichatbot.ChatSession;
import tn.esprit.pi.enums.aichatbot.MessageType;
import tn.esprit.pi.enums.aichatbot.SenderType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.aichatbot.ChatMessageRepository;
import tn.esprit.pi.repository.aichatbot.ChatSessionRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    public List<ChatMessage> findAll() {
        return chatMessageRepository.findAllByDeletedAtIsNull();
    }

    public Optional<ChatMessage> findById(Long id) {
        return chatMessageRepository.findById(id);
    }

    public List<ChatMessage> findBySessionId(Long sessionId) {
        return chatMessageRepository.findBySessionIdAndDeletedAtIsNullOrderBySentAtAsc(sessionId);
    }

    public List<ChatMessage> findBySessionIdAndSenderType(Long sessionId, SenderType senderType) {
        return chatMessageRepository.findBySessionIdAndSenderTypeAndDeletedAtIsNull(sessionId, senderType);
    }

    public List<ChatMessage> findBySessionIdAndMessageType(Long sessionId, MessageType messageType) {
        return chatMessageRepository.findBySessionIdAndMessageTypeAndDeletedAtIsNull(sessionId, messageType);
    }

    public ChatMessageResponse create(ChatMessageCreateRequest request) {
        ChatSession session = chatSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + request.getSessionId()));

        ChatMessage message = ChatMessage.builder()
                .session(session)
                .senderType(request.getSenderType())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .build();

        if (request.getReplyTo() != null) {
            ChatMessage replyTo = chatMessageRepository.findById(request.getReplyTo())
                    .orElseThrow(() -> new ResourceNotFoundException("Reply-to message not found with id: " + request.getReplyTo()));
            message.setReplyTo(replyTo);
        }

        return toResponse(chatMessageRepository.save(message));
    }

    public void softDelete(Long id) {
        ChatMessage message = chatMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat message not found with id: " + id));
        message.setDeletedAt(LocalDateTime.now());
        chatMessageRepository.save(message);
    }

    public List<ChatMessageResponse> toResponseList(List<ChatMessage> messages) {
        return messages.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .sessionId(message.getSession() != null ? message.getSession().getId() : null)
                .senderType(message.getSenderType())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .replyTo(message.getReplyTo() != null ? message.getReplyTo().getId() : null)
                .sentAt(message.getSentAt())
                .build();
    }
}
