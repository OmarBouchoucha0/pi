package tn.esprit.pi.service.aichatbot;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.aichatbot.ChatContextCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatContextResponse;
import tn.esprit.pi.entity.aichatbot.ChatContext;
import tn.esprit.pi.entity.aichatbot.ChatSession;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.aichatbot.ChatContextRepository;
import tn.esprit.pi.repository.aichatbot.ChatSessionRepository;

@Service
@RequiredArgsConstructor
public class ChatContextService {

    private final ChatContextRepository chatContextRepository;
    private final ChatSessionRepository chatSessionRepository;

    public List<ChatContext> findAll() {
        return chatContextRepository.findAll();
    }

    public Optional<ChatContext> findById(Long id) {
        return chatContextRepository.findById(id);
    }

    public List<ChatContext> findBySessionId(Long sessionId) {
        return chatContextRepository.findBySessionId(sessionId);
    }

    public Optional<ChatContext> findBySessionIdAndKey(Long sessionId, String key) {
        return chatContextRepository.findBySessionIdAndKey(sessionId, key);
    }

    public boolean existsBySessionIdAndKey(Long sessionId, String key) {
        return chatContextRepository.existsBySessionIdAndKey(sessionId, key);
    }

    public ChatContextResponse create(ChatContextCreateRequest request) {
        ChatSession session = chatSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + request.getSessionId()));

        chatContextRepository.findBySessionIdAndKey(request.getSessionId(), request.getKey())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Chat context key already exists for this session: " + request.getKey());
                });

        ChatContext context = ChatContext.builder()
                .session(session)
                .key(request.getKey())
                .value(request.getValue())
                .build();

        return toResponse(chatContextRepository.save(context));
    }

    public void deleteBySessionIdAndKey(Long sessionId, String key) {
        ChatContext context = chatContextRepository.findBySessionIdAndKey(sessionId, key)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chat context not found for session " + sessionId + " with key: " + key));
        chatContextRepository.delete(context);
    }

    public List<ChatContextResponse> toResponseList(List<ChatContext> contexts) {
        return contexts.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ChatContextResponse toResponse(ChatContext context) {
        return ChatContextResponse.builder()
                .id(context.getId())
                .sessionId(context.getSession() != null ? context.getSession().getId() : null)
                .key(context.getKey())
                .value(context.getValue())
                .createdAt(context.getCreatedAt())
                .build();
    }
}
