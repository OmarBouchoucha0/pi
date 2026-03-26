package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
