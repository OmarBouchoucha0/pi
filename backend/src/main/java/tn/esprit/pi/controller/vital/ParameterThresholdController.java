package tn.esprit.pi.controller.vital;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.vital.ThresholdCoverageResponse;
import tn.esprit.pi.dto.vital.ThresholdRequest;
import tn.esprit.pi.entity.vital.ParameterThreshold;
import tn.esprit.pi.enums.vital.VitalType;
import tn.esprit.pi.service.vital.IParameterThresholdService;

@RestController
@RequestMapping("/thresholds")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParameterThresholdController {

    private final IParameterThresholdService service;

    // ── CRUD ──────────────────────────────────────────────────────────

    // POST /pi/thresholds
    @PostMapping
    public ResponseEntity<ParameterThreshold> save(@RequestBody @Valid ThresholdRequest req) {
        return ResponseEntity.status(201).body(service.save(req));
    }

    // GET /pi/thresholds/all
    @GetMapping("/all")
    public ResponseEntity<List<ParameterThreshold>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /pi/thresholds/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ParameterThreshold> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /pi/thresholds?tenantId=1
    @GetMapping
    public ResponseEntity<List<ParameterThreshold>> getByTenant(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByTenant(tenantId));
    }

    // PUT /pi/thresholds/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ParameterThreshold> update(
            @PathVariable Long id, @RequestBody @Valid ThresholdRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // DELETE /pi/thresholds/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── NEW EASY APIs ─────────────────────────────────────────────────

    // GET /pi/thresholds/missing?tenantId=1
    @GetMapping("/missing")
    public ResponseEntity<List<VitalType>> getMissing(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getMissingTypes(tenantId));
    }

    // GET /pi/thresholds/coverage?tenantId=1
    @GetMapping("/coverage")
    public ResponseEntity<ThresholdCoverageResponse> getCoverage(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getCoverage(tenantId));
    }
}
