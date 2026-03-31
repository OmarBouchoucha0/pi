package com.pi.backend.service.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

/**
 * Unit tests for {@link ChatSessionService}. Uses Mockito to mock repositories
 * and verify service logic for session creation, messaging, and context management.
 */
@ExtendWith(MockitoExtension.class)
class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatContextRepository chatContextRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private ChatSessionService chatSessionService;

    /**
     * Verifies that a chat session can be created successfully with valid patient and tenant.
     */
    @Test
    void createSession_success() {
        User patient = createMockUser();
        Tenant tenant = createMockTenant();
        ChatSession session = createMockSession(patient, tenant);

        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);

        ChatSession result = chatSessionService.createSession(1L, 1L);

        assertNotNull(result);
        assertEquals(ChatSessionStatus.ACTIVE, result.getStatus());
        verify(chatSessionRepository).save(any(ChatSession.class));
    }

    /**
     * Verifies that creating a session with a non-existent patient throws {@link ResourceNotFoundException}.
     */
    @Test
    void createSession_patientNotFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            chatSessionService.createSession(999L, 1L);
        });
    }

    /**
     * Verifies that creating a session with a non-existent tenant throws {@link ResourceNotFoundException}.
     */
    @Test
    void createSession_tenantNotFound() {
        User patient = createMockUser();
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            chatSessionService.createSession(1L, 1L);
        });
    }

    /**
     * Verifies that a message can be added to an existing session successfully.
     */
    @Test
    void addMessage_success() {
        ChatSession session = createMockSession(createMockUser(), createMockTenant());
        ChatMessage message = createMockMessage(session);

        when(chatSessionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(message);

        ChatMessage result = chatSessionService.addMessage(1L, SenderType.PATIENT,
            MessageType.TEXT, "Hello", null, null, null);

        assertNotNull(result);
        verify(chatMessageRepository).save(any(ChatMessage.class));
        verify(chatSessionRepository).save(any(ChatSession.class));
    }

    /**
     * Verifies that adding a message to a non-existent session throws {@link ResourceNotFoundException}.
     */
    @Test
    void addMessage_sessionNotFound() {
        when(chatSessionRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            chatSessionService.addMessage(999L, SenderType.PATIENT, MessageType.TEXT, "Hello", null, null, null);
        });
    }

    /**
     * Verifies that adding a message with a reply-to reference works correctly.
     */
    @Test
    void addMessage_withReplyTo() {
        ChatSession session = createMockSession(createMockUser(), createMockTenant());
        ChatMessage original = createMockMessage(session);
        ChatMessage reply = createMockMessage(session);

        when(chatSessionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(original));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(reply);

        ChatMessage result = chatSessionService.addMessage(1L, SenderType.AI,
            MessageType.QUESTION, "How long?", "follow_up", 0.9f, 1L);

        assertNotNull(result);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    /**
     * Verifies that adding a message with a non-existent reply-to throws {@link ResourceNotFoundException}.
     */
    @Test
    void addMessage_replyToNotFound() {
        ChatSession session = createMockSession(createMockUser(), createMockTenant());

        when(chatSessionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            chatSessionService.addMessage(1L, SenderType.AI, MessageType.QUESTION, "How long?", null, null, 999L);
        });
    }

    /**
     * Verifies that a context value can be set for a new session.
     */
    @Test
    void setContext_newContext() {
        ChatSession session = createMockSession(createMockUser(), createMockTenant());
        ChatContext context = new ChatContext();
        context.setId(1L);
        context.setKey("symptoms");
        context.setValue("[\"headache\"]");

        when(chatSessionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(session));
        when(chatContextRepository.findBySessionIdAndKey(1L, "symptoms")).thenReturn(Optional.empty());
        when(chatContextRepository.save(any(ChatContext.class))).thenReturn(context);

        ChatContext result = chatSessionService.setContext(1L, "symptoms", "[\"headache\"]");

        assertNotNull(result);
        assertEquals("symptoms", result.getKey());
        verify(chatContextRepository).save(any(ChatContext.class));
    }

    /**
     * Verifies that an existing context value can be updated.
     */
    @Test
    void setContext_updateExisting() {
        ChatSession session = createMockSession(createMockUser(), createMockTenant());
        ChatContext existing = new ChatContext();
        existing.setId(1L);
        existing.setKey("symptoms");
        existing.setValue("[\"headache\"]");

        when(chatSessionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(session));
        when(chatContextRepository.findBySessionIdAndKey(1L, "symptoms")).thenReturn(Optional.of(existing));
        when(chatContextRepository.save(any(ChatContext.class))).thenReturn(existing);

        ChatContext result = chatSessionService.setContext(1L, "symptoms", "[\"headache\", \"fever\"]");

        assertNotNull(result);
        assertEquals("[\"headache\", \"fever\"]", result.getValue());
    }

    /**
     * Verifies that setting context for a non-existent session throws {@link ResourceNotFoundException}.
     */
    @Test
    void setContext_sessionNotFound() {
        when(chatSessionRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            chatSessionService.setContext(999L, "symptoms", "[\"headache\"]");
        });
    }

    /**
     * Verifies that a context value can be retrieved by key.
     */
    @Test
    void getContext_success() {
        ChatContext context = new ChatContext();
        context.setKey("symptoms");
        context.setValue("[\"headache\"]");

        when(chatContextRepository.findBySessionIdAndKey(1L, "symptoms")).thenReturn(Optional.of(context));

        String result = chatSessionService.getContext(1L, "symptoms");

        assertEquals("[\"headache\"]", result);
    }

    /**
     * Verifies that getContext returns null when no context exists for the key.
     */
    @Test
    void getContext_notFound() {
        when(chatContextRepository.findBySessionIdAndKey(1L, "missing")).thenReturn(Optional.empty());

        String result = chatSessionService.getContext(1L, "missing");
        assertNull(result);
    }

    /**
     * Verifies that messages can be retrieved in chronological order.
     */
    @Test
    void getMessages_success() {
        ChatMessage m1 = new ChatMessage();
        ChatMessage m2 = new ChatMessage();

        when(chatMessageRepository.findBySessionIdAndDeletedAtIsNullOrderBySentAtAsc(1L))
            .thenReturn(List.of(m1, m2));

        List<ChatMessage> result = chatSessionService.getMessages(1L);

        assertEquals(2, result.size());
    }

    /**
     * Verifies that a session can be completed successfully.
     */
    @Test
    void completeSession_success() {
        ChatSession session = createMockSession(createMockUser(), createMockTenant());
        session.setStatus(ChatSessionStatus.ACTIVE);

        when(chatSessionRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(session));

        chatSessionService.completeSession(1L);

        verify(chatSessionRepository).save(argThat(s -> s.getStatus() == ChatSessionStatus.COMPLETED));
    }

    /**
     * Verifies that completing a non-existent session throws {@link ResourceNotFoundException}.
     */
    @Test
    void completeSession_notFound() {
        when(chatSessionRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            chatSessionService.completeSession(999L);
        });
    }

    private User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("patient@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }

    private Tenant createMockTenant() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("Hospital A");
        return tenant;
    }

    private ChatSession createMockSession(User patient, Tenant tenant) {
        ChatSession session = new ChatSession();
        session.setId(1L);
        session.setPatient(patient);
        session.setTenant(tenant);
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setTotalMessages(0);
        session.setStartedAt(LocalDateTime.now());
        return session;
    }

    private ChatMessage createMockMessage(ChatSession session) {
        ChatMessage message = new ChatMessage();
        message.setId(1L);
        message.setSession(session);
        message.setSenderType(SenderType.PATIENT);
        message.setContent("Hello");
        message.setMessageType(MessageType.TEXT);
        return message;
    }
}
