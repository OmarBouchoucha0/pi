package tn.esprit.pi.repository.aichatbot;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.pi.entity.aichatbot.ChatContext;

public interface ChatContextRepository extends JpaRepository<ChatContext, Long> {

    List<ChatContext> findBySessionId(Long sessionId);

    Optional<ChatContext> findBySessionIdAndKey(Long sessionId, String key);

    boolean existsBySessionIdAndKey(Long sessionId, String key);

    void deleteBySessionIdAndKey(Long sessionId, String key);
}
