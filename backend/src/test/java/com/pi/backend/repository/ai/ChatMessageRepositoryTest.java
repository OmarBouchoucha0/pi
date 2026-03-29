package com.pi.backend.repository.ai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.ai.ChatMessage;
import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.model.ai.enums.ChatSessionStatus;
import com.pi.backend.model.ai.enums.MessageType;
import com.pi.backend.model.ai.enums.SenderType;
import com.pi.backend.model.ai.enums.SessionType;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;
import com.pi.backend.repository.user.UserRepository;

@SpringBootTest
@Transactional
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void saveAndRetrieveMessage() {
        ChatSession session = createSession();

        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setSenderType(SenderType.PATIENT);
        message.setContent("I have a headache");
        message.setMessageType(MessageType.TEXT);

        ChatMessage saved = chatMessageRepository.save(message);

        assertNotNull(saved.getId());
        assertEquals("I have a headache", saved.getContent());
        assertEquals(SenderType.PATIENT, saved.getSenderType());
        assertEquals(MessageType.TEXT, saved.getMessageType());
        assertNotNull(saved.getSentAt());
    }

    @Test
    void replyToSelfReference() {
        ChatSession session = createSession();

        ChatMessage original = new ChatMessage();
        original.setSession(session);
        original.setSenderType(SenderType.PATIENT);
        original.setContent("I have a headache");
        original.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(original);

        ChatMessage reply = new ChatMessage();
        reply.setSession(session);
        reply.setSenderType(SenderType.AI);
        reply.setContent("How long have you had it?");
        reply.setMessageType(MessageType.QUESTION);
        reply.setReplyTo(original);
        chatMessageRepository.save(reply);

        ChatMessage found = chatMessageRepository.findById(reply.getId()).orElseThrow();
        assertNotNull(found.getReplyTo());
        assertEquals(original.getId(), found.getReplyTo().getId());
    }

    @Test
    void intentAndConfidenceScore() {
        ChatSession session = createSession();

        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setSenderType(SenderType.AI);
        message.setContent("Can you describe the pain?");
        message.setMessageType(MessageType.QUESTION);
        message.setIntent("symptom_inquiry");
        message.setConfidenceScore(0.92f);
        chatMessageRepository.save(message);

        ChatMessage found = chatMessageRepository.findById(message.getId()).orElseThrow();
        assertEquals("symptom_inquiry", found.getIntent());
        assertEquals(0.92f, found.getConfidenceScore());
    }

    private ChatSession createSession() {
        Tenant tenant = new Tenant();
        tenant.setName("Message Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        User user = new User();
        user.setTenant(tenant);
        user.setEmail("message-patient@test.com");
        user.setPasswordHash("hashed");
        user.setFirstName("Message");
        user.setLastName("Patient");
        user.setRole(UserRole.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        ChatSession session = new ChatSession();
        session.setPatient(user);
        session.setTenant(tenant);
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setSessionType(SessionType.TRIAGE);
        session.setLanguage("en");
        return chatSessionRepository.save(session);
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        ChatSession session = createSession();

        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setSenderType(SenderType.PATIENT);
        message.setContent("Test message");
        message.setMessageType(MessageType.TEXT);
        ChatMessage saved = chatMessageRepository.save(message);

        chatMessageRepository.deleteById(saved.getId());

        List<ChatMessage> all = chatMessageRepository.findAll();
        assertTrue(all.isEmpty());
    }
}
