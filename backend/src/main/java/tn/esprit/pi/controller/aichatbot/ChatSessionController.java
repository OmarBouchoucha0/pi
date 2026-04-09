package tn.esprit.pi.controller.aichatbot;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.aichatbot.ChatSessionCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatSessionResponse;
import tn.esprit.pi.dto.aichatbot.ChatSessionUpdateRequest;
import tn.esprit.pi.enums.aichatbot.ChatSessionStatus;
import tn.esprit.pi.service.aichatbot.ChatSessionService;

@RestController
@RequestMapping("/api/ai/chat-sessions")
@RequiredArgsConstructor
@Tag(name = "AI Chat Sessions", description = """
        APIs for managing AI-powered chat sessions in the MeddiFollow system.

        This controller handles the lifecycle of AI chatbot conversations between
        patients and the AI assistant. Each session maintains the conversation
        history and context for personalized healthcare assistance.

        ## Chat Session Features
        - **Patient Interaction**: Patients can start chat sessions for health inquiries
        - **AI Responses**: The system uses Groq AI (Llama 3.3 70B) to generate responses
        - **Session Management**: Sessions can be active, ended, or deleted
        - **Context Preservation**: Chat context is maintained for follow-up questions

        ## Session Lifecycle
        1. Patient creates a new chat session
        2. Messages are exchanged between patient and AI
        3. Context is built and maintained throughout the conversation
        4. Session can be ended by patient or automatically
        5. Session history is preserved for future reference

        ## ChatSessionStatus
        - **ACTIVE**: The session is ongoing and accepting messages
        - **ENDED**: The session has been completed by the patient
        - **DELETED**: The session has been soft-deleted

        ## Integration
        - Uses Groq API for AI responses (configured via groq.api.key)
        - Maintains conversation context for personalized interactions
        - Supports multi-tenant environments with tenant isolation
        """)
@SecurityRequirement(name = "Bearer Authentication")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @Operation(
            summary = "Get All Chat Sessions",
            description = """
                    Retrieves all chat sessions in the system.

                    This endpoint returns all chat sessions regardless of their status.
                    Typically used for administrative purposes.

                    ## Response Details
                    - Returns list of all chat sessions
                    - Each session includes patient info, status, and message count
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat sessions retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<ChatSessionResponse>> findAll() {
        return ResponseEntity.ok(chatSessionService.toResponseList(chatSessionService.findAll()));
    }

    @Operation(
            summary = "Get Chat Session by ID",
            description = """
                    Retrieves a specific chat session by its unique identifier.

                    This endpoint returns detailed information about a chat session,
                    including the patient's information and session metadata.

                    ## Path Parameters
                    - id: The unique identifier of the chat session

                    ## Response Details
                    - Returns session details including name, status, and message count
                    - Returns 404 if session not found
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat session retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChatSessionResponse> findById(
            @Parameter(description = "The unique identifier of the chat session", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(chatSessionService.toResponse(id));
    }

    @Operation(
            summary = "Get Chat Sessions by Patient",
            description = """
                    Retrieves all chat sessions for a specific patient.

                    This endpoint returns all chat sessions associated with a patient,
                    regardless of their status.

                    ## Path Parameters
                    - patientId: The unique identifier of the patient

                    ## Response Details
                    - Returns list of patient's chat sessions
                    - Ordered by most recent activity
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat sessions retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Patient not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ChatSessionResponse>> findByPatientId(
            @Parameter(description = "The unique identifier of the patient", required = true)
            @PathVariable Long patientId) {
        return ResponseEntity.ok(chatSessionService.toResponseList(chatSessionService.findByPatientId(patientId)));
    }

    @Operation(
            summary = "Get Chat Sessions by Tenant",
            description = """
                    Retrieves all chat sessions for a specific tenant/organization.

                    This endpoint returns all chat sessions within a tenant,
                    useful for administrators to monitor conversations.

                    ## Path Parameters
                    - tenantId: The unique identifier of the tenant

                    ## Response Details
                    - Returns list of tenant's chat sessions
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat sessions retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ChatSessionResponse>> findByTenantId(
            @Parameter(description = "The unique identifier of the tenant", required = true)
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(chatSessionService.toResponseList(chatSessionService.findByTenantId(tenantId)));
    }

    @Operation(
            summary = "Get Chat Sessions by Status",
            description = """
                    Retrieves all chat sessions with a specific status.

                    This endpoint filters chat sessions by their current status,
                    useful for finding active conversations or ended sessions.

                    ## Path Parameters
                    - status: The status to filter by (ACTIVE, ENDED, DELETED)

                    ## Response Details
                    - Returns list of sessions with the specified status
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat sessions retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChatSessionResponse>> findByStatus(
            @Parameter(description = "The status to filter sessions by", required = true,
                    schema = @Schema(allowableValues = {"ACTIVE", "ENDED", "DELETED"}))
            @PathVariable ChatSessionStatus status) {
        return ResponseEntity.ok(chatSessionService.toResponseList(chatSessionService.findByStatus(status)));
    }

    @Operation(
            summary = "Create Chat Session",
            description = """
                    Creates a new AI chat session for a patient.

                    This endpoint initializes a new conversation with the AI assistant.
                    The session is created with ACTIVE status and ready to accept messages.

                    ## Request Details
                    - tenantId: The organization this session belongs to
                    - patientId: The patient starting the conversation
                    - name: Optional name for the session (e.g., "Heart Health Consultation")

                    ## Session Initialization
                    - New session starts with zero messages
                    - Session timestamp is recorded
                    - Patient can immediately start sending messages

                    ## Response Details
                    - Returns the created session with generated ID
                    - Session is ready for message exchange
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chat session created successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Patient or tenant not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<ChatSessionResponse> create(@Valid @RequestBody ChatSessionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatSessionService.create(request));
    }

    @Operation(
            summary = "Update Chat Session",
            description = """
                    Updates an existing chat session's information.

                    This endpoint allows updating the session name or status.
                    Only provided fields will be updated.

                    ## Path Parameters
                    - id: The unique identifier of the chat session

                    ## Request Details
                    - name: New session name (optional)
                    - status: New session status (optional)

                    ## Response Details
                    - Returns the updated session
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat session updated successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ChatSessionResponse> update(
            @Parameter(description = "The unique identifier of the chat session", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ChatSessionUpdateRequest request) {
        return ResponseEntity.ok(chatSessionService.update(id, request));
    }

    @Operation(
            summary = "End Chat Session",
            description = """
                    Ends an active chat session.

                    This endpoint marks an active session as ENDED, indicating that
                    the patient has completed their conversation. The session history
                    is preserved for future reference.

                    ## Path Parameters
                    - id: The unique identifier of the chat session

                    ## Effects
                    - Session status changes from ACTIVE to ENDED
                    - End timestamp is recorded
                    - Session can no longer accept new messages
                    - Session history remains accessible

                    ## Response Details
                    - Returns 200 OK on success
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat session ended successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}/end")
    public ResponseEntity<Void> endSession(
            @Parameter(description = "The unique identifier of the chat session to end", required = true)
            @PathVariable Long id) {
        chatSessionService.endSession(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Soft Delete Chat Session",
            description = """
                    Soft deletes a chat session by setting their deletedAt timestamp.

                    This endpoint performs a soft delete, removing the session from
                    active views while preserving the conversation history in the database.

                    ## Path Parameters
                    - id: The unique identifier of the chat session to delete

                    ## Effects
                    - Session is marked as deleted
                    - Session is hidden from default queries
                    - All associated messages are also soft-deleted
                    - Conversation history is preserved for audit purposes

                    ## Response Details
                    - Returns 204 No Content on success
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chat session soft deleted successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(
            @Parameter(description = "The unique identifier of the chat session to delete", required = true)
            @PathVariable Long id) {
        chatSessionService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
