// FollowupProtocolController.java
package tn.esprit.pi.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.request.ProtocolRequest;
import tn.esprit.pi.dto.request.ProtocolStepRequest;
import tn.esprit.pi.dto.response.ProtocolResponse;
import tn.esprit.pi.dto.response.ProtocolStepResponse;
import tn.esprit.pi.service.FollowupProtocol.FollowupProtocolService;

@RestController
@RequestMapping("/api/protocols")
@RequiredArgsConstructor
public class FollowupProtocolController {

    private final FollowupProtocolService service;

    @PostMapping
    public ResponseEntity<ProtocolResponse> create(
            @Valid @RequestBody ProtocolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createProtocol(request));
    }

    @GetMapping
    public ResponseEntity<List<ProtocolResponse>> getAll() {
        return ResponseEntity.ok(service.getAllProtocols());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProtocolResponse>> getActive() {
        return ResponseEntity.ok(service.getActiveProtocols());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtocolResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getProtocolById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProtocolResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ProtocolRequest request) {
        return ResponseEntity.ok(service.updateProtocol(id, request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ProtocolResponse> toggle(@PathVariable String id) {
        return ResponseEntity.ok(service.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteProtocol(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{protocolId}/steps")
    public ResponseEntity<ProtocolStepResponse> addStep(
            @PathVariable String protocolId,
            @Valid @RequestBody ProtocolStepRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addStep(protocolId, request));
    }

    @GetMapping("/{protocolId}/steps")
    public ResponseEntity<List<ProtocolStepResponse>> getSteps(
            @PathVariable String protocolId) {
        return ResponseEntity.ok(service.getSteps(protocolId));
    }

    @DeleteMapping("/steps/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable String stepId) {
        service.deleteStep(stepId);
        return ResponseEntity.noContent().build();
    }
}
