package com.pi.backend.model.ai;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.pi.backend.model.ai.enums.SymptomSeverity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "extracted_symptoms")
@Data
public class ExtractedSymptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(name = "symptom_name", nullable = false)
    private String symptomName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SymptomSeverity severity;

    @Column(nullable = false)
    private String duration;

    @CreationTimestamp
    @Column(name = "first_detected_at", updatable = false)
    private LocalDateTime firstDetectedAt;
}
