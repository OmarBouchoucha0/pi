package tn.esprit.pi.controller.aichatbot;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.aichatbot.ChatContextCreateRequest;
import tn.esprit.pi.dto.aichatbot.ChatContextResponse;
import tn.esprit.pi.service.aichatbot.ChatContextService;

@RestController
@RequestMapping("/api/ai/chat-contexts")
@RequiredArgsConstructor
@Tag(name = "AI Chat Context", description = "APIs for managing conversation context in AI chat sessions")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatContextController {

    private final ChatContextService chatContextService;

    @Operation(summary = "Get All Context for Session", description = "Retrieves all context key-value pairs for a specific chat session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Context retrieved successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<ChatContextResponse>> findBySessionId(
            @Parameter(description = "The unique identifier of the chat session", required = true)
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(chatContextService.toResponseList(chatContextService.findBySessionId(sessionId)));
    }

    @Operation(summary = "Check Context Key Exists", description = "Checks if a specific context key exists for a session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Context check completed.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/session/{sessionId}/exists/{key}")
    public ResponseEntity<Boolean> existsBySessionIdAndKey(
            @Parameter(description = "The unique identifier of the chat session", required = true)
            @PathVariable Long sessionId,
            @Parameter(description = "The context key to check for", required = true)
            @PathVariable String key) {
        return ResponseEntity.ok(chatContextService.existsBySessionIdAndKey(sessionId, key));
    }

    @Operation(
            summary = "Create/Update Context",
            description = """
                    Creates or updates a context key-value pair for a session.

                    This endpoint stores context information that the AI uses for
                    generating personalized responses. If the key already exists,
                    its value will be updated.

                    ## Request Details
                    - sessionId: The chat session this context belongs to
                    - key: The context key (e.g., "symptoms", "medications")
                    - value: The context value (e.g., "Type 2 Diabetes", "Metformin")

                    ## Common Keys
                    - **symptoms**: Current symptoms being discussed
                    - **medications**: Current medications
                    - **allergies**: Known allergies
                    - **last_topic**: Most recent discussion topic
                    - **medical_history**: Relevant medical history

                    ## Response Details
                    - Returns the created/updated context item
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Context created successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "200", description = "Context updated successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Chat session not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<ChatContextResponse> create(@Valid @RequestBody ChatContextCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatContextService.create(request));
    }

    @Operation(
            summary = "Delete Context by Key",
            description = """
                    Deletes a specific context key-value pair from a session.

                    This endpoint removes context data that is no longer needed.
                    Use this to clean up old or irrelevant context.

                    ## Path Parameters
                    - sessionId: The unique identifier of the chat session
                    - key: The context key to delete

                    ## Use Cases
                    - Clean up outdated information
                    - Remove incorrect context data
                    - Reset specific context areas

                    ## Response Details
                    - Returns 204 No Content on success
                    - Returns 404 if the key doesn't exist
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Context deleted successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid or missing authentication token.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Context key not found.",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/session/{sessionId}/{key}")
    public ResponseEntity<Void> deleteBySessionIdAndKey(
            @Parameter(description = "The unique identifier of the chat session", required = true)
            @PathVariable Long sessionId,
            @Parameter(description = "The context key to delete", required = true)
            @PathVariable String key) {
        chatContextService.deleteBySessionIdAndKey(sessionId, key);
        return ResponseEntity.noContent().build();
    }
}
