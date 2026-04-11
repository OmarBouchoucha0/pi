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

import tn.esprit.pi.dto.aichatbot.ChatMessageCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatMessageResponse;
import tn.esprit.pi.entity.aichatbot.ChatMessage;
import tn.esprit.pi.entity.aichatbot.ChatSession;
import tn.esprit.pi.enums.aichatbot.MessageType;
import tn.esprit.pi.enums.aichatbot.SenderType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.aichatbot.ChatMessageRepository;
import tn.esprit.pi.repository.aichatbot.ChatSessionRepository;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private ChatSession session;
    private ChatMessage message;

    @BeforeEach
    void setUp() {
        session = ChatSession.builder().id(1L).name("Test Session").build();
        message = ChatMessage.builder()
                .id(1L)
                .session(session)
                .senderType(SenderType.PATIENT)
                .content("Hello")
                .messageType(MessageType.TEXT)
                .sentAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_shouldReturnNonDeletedMessages() {
        when(chatMessageRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(message));

        List<ChatMessage> result = chatMessageService.findAll();

        assertThat(result).hasSize(1);
        verify(chatMessageRepository).findAllByDeletedAtIsNull();
    }

    @Test
    void findById_shouldReturnMessageWhenExists() {
        when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(message));

        Optional<ChatMessage> result = chatMessageService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo("Hello");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(chatMessageRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ChatMessage> result = chatMessageService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findBySessionId_shouldReturnMessagesForSession() {
        when(chatMessageRepository.findBySessionIdAndDeletedAtIsNullOrderBySentAtAsc(1L))
                .thenReturn(List.of(message));

        List<ChatMessage> result = chatMessageService.findBySessionId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Hello");
    }

    @Test
    void findBySessionIdAndSenderType_shouldReturnFilteredMessages() {
        when(chatMessageRepository.findBySessionIdAndSenderTypeAndDeletedAtIsNull(1L, SenderType.PATIENT))
                .thenReturn(List.of(message));

        List<ChatMessage> result = chatMessageService.findBySessionIdAndSenderType(1L, SenderType.PATIENT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSenderType()).isEqualTo(SenderType.PATIENT);
    }

    @Test
    void findBySessionIdAndMessageType_shouldReturnFilteredMessages() {
        when(chatMessageRepository.findBySessionIdAndMessageTypeAndDeletedAtIsNull(1L, MessageType.TEXT))
                .thenReturn(List.of(message));

        List<ChatMessage> result = chatMessageService.findBySessionIdAndMessageType(1L, MessageType.TEXT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessageType()).isEqualTo(MessageType.TEXT);
    }

    @Test
    void create_shouldReturnResponse() {
        ChatMessageCreateRequest request = new ChatMessageCreateRequest();
        request.setSessionId(1L);
        request.setSenderType(SenderType.PATIENT);
        request.setContent("Hello");
        request.setMessageType(MessageType.TEXT);

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        ChatMessageResponse response = chatMessageService.create(request);

        assertThat(response.getContent()).isEqualTo("Hello");
        assertThat(response.getSenderType()).isEqualTo(SenderType.PATIENT);
        assertThat(response.getMessageType()).isEqualTo(MessageType.TEXT);
        assertThat(response.getSessionId()).isEqualTo(1L);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void create_shouldThrowWhenSessionNotFound() {
        ChatMessageCreateRequest request = new ChatMessageCreateRequest();
        request.setSessionId(99L);
        request.setSenderType(SenderType.PATIENT);
        request.setContent("Hello");
        request.setMessageType(MessageType.TEXT);

        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatMessageService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Chat session not found with id: 99");
    }

    @Test
    void create_withReplyTo_shouldSetReplyTo() {
        ChatMessageCreateRequest request = new ChatMessageCreateRequest();
        request.setSessionId(1L);
        request.setSenderType(SenderType.PATIENT);
        request.setContent("Reply");
        request.setMessageType(MessageType.TEXT);
        request.setReplyTo(10L);

        ChatMessage parentMessage = ChatMessage.builder()
                .id(10L)
                .content("Original")
                .build();

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.findById(10L)).thenReturn(Optional.of(parentMessage));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage m = invocation.getArgument(0);
            m.setId(2L);
            return m;
        });

        ChatMessageResponse response = chatMessageService.create(request);

        assertThat(response.getReplyTo()).isEqualTo(10L);
    }

    @Test
    void create_shouldThrowWhenReplyToNotFound() {
        ChatMessageCreateRequest request = new ChatMessageCreateRequest();
        request.setSessionId(1L);
        request.setSenderType(SenderType.PATIENT);
        request.setContent("Reply");
        request.setMessageType(MessageType.TEXT);
        request.setReplyTo(99L);

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatMessageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatMessageService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reply-to message not found with id: 99");
    }

    @Test
    void softDelete_shouldSetDeletedAt() {
        when(chatMessageRepository.findById(1L)).thenReturn(Optional.of(message));

        chatMessageService.softDelete(1L);

        assertThat(message.getDeletedAt()).isNotNull();
        verify(chatMessageRepository).save(message);
    }

    @Test
    void softDelete_shouldThrowWhenNotFound() {
        when(chatMessageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatMessageService.softDelete(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Chat message not found with id: 1");
    }

    @Test
    void toResponseList_shouldMapMessages() {
        when(chatMessageRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(message));

        List<ChatMessageResponse> responses = chatMessageService.toResponseList(chatMessageService.findAll());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSessionId()).isEqualTo(1L);
        assertThat(responses.get(0).getContent()).isEqualTo("Hello");
        assertThat(responses.get(0).getSenderType()).isEqualTo(SenderType.PATIENT);
    }
}
