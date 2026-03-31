package com.pi.backend.repository.ai;

import static com.pi.backend.repository.user.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.model.ai.enums.ChatSessionStatus;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.repository.TenantRepository;
import com.pi.backend.repository.user.UserRepository;

@SpringBootTest
@Transactional
class ChatSessionRepositoryTest {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void saveAndRetrieveSession() {
        Tenant tenant = createTenant(tenantRepository, "Session Hospital");
        User patient = createUser(userRepository, tenant, "patient@test.com", UserRole.PATIENT);

        ChatSession session = new ChatSession();
        session.setPatient(patient);
        session.setTenant(tenant);
        session.setStatus(ChatSessionStatus.ACTIVE);
        session.setTotalMessages(0);
        ChatSession saved = chatSessionRepository.save(session);

        assertNotNull(saved.getId());
        assertEquals(ChatSessionStatus.ACTIVE, saved.getStatus());
        assertEquals(patient.getId(), saved.getPatient().getId());
    }

    @Test
    void findByPatientId() {
        Tenant tenant = createTenant(tenantRepository, "Session Hospital");
        User patient = createUser(userRepository, tenant, "patient@test.com", UserRole.PATIENT);

        ChatSession s1 = new ChatSession();
        s1.setPatient(patient);
        s1.setTenant(tenant);
        s1.setStatus(ChatSessionStatus.ACTIVE);
        chatSessionRepository.save(s1);

        ChatSession s2 = new ChatSession();
        s2.setPatient(patient);
        s2.setTenant(tenant);
        s2.setStatus(ChatSessionStatus.COMPLETED);
        chatSessionRepository.save(s2);

        List<ChatSession> sessions = chatSessionRepository.findByPatientId(patient.getId());
        assertEquals(2, sessions.size());
    }

    @Test
    void findByTenantIdAndStatus() {
        Tenant tenant = createTenant(tenantRepository, "Session Hospital");
        User patient = createUser(userRepository, tenant, "patient@test.com", UserRole.PATIENT);

        ChatSession s1 = new ChatSession();
        s1.setPatient(patient);
        s1.setTenant(tenant);
        s1.setStatus(ChatSessionStatus.ACTIVE);
        chatSessionRepository.save(s1);

        ChatSession s2 = new ChatSession();
        s2.setPatient(patient);
        s2.setTenant(tenant);
        s2.setStatus(ChatSessionStatus.COMPLETED);
        chatSessionRepository.save(s2);

        List<ChatSession> active = chatSessionRepository.findByTenantIdAndStatus(tenant.getId(), ChatSessionStatus.ACTIVE);
        assertEquals(1, active.size());
        assertEquals(ChatSessionStatus.ACTIVE, active.get(0).getStatus());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant(tenantRepository, "Session Hospital");
        User patient = createUser(userRepository, tenant, "patient@test.com", UserRole.PATIENT);

        ChatSession session = new ChatSession();
        session.setPatient(patient);
        session.setTenant(tenant);
        session.setStatus(ChatSessionStatus.ACTIVE);
        ChatSession saved = chatSessionRepository.save(session);

        chatSessionRepository.deleteById(saved.getId());

        List<ChatSession> all = chatSessionRepository.findAll();
        assertTrue(all.isEmpty());
    }
}
