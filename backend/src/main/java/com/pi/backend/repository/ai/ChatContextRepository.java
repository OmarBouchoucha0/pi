package com.pi.backend.repository.ai;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ChatContext;

/**
 * Repository for managing ChatContext entities.
 */
public interface ChatContextRepository extends JpaRepository<ChatContext, Long> {

    Optional<ChatContext> findBySessionIdAndKey(Long sessionId, String key);

    List<ChatContext> findBySessionId(Long sessionId);

    void deleteBySessionId(Long sessionId);

    Optional<ChatContext> findById(Long id);
}
