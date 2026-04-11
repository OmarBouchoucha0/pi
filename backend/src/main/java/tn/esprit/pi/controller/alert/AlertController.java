package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.AlertRequest;
import tn.esprit.pi.dto.AlertResponse;
import tn.esprit.pi.enums.AlertSeverity;
import tn.esprit.pi.enums.AlertStatus;
import tn.esprit.pi.service.AlertService;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "APIs for managing alerts and notifications")
public class AlertController {

    private final AlertService alertService;

    // POST /api/alerts
    @PostMapping
    @Operation(summary = "Create alert", description = "Creates a new alert")
    @ApiResponse(responseCode = "201", description = "Alert created successfully")
    public ResponseEntity<AlertResponse> createAlert(@RequestBody AlertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlert(request));
    }

    // GET /api/alerts
    @GetMapping
    @Operation(summary = "Get all alerts", description = "Retrieves all alerts")
    @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully")
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    // GET /api/alerts/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get alert by ID", description = "Retrieves a specific alert by its ID")
    @ApiResponse(responseCode = "200", description = "Alert found")
    @ApiResponse(responseCode = "404", description = "Alert not found")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    // GET /api/alerts/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get alerts by patient", description = "Retrieves all alerts for a specific patient")
    @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully")
    public ResponseEntity<List<AlertResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(alertService.getAlertsByPatient(patientId));
    }

    // GET /api/alerts/tenant/{tenantId}
    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get alerts by tenant", description = "Retrieves all alerts for a specific tenant")
    @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully")
    public ResponseEntity<List<AlertResponse>> getByTenant(@PathVariable Long tenantId) {
        return ResponseEntity.ok(alertService.getAlertsByTenant(tenantId));
    }

    // GET /api/alerts/status/{status}
    @GetMapping("/status/{status}")
    @Operation(summary = "Get alerts by status", description = "Retrieves all alerts with a specific status")
    @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully")
    public ResponseEntity<List<AlertResponse>> getByStatus(@PathVariable AlertStatus status) {
        return ResponseEntity.ok(alertService.getAlertsByStatus(status));
    }

    // GET /api/alerts/severity/{severity}
    @GetMapping("/severity/{severity}")
    @Operation(summary = "Get alerts by severity", description = "Retrieves all alerts with a specific severity")
    @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully")
    public ResponseEntity<List<AlertResponse>> getBySeverity(@PathVariable AlertSeverity severity) {
        return ResponseEntity.ok(alertService.getAlertsBySeverity(severity));
    }

    // PATCH /api/alerts/{id}/status
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update alert status", description = "Updates the status of an alert")
    @ApiResponse(responseCode = "200", description = "Alert status updated successfully")
    public ResponseEntity<AlertResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam AlertStatus status) {
        return ResponseEntity.ok(alertService.updateAlertStatus(id, status));
    }

    // PATCH /api/alerts/{id}/escalate
    @PatchMapping("/{id}/escalate")
    @Operation(summary = "Escalate alert", description = "Escalates an alert to higher severity")
    @ApiResponse(responseCode = "200", description = "Alert escalated successfully")
    public ResponseEntity<AlertResponse> escalate(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.escalateAlert(id));
    }

    // PATCH /api/alerts/{id}/acknowledge
    @PatchMapping("/{id}/acknowledge")
    @Operation(summary = "Acknowledge alert", description = "Acknowledges an alert")
    @ApiResponse(responseCode = "200", description = "Alert acknowledged successfully")
    public ResponseEntity<AlertResponse> acknowledge(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.acknowledgeAlert(id));
    }

    // PATCH /api/alerts/{id}/close
    @PatchMapping("/{id}/close")
    @Operation(summary = "Close alert", description = "Closes an alert")
    @ApiResponse(responseCode = "200", description = "Alert closed successfully")
    public ResponseEntity<AlertResponse> close(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.closeAlert(id));
    }

    // DELETE /api/alerts/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete alert", description = "Deletes an alert")
    @ApiResponse(responseCode = "204", description = "Alert deleted successfully")
    @ApiResponse(responseCode = "404", description = "Alert not found")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update alert", description = "Updates an existing alert")
    @ApiResponse(responseCode = "200", description = "Alert updated successfully")
    @ApiResponse(responseCode = "404", description = "Alert not found")
    public ResponseEntity<AlertResponse> updateAlert(
            @PathVariable Long id,
            @RequestBody AlertRequest request) {
        return ResponseEntity.ok(alertService.updateAlert(id, request));}
}
