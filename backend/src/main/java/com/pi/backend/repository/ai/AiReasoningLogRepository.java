package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.AiReasoningLog;

/**
 * Repository for managing AiReasoningLog entities.
 */
public interface AiReasoningLogRepository extends JpaRepository<AiReasoningLog, Long> {
}
