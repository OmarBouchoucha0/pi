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

import tn.esprit.pi.dto.aichatbot.ChatSessionCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatSessionResponse;
import tn.esprit.pi.dto.aichatbot.ChatSessionUpdateRequest;
import tn.esprit.pi.entity.aichatbot.ChatSession;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.aichatbot.ChatSessionStatus;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.aichatbot.ChatSessionRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatSessionService chatSessionService;

    private Tenant tenant;
    private User patient;
    private ChatSession session;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder().id(1L).name("Test Tenant").build();
        patient = User.builder().id(1L).firstName("John").lastName("Doe").build();
        session = ChatSession.builder()
                .id(1L)
                .name("Test Session")
                .status(ChatSessionStatus.ACTIVE)
                .tenant(tenant)
                .patient(patient)
                .totalMessages(0L)
                .startedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_shouldReturnNonDeletedSessions() {
        when(chatSessionRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(session));

        List<ChatSession> result = chatSessionService.findAll();

        assertThat(result).hasSize(1);
        verify(chatSessionRepository).findAllByDeletedAtIsNull();
    }

    @Test
    void findById_shouldReturnSessionWhenExists() {
        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Optional<ChatSession> result = chatSessionService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Session");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ChatSession> result = chatSessionService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPatientId_shouldReturnSessionsForPatient() {
        when(chatSessionRepository.findByPatientIdAndDeletedAtIsNull(1L)).thenReturn(List.of(session));

        List<ChatSession> result = chatSessionService.findByPatientId(1L);

        assertThat(result).hasSize(1);
        verify(chatSessionRepository).findByPatientIdAndDeletedAtIsNull(1L);
    }

    @Test
    void findByPatientIdAndStatus_shouldReturnFilteredSessions() {
        when(chatSessionRepository.findByPatientIdAndStatusAndDeletedAtIsNull(1L, ChatSessionStatus.ACTIVE))
                .thenReturn(List.of(session));

        List<ChatSession> result = chatSessionService.findByPatientIdAndStatus(1L, ChatSessionStatus.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ChatSessionStatus.ACTIVE);
    }

    @Test
    void findByTenantId_shouldReturnSessionsForTenant() {
        when(chatSessionRepository.findByTenantIdAndDeletedAtIsNull(1L)).thenReturn(List.of(session));

        List<ChatSession> result = chatSessionService.findByTenantId(1L);

        assertThat(result).hasSize(1);
        verify(chatSessionRepository).findByTenantIdAndDeletedAtIsNull(1L);
    }

    @Test
    void findByIdAndPatientId_shouldReturnSessionWhenFound() {
        when(chatSessionRepository.findByIdAndPatientIdAndDeletedAtIsNull(1L, 1L))
                .thenReturn(Optional.of(session));

        Optional<ChatSession> result = chatSessionService.findByIdAndPatientId(1L, 1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void findByStatus_shouldReturnSessionsWithStatus() {
        when(chatSessionRepository.findByStatusAndDeletedAtIsNull(ChatSessionStatus.ACTIVE))
                .thenReturn(List.of(session));

        List<ChatSession> result = chatSessionService.findByStatus(ChatSessionStatus.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ChatSessionStatus.ACTIVE);
    }

    @Test
    void create_shouldReturnResponse() {
        ChatSessionCreateRequest request = new ChatSessionCreateRequest();
        request.setName("New Session");
        request.setTenantId(1L);
        request.setPatientId(1L);

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));
        when(chatSessionRepository.save(any(ChatSession.class))).thenAnswer(invocation -> {
            ChatSession s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        ChatSessionResponse response = chatSessionService.create(request);

        assertThat(response.getName()).isEqualTo("New Session");
        assertThat(response.getPatientId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(ChatSessionStatus.ACTIVE);
        verify(chatSessionRepository).save(any(ChatSession.class));
    }

    @Test
    void create_shouldThrowWhenTenantNotFound() {
        ChatSessionCreateRequest request = new ChatSessionCreateRequest();
        request.setName("New Session");
        request.setTenantId(99L);
        request.setPatientId(1L);

        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatSessionService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tenant not found with id: 99");
    }

    @Test
    void create_shouldThrowWhenPatientNotFound() {
        ChatSessionCreateRequest request = new ChatSessionCreateRequest();
        request.setName("New Session");
        request.setTenantId(1L);
        request.setPatientId(99L);

        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatSessionService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found with id: 99");
    }

    @Test
    void update_shouldApplyPartialChanges() {
        ChatSessionUpdateRequest request = new ChatSessionUpdateRequest();
        request.setName("Updated Name");

        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);

        ChatSessionResponse response = chatSessionService.update(1L, request);

        assertThat(response.getName()).isEqualTo("Updated Name");
        verify(chatSessionRepository).save(session);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        ChatSessionUpdateRequest request = new ChatSessionUpdateRequest();
        request.setName("Updated Name");

        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatSessionService.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Chat session not found with id: 99");
    }

    @Test
    void softDelete_shouldSetDeletedAt() {
        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        chatSessionService.softDelete(1L);

        assertThat(session.getDeletedAt()).isNotNull();
        verify(chatSessionRepository).save(session);
    }

    @Test
    void softDelete_shouldThrowWhenNotFound() {
        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatSessionService.softDelete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Chat session not found with id: 99");
    }

    @Test
    void endSession_shouldSetCompletedStatus() {
        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        chatSessionService.endSession(1L);

        assertThat(session.getStatus()).isEqualTo(ChatSessionStatus.COMPLETED);
        assertThat(session.getEndedAt()).isNotNull();
        verify(chatSessionRepository).save(session);
    }

    @Test
    void endSession_shouldThrowWhenNotFound() {
        when(chatSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatSessionService.endSession(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Chat session not found with id: 99");
    }

    @Test
    void toResponse_shouldMapSessionToResponse() {
        when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        ChatSessionResponse response = chatSessionService.toResponse(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Test Session");
        assertThat(response.getPatientId()).isEqualTo(1L);
        assertThat(response.getPatientName()).isEqualTo("John Doe");
        assertThat(response.getStatus()).isEqualTo(ChatSessionStatus.ACTIVE);
    }

    @Test
    void toResponseList_shouldMapSessions() {
        when(chatSessionRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(session));

        List<ChatSessionResponse> responses = chatSessionService.toResponseList(chatSessionService.findAll());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Test Session");
        assertThat(responses.get(0).getPatientId()).isEqualTo(1L);
    }
}
