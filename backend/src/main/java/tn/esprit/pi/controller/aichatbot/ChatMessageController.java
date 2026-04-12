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
import tn.esprit.pi.dto.aichatbot.ChatMessageCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatMessageResponse;
import tn.esprit.pi.enums.aichatbot.MessageType;
import tn.esprit.pi.enums.aichatbot.SenderType;
import tn.esprit.pi.service.aichatbot.ChatMessageService;

@RestController
@RequestMapping("/api/ai/chat-messages")
@RequiredArgsConstructor
@Tag(name = "AI Chat Messages", description = "APIs for managing messages within AI chat sessions")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "Get Messages by Session", description = "Retrieves all messages for a specific chat session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<ChatMessageResponse>> findBySessionId(
            @Parameter(description = "The unique identifier of the chat session", required = true)
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(chatMessageService.toResponseList(chatMessageService.findBySessionId(sessionId)));
    }

    @Operation(summary = "Get Messages by Session and Sender", description = "Retrieves messages filtered by session and sender type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/session/{sessionId}/sender/{senderType}")
    public ResponseEntity<List<ChatMessageResponse>> findBySessionIdAndSenderType(
            @Parameter(description = "The unique identifier of the chat session", required = true)
            @PathVariable Long sessionId,
            @Parameter(description = "The sender type to filter by", required = true,
                    schema = @Schema(allowableValues = {"PATIENT", "AI"}))
            @PathVariable SenderType senderType) {
        return ResponseEntity.ok(chatMessageService.toResponseList(chatMessageService.findBySessionIdAndSenderType(sessionId, senderType)));
    }

    @Operation(summary = "Get Messages by Session and Type", description = "Retrieves messages filtered by session and message type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/session/{sessionId}/type/{messageType}")
    public ResponseEntity<List<ChatMessageResponse>> findBySessionIdAndMessageType(
            @Parameter(description = "The unique identifier of the chat session", required = true)
            @PathVariable Long sessionId,
            @Parameter(description = "The message type to filter by", required = true,
                    schema = @Schema(allowableValues = {"TEXT", "IMAGE", "SYSTEM"}))
            @PathVariable MessageType messageType) {
        return ResponseEntity.ok(chatMessageService.toResponseList(chatMessageService.findBySessionIdAndMessageType(sessionId, messageType)));
    }

    @Operation(summary = "Send a Message", description = "Sends a new message in an existing chat session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message created successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid message data or session not active.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<ChatMessageResponse> create(@Valid @RequestBody ChatMessageCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatMessageService.create(request));
    }

    @Operation(summary = "Soft Delete Message", description = "Soft deletes a message by setting its deletedAt timestamp.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message soft deleted successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Message not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(
            @Parameter(description = "The unique identifier of the message to delete", required = true)
            @PathVariable Long id) {
        chatMessageService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
