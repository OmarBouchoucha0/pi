package tn.esprit.pi.entity.aichatbot;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_contexts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Column(name = "context_key", nullable = false)
    private String key;

    @Column(name = "\"value\"", columnDefinition = "TEXT")
    private String value;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
