package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ChatSession;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
}
