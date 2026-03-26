package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ChatAlert;

public interface ChatAlertRepository extends JpaRepository<ChatAlert, Long> {
}
