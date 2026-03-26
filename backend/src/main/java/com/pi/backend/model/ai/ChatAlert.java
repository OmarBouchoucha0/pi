package com.pi.backend.model.ai;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.pi.backend.model.ai.enums.AlertType;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chat_alerts")
@Data
public class ChatAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
