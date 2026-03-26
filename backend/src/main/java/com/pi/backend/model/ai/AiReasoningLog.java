package com.pi.backend.model.ai;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ai_reasoning_logs")
@Data
public class AiReasoningLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Column(name = "input_data", columnDefinition = "JSON")
    private String inputData;

    @Column(name = "output_data", columnDefinition = "JSON")
    private String outputData;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
