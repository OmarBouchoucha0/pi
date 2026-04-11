// FollowupProtocolController.java
package tn.esprit.pi.controller.followup;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.followup.ProtocolRequest;
import tn.esprit.pi.dto.followup.ProtocolResponse;
import tn.esprit.pi.dto.followup.ProtocolStepRequest;
import tn.esprit.pi.dto.followup.ProtocolStepResponse;
import tn.esprit.pi.service.followup.FollowupProtocol.FollowupProtocolService;

@RestController
@RequestMapping("/api/protocols")
@RequiredArgsConstructor
@Tag(name = "Follow-up Protocols", description = "APIs for creating and managing follow-up protocols")
public class FollowupProtocolController {

    private final FollowupProtocolService service;

    @PostMapping
    @Operation(summary = "Create protocol", description = "Creates a new follow-up protocol with steps")
    @ApiResponse(responseCode = "201", description = "Protocol created successfully")
    public ResponseEntity<ProtocolResponse> create(
            @Valid @RequestBody ProtocolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createProtocol(request));
    }

    @GetMapping
    @Operation(summary = "Get all protocols", description = "Retrieves all follow-up protocols")
    @ApiResponse(responseCode = "200", description = "Protocols retrieved successfully")
    public ResponseEntity<List<ProtocolResponse>> getAll() {
        return ResponseEntity.ok(service.getAllProtocols());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active protocols", description = "Retrieves all active follow-up protocols")
    @ApiResponse(responseCode = "200", description = "Active protocols retrieved successfully")
    public ResponseEntity<List<ProtocolResponse>> getActive() {
        return ResponseEntity.ok(service.getActiveProtocols());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get protocol by ID", description = "Retrieves a specific protocol by its ID")
    @ApiResponse(responseCode = "200", description = "Protocol found")
    @ApiResponse(responseCode = "404", description = "Protocol not found")
    public ResponseEntity<ProtocolResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getProtocolById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update protocol", description = "Updates an existing follow-up protocol")
    @ApiResponse(responseCode = "200", description = "Protocol updated successfully")
    @ApiResponse(responseCode = "404", description = "Protocol not found")
    public ResponseEntity<ProtocolResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ProtocolRequest request) {
        return ResponseEntity.ok(service.updateProtocol(id, request));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle protocol status", description = "Toggles the active/inactive status of a protocol")
    @ApiResponse(responseCode = "200", description = "Protocol status toggled successfully")
    public ResponseEntity<ProtocolResponse> toggle(@PathVariable String id) {
        return ResponseEntity.ok(service.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete protocol", description = "Deletes a follow-up protocol by its ID")
    @ApiResponse(responseCode = "204", description = "Protocol deleted successfully")
    @ApiResponse(responseCode = "404", description = "Protocol not found")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteProtocol(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{protocolId}/steps")
    @Operation(summary = "Add protocol step", description = "Adds a new step to an existing protocol")
    @ApiResponse(responseCode = "201", description = "Step added successfully")
    @ApiResponse(responseCode = "404", description = "Protocol not found")
    public ResponseEntity<ProtocolStepResponse> addStep(
            @PathVariable String protocolId,
            @Valid @RequestBody ProtocolStepRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addStep(protocolId, request));
    }

    @GetMapping("/{protocolId}/steps")
    @Operation(summary = "Get protocol steps", description = "Retrieves all steps for a specific protocol")
    @ApiResponse(responseCode = "200", description = "Steps retrieved successfully")
    public ResponseEntity<List<ProtocolStepResponse>> getSteps(
            @PathVariable String protocolId) {
        return ResponseEntity.ok(service.getSteps(protocolId));
    }

    @DeleteMapping("/steps/{stepId}")
    @Operation(summary = "Delete protocol step", description = "Deletes a step from a protocol")
    @ApiResponse(responseCode = "204", description = "Step deleted successfully")
    public ResponseEntity<Void> deleteStep(@PathVariable String stepId) {
        service.deleteStep(stepId);
        return ResponseEntity.noContent().build();
    }
}
