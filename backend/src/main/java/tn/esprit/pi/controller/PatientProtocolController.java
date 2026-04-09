// PatientProtocolController.java
package tn.esprit.pi.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.request.ExecutionUpdateRequest;
import tn.esprit.pi.dto.request.PatientProtocolRequest;
import tn.esprit.pi.dto.response.ExecutionResponse;
import tn.esprit.pi.dto.response.PatientProtocolResponse;
import tn.esprit.pi.service.FollowupProtocol.PatientProtocolService;
@RestController
@RequestMapping("/api/patient-protocols")
@RequiredArgsConstructor
public class PatientProtocolController {

    private final PatientProtocolService service;

    @PostMapping
    public ResponseEntity<PatientProtocolResponse> assign(
            @Valid @RequestBody PatientProtocolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.assignProtocol(request));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientProtocolResponse>> getByPatient(
            @PathVariable String patientId) {
        return ResponseEntity.ok(service.getByPatient(patientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientProtocolResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/risks")
    public ResponseEntity<List<PatientProtocolResponse>> getRisks() {
        return ResponseEntity.ok(service.getRiskPatients());
    }

    @PatchMapping("/executions/{executionId}")
    public ResponseEntity<ExecutionResponse> updateExecution(
            @PathVariable String executionId,
            @Valid @RequestBody ExecutionUpdateRequest request) {
        return ResponseEntity.ok(service.updateExecution(executionId, request));
    }

    @PostMapping("/{id}/recalculate")
    public ResponseEntity<Void> recalculate(@PathVariable String id) {
        service.recalculateCompliance(id);
        return ResponseEntity.ok().build();
    }
}
