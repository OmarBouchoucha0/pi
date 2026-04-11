package tn.esprit.pi.service.aichatbot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.aichatbot.ChatSessionCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatSessionResponse;
import tn.esprit.pi.dto.aichatbot.ChatSessionUpdateRequest;
import tn.esprit.pi.entity.aichatbot.ChatSession;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.aichatbot.ChatSessionStatus;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.aichatbot.ChatSessionRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    public List<ChatSession> findAll() {
        return chatSessionRepository.findAllByDeletedAtIsNull();
    }

    public Optional<ChatSession> findById(Long id) {
        return chatSessionRepository.findById(id);
    }

    public List<ChatSession> findByPatientId(Long patientId) {
        return chatSessionRepository.findByPatientIdAndDeletedAtIsNull(patientId);
    }

    public List<ChatSession> findByPatientIdAndStatus(Long patientId, ChatSessionStatus status) {
        return chatSessionRepository.findByPatientIdAndStatusAndDeletedAtIsNull(patientId, status);
    }

    public List<ChatSession> findByTenantId(Long tenantId) {
        return chatSessionRepository.findByTenantIdAndDeletedAtIsNull(tenantId);
    }

    public Optional<ChatSession> findByIdAndPatientId(Long id, Long patientId) {
        return chatSessionRepository.findByIdAndPatientIdAndDeletedAtIsNull(id, patientId);
    }

    public List<ChatSession> findByStatus(ChatSessionStatus status) {
        return chatSessionRepository.findByStatusAndDeletedAtIsNull(status);
    }

    public ChatSessionResponse create(ChatSessionCreateRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));
        User patient = userRepository.findByIdAndDeletedAtIsNull(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        ChatSession session = ChatSession.builder()
                .name(request.getName())
                .tenant(tenant)
                .patient(patient)
                .build();

        return toResponse(chatSessionRepository.save(session));
    }

    public ChatSessionResponse update(Long id, ChatSessionUpdateRequest request) {
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + id));

        if (request.getName() != null) session.setName(request.getName());

        return toResponse(chatSessionRepository.save(session));
    }

    public void softDelete(Long id) {
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + id));
        session.setDeletedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }

    public void endSession(Long id) {
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + id));
        session.setStatus(ChatSessionStatus.COMPLETED);
        session.setEndedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }

    public ChatSessionResponse toResponse(Long id) {
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + id));
        return toResponse(session);
    }

    public List<ChatSessionResponse> toResponseList(List<ChatSession> sessions) {
        return sessions.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ChatSessionResponse toResponse(ChatSession session) {
        return ChatSessionResponse.builder()
                .id(session.getId())
                .name(session.getName())
                .status(session.getStatus())
                .patientId(session.getPatient() != null ? session.getPatient().getId() : null)
                .patientName(session.getPatient() != null ? session.getPatient().getFirstName() + " " + session.getPatient().getLastName() : null)
                .totalMessages(session.getTotalMessages())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .lastActivityAt(session.getLastActivityAt())
                .build();
    }
}
