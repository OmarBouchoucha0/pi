package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ChatMessage;

/**
 * Repository for managing ChatMessage entities.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
