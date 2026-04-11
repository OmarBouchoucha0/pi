package tn.esprit.pi.controller.intake;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.intake.IntakeLogRequest;
import tn.esprit.pi.dto.intake.IntakeLogResponse;
import tn.esprit.pi.enums.intake.IntakeStatus;
import tn.esprit.pi.service.intake.IntakeLogService;

@RestController
@RequestMapping("/intake-logs")
@RequiredArgsConstructor
@Tag(name = "Intake Logs", description = "APIs for tracking medication intake logs")
public class IntakeLogController {

    private final IntakeLogService intakeLogService;

    // POST /api/intake-logs
    @PostMapping
    @Operation(summary = "Create intake log", description = "Creates a new medication intake log entry")
    @ApiResponse(responseCode = "201", description = "Intake log created successfully")
    public ResponseEntity<IntakeLogResponse> create(
            @RequestBody IntakeLogRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(intakeLogService.create(request));
    }

    // GET /api/intake-logs
    @GetMapping
    @Operation(summary = "Get all intake logs", description = "Retrieves all medication intake logs")
    @ApiResponse(responseCode = "200", description = "Intake logs retrieved successfully")
    public ResponseEntity<List<IntakeLogResponse>> getAll() {
        return ResponseEntity.ok(intakeLogService.getAll());
    }

    // GET /api/intake-logs/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get intake log by ID", description = "Retrieves a specific intake log by its ID")
    @ApiResponse(responseCode = "200", description = "Intake log found")
    @ApiResponse(responseCode = "404", description = "Intake log not found")
    public ResponseEntity<IntakeLogResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(intakeLogService.getById(id));
    }

    // GET /api/intake-logs/prescription/{prescriptionId}
    @GetMapping("/prescription/{prescriptionId}")
    @Operation(summary = "Get intake logs by prescription", description = "Retrieves all intake logs for a specific prescription")
    @ApiResponse(responseCode = "200", description = "Intake logs retrieved successfully")
    public ResponseEntity<List<IntakeLogResponse>> getByPrescription(
            @PathVariable Long prescriptionId) {
        return ResponseEntity.ok(intakeLogService.getByPrescription(prescriptionId));
    }

    // GET /api/intake-logs/status/{status}
    @GetMapping("/status/{status}")
    @Operation(summary = "Get intake logs by status", description = "Retrieves all intake logs with a specific status")
    @ApiResponse(responseCode = "200", description = "Intake logs retrieved successfully")
    public ResponseEntity<List<IntakeLogResponse>> getByStatus(
            @PathVariable IntakeStatus status) {
        return ResponseEntity.ok(intakeLogService.getByStatus(status));
    }

    // GET /api/intake-logs/prescription/{prescriptionId}/adherence
    @GetMapping("/prescription/{prescriptionId}/adherence")
    @Operation(summary = "Get adherence stats", description = "Retrieves adherence statistics for a prescription")
    @ApiResponse(responseCode = "200", description = "Adherence stats retrieved successfully")
    public ResponseEntity<Map<String, Object>> getAdherence(
            @PathVariable Long prescriptionId) {
        return ResponseEntity.ok(intakeLogService.getAdherenceStats(prescriptionId));
    }

    // DELETE /api/intake-logs/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete intake log", description = "Deletes an intake log entry")
    @ApiResponse(responseCode = "204", description = "Intake log deleted successfully")
    @ApiResponse(responseCode = "404", description = "Intake log not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        intakeLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // PUT /intake-logs/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update intake log", description = "Updates an existing intake log entry")
    @ApiResponse(responseCode = "200", description = "Intake log updated successfully")
    @ApiResponse(responseCode = "404", description = "Intake log not found")
    public ResponseEntity<IntakeLogResponse> update(
            @PathVariable Long id,
            @RequestBody IntakeLogRequest request) {
        return ResponseEntity.ok(intakeLogService.update(id, request));
    }
}
