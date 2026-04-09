package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.AlertRequest;
import tn.esprit.pi.dto.AlertResponse;
import tn.esprit.pi.enums.AlertSeverity;
import tn.esprit.pi.enums.AlertStatus;
import tn.esprit.pi.service.AlertService;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // POST /api/alerts
    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@RequestBody AlertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlert(request));
    }

    // GET /api/alerts
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    // GET /api/alerts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    // GET /api/alerts/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AlertResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(alertService.getAlertsByPatient(patientId));
    }

    // GET /api/alerts/tenant/{tenantId}
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<AlertResponse>> getByTenant(@PathVariable Long tenantId) {
        return ResponseEntity.ok(alertService.getAlertsByTenant(tenantId));
    }

    // GET /api/alerts/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AlertResponse>> getByStatus(@PathVariable AlertStatus status) {
        return ResponseEntity.ok(alertService.getAlertsByStatus(status));
    }

    // GET /api/alerts/severity/{severity}
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<AlertResponse>> getBySeverity(@PathVariable AlertSeverity severity) {
        return ResponseEntity.ok(alertService.getAlertsBySeverity(severity));
    }

    // PATCH /api/alerts/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<AlertResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam AlertStatus status) {
        return ResponseEntity.ok(alertService.updateAlertStatus(id, status));
    }

    // PATCH /api/alerts/{id}/escalate
    @PatchMapping("/{id}/escalate")
    public ResponseEntity<AlertResponse> escalate(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.escalateAlert(id));
    }

    // PATCH /api/alerts/{id}/acknowledge
    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<AlertResponse> acknowledge(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.acknowledgeAlert(id));
    }

    // PATCH /api/alerts/{id}/close
    @PatchMapping("/{id}/close")
    public ResponseEntity<AlertResponse> close(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.closeAlert(id));
    }

    // DELETE /api/alerts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<AlertResponse> updateAlert(
            @PathVariable Long id,
            @RequestBody AlertRequest request) {
        return ResponseEntity.ok(alertService.updateAlert(id, request));}
}
