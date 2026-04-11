package tn.esprit.pi.service.aichatbot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.pi.dto.aichatbot.ChatContextCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatContextResponse;
import tn.esprit.pi.entity.aichatbot.ChatContext;
import tn.esprit.pi.entity.aichatbot.ChatSession;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.aichatbot.ChatContextRepository;
import tn.esprit.pi.repository.aichatbot.ChatSessionRepository;

@ExtendWith(MockitoExtension.class)
class ChatContextServiceTest {

    @Mock
    private ChatContextRepository chatContextRepository;

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @InjectMocks
    private ChatContextService chatContextService;

    private ChatSession session;
    private ChatContext context;

    @BeforeEach
    void setUp() {
        session = ChatSession.builder().id(1L).name("Test Session").build();
        context = ChatContext.builder()
                .id(1L)
                .session(session)
                .key("theme")
                .value("dark")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_shouldReturnAllContexts() {
        when(chatContextRepository.findAll()).thenReturn(List.of(context));

        List<ChatContext> result = chatContextService.findAll();

        assertThat(result).hasSize(1);
        verify(chatContextRepository).findAll();
    }

    @Test
    void findById_shouldReturnContextWhenExists() {
        when(chatContextRepository.findById(1L)).thenReturn(Optional.of(context));

        Optional<ChatContext> result = chatContextService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getKey()).isEqualTo("theme");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(chatContextRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ChatContext> result = chatContextService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findBySessionId_shouldReturnContextsForSession() {
        when(chatContextRepository.findBySessionId(1L)).thenReturn(List.of(context));

        List<ChatContext> result = chatContextService.findBySessionId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKey()).isEqualTo("theme");
    }

    @Test
    void findBySessionIdAndKey_shouldReturnContextWhenFound() {
        when(chatContextRepository.findBySessionIdAndKey(1L, "theme"))
                .thenReturn(Optional.of(context));

        Optional<ChatContext> result = chatContextService.findBySessionIdAndKey(1L, "theme");

        assertThat(result).isPresent();
        assertThat(result.get().getValue()).isEqualTo("dark");
    }

    @Test
    void findBySessionIdAndKey_shouldReturnEmptyWhenNotFound() {
        when(chatContextRepository.findBySessionIdAndKey(1L, "unknown"))
                .thenReturn(Optional.empty());

        Optional<ChatContext> result = chatContextService.findBySessionIdAndKey(1L, "unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void existsBySessionIdAndKey_shouldReturnTrueWhenExists() {
        when(chatContextRepository.existsBySessionIdAndKey(1L, "theme")).thenReturn(true);

        boolean result = chatContextService.existsBySessionIdAndKey(1L, "theme");

        assertThat(result).isTrue();
    }

    @Test
    void existsBySessionIdAndKey_shouldReturnFalseWhenNotExists() {
        when(chatContextRepository.existsBySessionIdAndKey(1L, "unknown")).thenReturn(false);

        boolean result = chatContextService.existsBySessionIdAndKey(1L, "unknown");

        assertThat(result).isFalse();
    }

    @Test
    void create_shouldReturnResponse() {
        ChatContextCreateRequest request = new ChatContextCreateRequest();
        request.setSessionId(1L);
        request.setKey("theme");
        request.setValue("dark");

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatContextRepository.findBySessionIdAndKey(1L, "theme")).thenReturn(Optional.empty());
        when(chatContextRepository.save(any(ChatContext.class))).thenAnswer(invocation -> {
            ChatContext c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        ChatContextResponse response = chatContextService.create(request);

        assertThat(response.getKey()).isEqualTo("theme");
        assertThat(response.getValue()).isEqualTo("dark");
        assertThat(response.getSessionId()).isEqualTo(1L);
        verify(chatContextRepository).save(any(ChatContext.class));
    }

    @Test
    void create_shouldThrowWhenSessionNotFound() {
        ChatContextCreateRequest request = new ChatContextCreateRequest();
        request.setSessionId(99L);
        request.setKey("theme");
        request.setValue("dark");

        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatContextService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Chat session not found with id: 99");
    }

    @Test
    void create_shouldThrowWhenKeyAlreadyExists() {
        ChatContextCreateRequest request = new ChatContextCreateRequest();
        request.setSessionId(1L);
        request.setKey("theme");
        request.setValue("dark");

        ChatContext existing = ChatContext.builder().id(1L).key("theme").build();
        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatContextRepository.findBySessionIdAndKey(1L, "theme")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> chatContextService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Chat context key already exists");
    }

    @Test
    void deleteBySessionIdAndKey_shouldDeleteContext() {
        when(chatContextRepository.findBySessionIdAndKey(1L, "theme")).thenReturn(Optional.of(context));

        chatContextService.deleteBySessionIdAndKey(1L, "theme");

        verify(chatContextRepository).delete(context);
    }

    @Test
    void deleteBySessionIdAndKey_shouldThrowWhenNotFound() {
        when(chatContextRepository.findBySessionIdAndKey(1L, "theme")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatContextService.deleteBySessionIdAndKey(1L, "theme"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Chat context not found for session 1 with key: theme");
    }

    @Test
    void toResponseList_shouldMapContexts() {
        when(chatContextRepository.findAll()).thenReturn(List.of(context));

        List<ChatContextResponse> responses = chatContextService.toResponseList(chatContextService.findAll());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSessionId()).isEqualTo(1L);
        assertThat(responses.get(0).getKey()).isEqualTo("theme");
        assertThat(responses.get(0).getValue()).isEqualTo("dark");
    }
}
