package tn.esprit.pi.repository.aichatbot;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.pi.entity.aichatbot.ChatSession;
import tn.esprit.pi.enums.aichatbot.ChatSessionStatus;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findAllByDeletedAtIsNull();

    List<ChatSession> findByPatientIdAndDeletedAtIsNull(Long patientId);

    List<ChatSession> findByPatientIdAndStatusAndDeletedAtIsNull(Long patientId, ChatSessionStatus status);

    List<ChatSession> findByTenantIdAndDeletedAtIsNull(Long tenantId);

    Optional<ChatSession> findByIdAndPatientIdAndDeletedAtIsNull(Long id, Long patientId);

    List<ChatSession> findByStatusAndDeletedAtIsNull(ChatSessionStatus status);
}
