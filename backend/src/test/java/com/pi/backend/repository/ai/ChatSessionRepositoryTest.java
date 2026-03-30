package com.pi.backend.repository.ai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.model.ai.enums.ChatSessionStatus;
import com.pi.backend.model.ai.enums.SessionType;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;
import com.pi.backend.repository.user.UserRepository;

/**
 * Integration tests for {@link ChatSessionRepository}. Verifies database operations
 * including CRUD, enum persistence for session types and statuses, and soft delete filtering.
 */
@SpringBootTest
@Transactional
class ChatSessionRepositoryTest {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * Verifies that a chat session can be saved and retrieved from the database.
     */
    @Test
    void saveAndRetrieveSession() {
        User patient = createPatientUser();
        Tenant tenant = patient.getTenant();

        ChatSession session = new ChatSession();
        session.setPatient(patient);
        session.setTenant(tenant);
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setSessionType(SessionType.TRIAGE);
        session.setLanguage("en");

        ChatSession saved = chatSessionRepository.save(session);

        assertNotNull(saved.getId());
        assertEquals(ChatSessionStatus.ACTIVE, saved.getStatus());
        assertEquals(SessionType.TRIAGE, saved.getSessionType());
        assertEquals("en", saved.getLanguage());
        assertEquals(0, saved.getTotalMessages());
        assertNotNull(saved.getStartedAt());
    }

    /**
     * Verifies that ChatSessionStatus and SessionType enums are persisted and retrieved correctly.
     */
    @Test
    void enumPersistence() {
        User patient = createPatientUser();

        ChatSession session = new ChatSession();
        session.setPatient(patient);
        session.setTenant(patient.getTenant());
        session.setStatus(ChatSessionStatus.COMPLETED);
        session.setSessionType(SessionType.FOLLOW_UP);
        session.setLanguage("fr");
        chatSessionRepository.save(session);

        ChatSession found = chatSessionRepository.findById(session.getId()).orElseThrow();
        assertEquals(ChatSessionStatus.COMPLETED, found.getStatus());
        assertEquals(SessionType.FOLLOW_UP, found.getSessionType());
    }

    /**
     * Verifies that soft-deleted chat sessions are excluded from findAll results.
     */
    @Test
    void softDeleteFiltersFromFindAll() {
        User patient = createPatientUser();

        ChatSession session = new ChatSession();
        session.setPatient(patient);
        session.setTenant(patient.getTenant());
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setSessionType(SessionType.GENERAL);
        session.setLanguage("en");
        ChatSession saved = chatSessionRepository.save(session);

        chatSessionRepository.deleteById(saved.getId());

        List<ChatSession> all = chatSessionRepository.findAll();
        assertTrue(all.isEmpty());
    }

    private User createPatientUser() {
        Tenant tenant = new Tenant();
        tenant.setName("Session Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        User user = new User();
        user.setTenant(tenant);
        user.setEmail("session-patient@test.com");
        user.setPasswordHash("hashed");
        user.setFirstName("Session");
        user.setLastName("Patient");
        user.setRole(UserRole.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }
}
