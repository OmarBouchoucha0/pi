package com.pi.backend.repository.ai;

import static com.pi.backend.repository.user.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.ai.ChatMessage;
import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.model.ai.enums.ChatSessionStatus;
import com.pi.backend.model.ai.enums.MessageType;
import com.pi.backend.model.ai.enums.SenderType;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
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
    void findBySessionIdOrderBySentAtAsc() {
        ChatSession session = createSession();

        ChatMessage m1 = new ChatMessage();
        m1.setSession(session);
        m1.setSenderType(SenderType.PATIENT);
        m1.setContent("Hello");
        m1.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(m1);

        ChatMessage m2 = new ChatMessage();
        m2.setSession(session);
        m2.setSenderType(SenderType.AI);
        m2.setContent("Hi there");
        m2.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(m2);

        List<ChatMessage> messages = chatMessageRepository.findBySessionIdAndDeletedAtIsNullOrderBySentAtAsc(session.getId());
        assertEquals(2, messages.size());
    }

    @Test
    void findBySessionIdAndSenderType() {
        ChatSession session = createSession();

        ChatMessage m1 = new ChatMessage();
        m1.setSession(session);
        m1.setSenderType(SenderType.PATIENT);
        m1.setContent("Hello");
        m1.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(m1);

        ChatMessage m2 = new ChatMessage();
        m2.setSession(session);
        m2.setSenderType(SenderType.AI);
        m2.setContent("Hi");
        m2.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(m2);

        List<ChatMessage> patientMessages = chatMessageRepository.findBySessionIdAndSenderType(session.getId(), SenderType.PATIENT);
        assertEquals(1, patientMessages.size());
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
    void countBySessionId() {
        ChatSession session = createSession();

        ChatMessage m1 = new ChatMessage();
        m1.setSession(session);
        m1.setSenderType(SenderType.PATIENT);
        m1.setContent("Hello");
        m1.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(m1);

        ChatMessage m2 = new ChatMessage();
        m2.setSession(session);
        m2.setSenderType(SenderType.AI);
        m2.setContent("Hi");
        m2.setMessageType(MessageType.TEXT);
        chatMessageRepository.save(m2);

        long count = chatMessageRepository.countBySessionId(session.getId());
        assertEquals(2, count);
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

    private ChatSession createSession() {
        Tenant tenant = createTenant(tenantRepository, "Message Hospital");
        User patient = createUser(userRepository, tenant, "message-patient@test.com", UserRole.PATIENT);

        ChatSession session = new ChatSession();
        session.setPatient(patient);
        session.setTenant(tenant);
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setTotalMessages(0);
        return chatSessionRepository.save(session);
    }
}
