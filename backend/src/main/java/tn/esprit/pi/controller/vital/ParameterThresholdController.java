package tn.esprit.pi.controller.vital;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Parameter Thresholds", description = "APIs for managing vital parameter thresholds")
public class ParameterThresholdController {

    private final IParameterThresholdService service;

    // ── CRUD ──────────────────────────────────────────────────────────

    // POST /pi/thresholds
    @PostMapping
    @Operation(summary = "Create a parameter threshold", description = "Creates a new vital parameter threshold with min/max values")
    @ApiResponse(responseCode = "201", description = "Threshold created successfully")
    public ResponseEntity<ParameterThreshold> save(@RequestBody @Valid ThresholdRequest req) {
        return ResponseEntity.status(201).body(service.save(req));
    }

    // GET /pi/thresholds/all
    @GetMapping("/all")
    @Operation(summary = "Get all thresholds", description = "Retrieves all parameter thresholds")
    @ApiResponse(responseCode = "200", description = "Thresholds retrieved successfully")
    public ResponseEntity<List<ParameterThreshold>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /pi/thresholds/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get threshold by ID", description = "Retrieves a specific threshold by its ID")
    @ApiResponse(responseCode = "200", description = "Threshold found")
    @ApiResponse(responseCode = "404", description = "Threshold not found")
    public ResponseEntity<ParameterThreshold> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // GET /pi/thresholds?tenantId=1
    @GetMapping
    @Operation(summary = "Get thresholds by tenant", description = "Retrieves all thresholds for a specific tenant")
    @ApiResponse(responseCode = "200", description = "Thresholds retrieved successfully")
    public ResponseEntity<List<ParameterThreshold>> getByTenant(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getByTenant(tenantId));
    }

    // PUT /pi/thresholds/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update a threshold", description = "Updates an existing parameter threshold")
    @ApiResponse(responseCode = "200", description = "Threshold updated successfully")
    @ApiResponse(responseCode = "404", description = "Threshold not found")
    public ResponseEntity<ParameterThreshold> update(
            @PathVariable Long id, @RequestBody @Valid ThresholdRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // DELETE /pi/thresholds/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a threshold", description = "Deletes a parameter threshold by its ID")
    @ApiResponse(responseCode = "204", description = "Threshold deleted successfully")
    @ApiResponse(responseCode = "404", description = "Threshold not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── NEW EASY APIs ─────────────────────────────────────────────────

    // GET /pi/thresholds/missing?tenantId=1
    @GetMapping("/missing")
    @Operation(summary = "Get missing vital types", description = "Retrieves vital parameter types that have no threshold defined for a tenant")
    @ApiResponse(responseCode = "200", description = "Missing types retrieved successfully")
    public ResponseEntity<List<VitalType>> getMissing(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getMissingTypes(tenantId));
    }

    // GET /pi/thresholds/coverage?tenantId=1
    @GetMapping("/coverage")
    @Operation(summary = "Get threshold coverage", description = "Retrieves coverage statistics showing how many vital types have thresholds defined")
    @ApiResponse(responseCode = "200", description = "Coverage retrieved successfully")
    public ResponseEntity<ThresholdCoverageResponse> getCoverage(@RequestParam Long tenantId) {
        return ResponseEntity.ok(service.getCoverage(tenantId));
    }
}
