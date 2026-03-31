package com.pi.backend.controller.ai;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi.backend.dto.ai.AddMessageRequest;
import com.pi.backend.dto.ai.CreateSessionRequest;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.ai.ChatMessage;
import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.model.ai.enums.ChatSessionStatus;
import com.pi.backend.model.ai.enums.MessageType;
import com.pi.backend.model.ai.enums.SenderType;
import com.pi.backend.model.user.User;
import com.pi.backend.service.ai.ChatSessionService;

/**
 * Tests for {@link ChatSessionController}. Uses MockMvc to test HTTP endpoints
 * and verify proper request/response handling and exception mapping.
 */
@SpringBootTest
class ChatSessionControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ChatSessionService chatSessionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * Verifies that POST /api/chat/sessions creates a session and returns 201.
     */
    @Test
    void createSession_success() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest(1L, 1L);
        ChatSession session = createMockSession();
        when(chatSessionService.createSession(1L, 1L)).thenReturn(session);

        mockMvc.perform(post("/api/chat/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.totalMessages").value(0));
    }

    /**
     * Verifies that POST /api/chat/sessions with missing fields returns 400.
     */
    @Test
    void createSession_validationError() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest(null, null);

        mockMvc.perform(post("/api/chat/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.errors.patientId").value("Patient ID is required"))
            .andExpect(jsonPath("$.errors.tenantId").value("Tenant ID is required"));
    }

    /**
     * Verifies that POST /api/chat/sessions with non-existent patient returns 404.
     */
    @Test
    void createSession_patientNotFound() throws Exception {
        CreateSessionRequest request = new CreateSessionRequest(999L, 1L);
        when(chatSessionService.createSession(999L, 1L))
            .thenThrow(new ResourceNotFoundException("User", 999L));

        mockMvc.perform(post("/api/chat/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.resource").value("User"));
    }

    /**
     * Verifies that POST /api/chat/sessions/{id}/messages adds a message and returns 201.
     */
    @Test
    void addMessage_success() throws Exception {
        AddMessageRequest request = new AddMessageRequest(
            SenderType.PATIENT, MessageType.TEXT, "I have a headache", null, null, null
        );
        ChatMessage message = createMockMessage();
        when(chatSessionService.addMessage(eq(1L), eq(SenderType.PATIENT), eq(MessageType.TEXT),
            eq("I have a headache"), isNull(), isNull(), isNull())).thenReturn(message);

        mockMvc.perform(post("/api/chat/sessions/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.content").value("I have a headache"))
            .andExpect(jsonPath("$.senderType").value("PATIENT"));
    }

    /**
     * Verifies that POST /api/chat/sessions/{id}/messages with missing fields returns 400.
     */
    @Test
    void addMessage_validationError() throws Exception {
        AddMessageRequest request = new AddMessageRequest(null, null, "", null, null, null);

        mockMvc.perform(post("/api/chat/sessions/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.errors.senderType").value("Sender type is required"))
            .andExpect(jsonPath("$.errors.messageType").value("Message type is required"))
            .andExpect(jsonPath("$.errors.content").value("Content is required"));
    }

    /**
     * Verifies that POST /api/chat/sessions/{id}/messages with non-existent session returns 404.
     */
    @Test
    void addMessage_sessionNotFound() throws Exception {
        AddMessageRequest request = new AddMessageRequest(
            SenderType.PATIENT, MessageType.TEXT, "Hello", null, null, null
        );
        when(chatSessionService.addMessage(eq(999L), any(), any(), any(), any(), any(), any()))
            .thenThrow(new ResourceNotFoundException("ChatSession", 999L));

        mockMvc.perform(post("/api/chat/sessions/999/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.resource").value("ChatSession"));
    }

    /**
     * Verifies that POST /api/chat/sessions/{id}/messages with reply-to works correctly.
     */
    @Test
    void addMessage_withReplyTo() throws Exception {
        AddMessageRequest request = new AddMessageRequest(
            SenderType.AI, MessageType.QUESTION, "How long?", "follow_up", 0.9f, 1L
        );
        ChatMessage message = createMockMessage();
        when(chatSessionService.addMessage(eq(1L), eq(SenderType.AI), eq(MessageType.QUESTION),
            eq("How long?"), eq("follow_up"), eq(0.9f), eq(1L))).thenReturn(message);

        mockMvc.perform(post("/api/chat/sessions/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    private ChatSession createMockSession() {
        ChatSession session = new ChatSession();
        session.setId(1L);
        session.setPatient(createMockUser());
        session.setTenant(createMockTenant());
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setTotalMessages(0);
        session.setStartedAt(LocalDateTime.now());
        return session;
    }

    private ChatMessage createMockMessage() {
        ChatMessage message = new ChatMessage();
        message.setId(1L);
        message.setSenderType(SenderType.PATIENT);
        message.setContent("I have a headache");
        message.setMessageType(MessageType.TEXT);
        message.setSentAt(LocalDateTime.now());
        return message;
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
}
