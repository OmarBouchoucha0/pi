package com.pi.backend.model.ai;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.pi.backend.model.ai.enums.UrgencyLevel;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ai_triage_reports")
@Data
public class AiTriageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private ChatSession session;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false)
    private UrgencyLevel urgencyLevel;

    @Column(name = "detected_conditions", columnDefinition = "JSON")
    private String detectedConditions;

    @Column(name = "recommended_action", columnDefinition = "TEXT")
    private String recommendedAction;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "confidence_score", nullable = false)
    private Float confidenceScore;

    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
}
