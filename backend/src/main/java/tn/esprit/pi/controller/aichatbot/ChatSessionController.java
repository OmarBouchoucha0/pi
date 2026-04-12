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
@Tag(name = "AI Chat Sessions", description = "APIs for managing AI-powered chat sessions")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @Operation(summary = "Get All Chat Sessions", description = "Retrieves all chat sessions in the system.")
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

    @Operation(summary = "Get Chat Session by ID", description = "Retrieves a specific chat session by its unique identifier.")
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

    @Operation(summary = "Get Chat Sessions by Patient", description = "Retrieves all chat sessions for a specific patient.")
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

    @Operation(summary = "Get Chat Sessions by Tenant", description = "Retrieves all chat sessions for a specific tenant/organization.")
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

    @Operation(summary = "Get Chat Sessions by Status", description = "Retrieves all chat sessions with a specific status.")
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

    @Operation(summary = "Create Chat Session", description = "Creates a new AI chat session for a patient.")
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

    @Operation(summary = "Update Chat Session", description = "Updates an existing chat session's information.")
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

    @Operation(summary = "End Chat Session", description = "Ends an active chat session.")
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

    @Operation(summary = "Soft Delete Chat Session", description = "Soft deletes a chat session by setting their deletedAt timestamp.")
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
