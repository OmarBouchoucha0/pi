// PatientProtocolController.java
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
import tn.esprit.pi.dto.followup.ExecutionResponse;
import tn.esprit.pi.dto.followup.ExecutionUpdateRequest;
import tn.esprit.pi.dto.followup.PatientProtocolRequest;
import tn.esprit.pi.dto.followup.PatientProtocolResponse;
import tn.esprit.pi.service.followup.FollowupProtocol.PatientProtocolService;

@RestController
@RequestMapping("/api/patient-protocols")
@RequiredArgsConstructor
@Tag(name = "Patient Protocols", description = "APIs for assigning and managing patient-specific follow-up protocols")
public class PatientProtocolController {

    private final PatientProtocolService service;

    @PostMapping
    @Operation(summary = "Assign protocol to patient", description = "Assigns a follow-up protocol to a specific patient")
    @ApiResponse(responseCode = "201", description = "Protocol assigned successfully")
    public ResponseEntity<PatientProtocolResponse> assign(
            @Valid @RequestBody PatientProtocolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.assignProtocol(request));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient protocols", description = "Retrieves all protocols assigned to a specific patient")
    @ApiResponse(responseCode = "200", description = "Patient protocols retrieved successfully")
    public ResponseEntity<List<PatientProtocolResponse>> getByPatient(
            @PathVariable String patientId) {
        return ResponseEntity.ok(service.getByPatient(patientId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient protocol by ID", description = "Retrieves a specific patient protocol assignment by ID")
    @ApiResponse(responseCode = "200", description = "Patient protocol found")
    @ApiResponse(responseCode = "404", description = "Patient protocol not found")
    public ResponseEntity<PatientProtocolResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/risks")
    @Operation(summary = "Get risk patients", description = "Retrieves all patients with high-risk protocol compliance")
    @ApiResponse(responseCode = "200", description = "Risk patients retrieved successfully")
    public ResponseEntity<List<PatientProtocolResponse>> getRisks() {
        return ResponseEntity.ok(service.getRiskPatients());
    }

    @PatchMapping("/executions/{executionId}")
    @Operation(summary = "Update protocol execution", description = "Updates the execution status of a protocol step (completed, skipped, missed)")
    @ApiResponse(responseCode = "200", description = "Execution updated successfully")
    @ApiResponse(responseCode = "404", description = "Execution not found")
    public ResponseEntity<ExecutionResponse> updateExecution(
            @PathVariable String executionId,
            @Valid @RequestBody ExecutionUpdateRequest request) {
        return ResponseEntity.ok(service.updateExecution(executionId, request));
    }

    @PostMapping("/{id}/recalculate")
    @Operation(summary = "Recalculate compliance", description = "Recalculates the compliance score for a patient protocol")
    @ApiResponse(responseCode = "200", description = "Compliance recalculated successfully")
    public ResponseEntity<Void> recalculate(@PathVariable String id) {
        service.recalculateCompliance(id);
        return ResponseEntity.ok().build();
    }
}
