package com.pi.backend.repository.ai;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.ai.AiTriageReport;
import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.model.ai.enums.ChatSessionStatus;
import com.pi.backend.model.ai.enums.SessionType;
import com.pi.backend.model.ai.enums.UrgencyLevel;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;
import com.pi.backend.repository.user.UserRepository;

@SpringBootTest
@Transactional
class AiTriageReportRepositoryTest {

    @Autowired
    private AiTriageReportRepository aiTriageReportRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void saveAndRetrieveReport() {
        ChatSession session = createSession();

        AiTriageReport report = new AiTriageReport();
        report.setSession(session);
        report.setRiskScore(75);
        report.setUrgencyLevel(UrgencyLevel.HIGH);
        report.setDetectedConditions("[\"migraine\", \"dehydration\"]");
        report.setRecommendedAction("Visit ER within 2 hours");
        report.setSummary("Patient shows signs of severe headache with dehydration");
        report.setConfidenceScore(0.88f);

        AiTriageReport saved = aiTriageReportRepository.save(report);

        assertNotNull(saved.getId());
        assertEquals(75, saved.getRiskScore());
        assertEquals(UrgencyLevel.HIGH, saved.getUrgencyLevel());
        assertEquals(0.88f, saved.getConfidenceScore());
        assertNotNull(saved.getGeneratedAt());
    }

    @Test
    void riskScoreValidation() {
        ChatSession session = createSession();

        AiTriageReport report = new AiTriageReport();
        report.setSession(session);
        report.setRiskScore(50);
        report.setUrgencyLevel(UrgencyLevel.MEDIUM);
        report.setConfidenceScore(0.75f);

        AiTriageReport saved = aiTriageReportRepository.save(report);
        assertEquals(50, saved.getRiskScore());
    }

    @Test
    void urgencyLevelEnumPersistence() {
        ChatSession session = createSession();

        AiTriageReport report = new AiTriageReport();
        report.setSession(session);
        report.setRiskScore(95);
        report.setUrgencyLevel(UrgencyLevel.CRITICAL);
        report.setConfidenceScore(0.95f);
        aiTriageReportRepository.save(report);

        AiTriageReport found = aiTriageReportRepository.findById(report.getId()).orElseThrow();
        assertEquals(UrgencyLevel.CRITICAL, found.getUrgencyLevel());
    }

    @Test
    void jsonFieldsStored() {
        ChatSession session = createSession();

        AiTriageReport report = new AiTriageReport();
        report.setSession(session);
        report.setRiskScore(60);
        report.setUrgencyLevel(UrgencyLevel.MEDIUM);
        report.setConfidenceScore(0.80f);
        report.setDetectedConditions("{\"conditions\": [\"flu\", \"cold\"]}");
        aiTriageReportRepository.save(report);

        AiTriageReport found = aiTriageReportRepository.findById(report.getId()).orElseThrow();
        assertNotNull(found.getDetectedConditions());
        assertTrue(found.getDetectedConditions().contains("flu"));
    }

    private ChatSession createSession() {
        Tenant tenant = new Tenant();
        tenant.setName("Triage Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        User user = new User();
        user.setTenant(tenant);
        user.setEmail("triage-patient@test.com");
        user.setPasswordHash("hashed");
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
    void uniqueConstraintOnSession() {
        ChatSession session = createSession();

        AiTriageReport r1 = new AiTriageReport();
        r1.setSession(session);
        r1.setRiskScore(50);
        r1.setUrgencyLevel(UrgencyLevel.MEDIUM);
        r1.setConfidenceScore(0.80f);
        aiTriageReportRepository.save(r1);

        AiTriageReport r2 = new AiTriageReport();
        r2.setSession(session);
        r2.setRiskScore(60);
        r2.setUrgencyLevel(UrgencyLevel.HIGH);
        r2.setConfidenceScore(0.90f);

        assertThrows(DataIntegrityViolationException.class, () -> {
            aiTriageReportRepository.saveAndFlush(r2);
        });
    }
}
