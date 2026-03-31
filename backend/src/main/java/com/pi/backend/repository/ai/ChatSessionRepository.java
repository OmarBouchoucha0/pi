package com.pi.backend.repository.ai;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ChatSession;
import com.pi.backend.model.ai.enums.ChatSessionStatus;

/**
 * Repository for managing ChatSession entities.
 */
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findByPatientId(Long patientId);

    List<ChatSession> findByPatientIdAndDeletedAtIsNull(Long patientId);

    List<ChatSession> findByTenantId(Long tenantId);

    List<ChatSession> findByTenantIdAndDeletedAtIsNull(Long tenantId);

    List<ChatSession> findByTenantIdAndStatus(Long tenantId, ChatSessionStatus status);

    List<ChatSession> findByTenantIdAndStatusAndDeletedAtIsNull(Long tenantId, ChatSessionStatus status);

    List<ChatSession> findByStatus(ChatSessionStatus status);

    List<ChatSession> findByStatusAndDeletedAtIsNull(ChatSessionStatus status);

    Optional<ChatSession> findByIdAndDeletedAtIsNull(Long id);

    long countByTenantIdAndStatus(Long tenantId, ChatSessionStatus status);
}
