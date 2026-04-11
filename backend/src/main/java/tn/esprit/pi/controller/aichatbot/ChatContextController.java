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
@Tag(name = "AI Chat Context", description = """
        APIs for managing conversation context in AI chat sessions.

        This controller handles the contextual information that the AI uses to
        generate personalized and relevant responses. Context data is stored
        as key-value pairs that persist throughout a chat session.

        ## Context Features
        - **Key-Value Storage**: Each context item has a key and value
        - **Session-Scoped**: Context is tied to specific chat sessions
        - **Dynamic Updates**: Context can be updated during conversation
        - **AI Reference**: AI uses context for personalized responses

        ## Common Context Keys
        - **patient_id**: Patient identifier for personalization
        - **current_symptoms**: Active symptoms being discussed
        - **medications**: Current medications if mentioned
        - **last_topic**: The most recent discussion topic
        - **medical_history**: Relevant medical history context

        ## Context Usage Flow
        1. When patient shares information (e.g., diabetes), it's stored as context
        2. AI includes this context in future response generation
        3. Context grows throughout the conversation
        4. Old/unnecessary context can be cleaned up
        5. Context is preserved for session history

        ## Example Usage
        Patient: "I have Type 2 Diabetes"
        System stores: context[symptoms] += "Type 2 Diabetes"

        Patient: "What should I eat?"
        AI checks context, sees diabetes, provides diabetes-friendly dietary advice

        ## Note
        Context is automatically managed by the system during message processing.
        This API provides manual control for advanced use cases.
        """)
@SecurityRequirement(name = "Bearer Authentication")
public class ChatContextController {

    private final ChatContextService chatContextService;

    @Operation(
            summary = "Get All Context for Session",
            description = """
                    Retrieves all context key-value pairs for a specific chat session.

                    This endpoint returns the complete context data stored for a session,
                    which the AI uses to generate personalized responses.

                    ## Path Parameters
                    - sessionId: The unique identifier of the chat session

                    ## Response Details
                    - Returns list of context items (key-value pairs)
                    - Each item includes the key, value, and creation timestamp
                    - Empty list if no context exists
                    """)
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

    @Operation(
            summary = "Check Context Key Exists",
            description = """
                    Checks if a specific context key exists for a session.

                    This endpoint is useful for verifying whether certain information
                    has already been stored in the context.

                    ## Path Parameters
                    - sessionId: The unique identifier of the chat session
                    - key: The context key to check for

                    ## Response Details
                    - Returns true if the key exists in context
                    - Returns false if key is not found or session doesn't exist
                    """)
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
