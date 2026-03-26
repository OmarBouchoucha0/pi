package com.pi.backend.model.ai;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Entity
@Table(name = "ai_reasoning_logs")
@Getter
@Setter
@ToString(exclude = "session")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SQLDelete(sql = "UPDATE ai_reasoning_logs SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class AiReasoningLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
