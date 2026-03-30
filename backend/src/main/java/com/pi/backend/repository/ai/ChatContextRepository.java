package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ChatContext;

/**
 * Repository for managing ChatContext entities.
 */
public interface ChatContextRepository extends JpaRepository<ChatContext, Long> {
}
