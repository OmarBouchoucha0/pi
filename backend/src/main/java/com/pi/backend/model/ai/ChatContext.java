package com.pi.backend.model.ai;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a key-value context entry stored during a chat session.
 */
@Entity
@Table(name = "chat_contexts")
@Getter
@Setter
@ToString(exclude = "session")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatContext {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(nullable = false)
    private String key;

    @Column(name = "`value`", columnDefinition = "JSON")
    private String value;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
