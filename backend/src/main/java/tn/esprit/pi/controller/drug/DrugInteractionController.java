package tn.esprit.pi.controller.drug;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.drug.DrugInteractionRequest;
import tn.esprit.pi.dto.drug.DrugInteractionResponse;
import tn.esprit.pi.enums.drug.DrugSeverity;
import tn.esprit.pi.service.drug.DrugInteractionService;

@RestController
@RequestMapping("/drug-interactions")
@RequiredArgsConstructor
@Tag(name = "Drug Interactions", description = "APIs for managing drug interactions and contraindications")
public class DrugInteractionController {

    private final DrugInteractionService drugInteractionService;

// POST /api/drug-interactions
    @PostMapping
    @Operation(summary = "Create drug interaction", description = "Creates a new drug interaction rule")
    @ApiResponse(responseCode = "201", description = "Drug interaction created successfully")
    public ResponseEntity<DrugInteractionResponse> create(
            @RequestBody DrugInteractionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(drugInteractionService.create(request));
    }

    // GET /api/drug-interactions
    @GetMapping
    @Operation(summary = "Get all drug interactions", description = "Retrieves all drug interaction rules")
    @ApiResponse(responseCode = "200", description = "Drug interactions retrieved successfully")
    public ResponseEntity<List<DrugInteractionResponse>> getAll() {
        return ResponseEntity.ok(drugInteractionService.getAll());
    }

    // GET /api/drug-interactions/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get drug interaction by ID", description = "Retrieves a specific drug interaction by its ID")
    @ApiResponse(responseCode = "200", description = "Drug interaction found")
    @ApiResponse(responseCode = "404", description = "Drug interaction not found")
    public ResponseEntity<DrugInteractionResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(drugInteractionService.getById(id));
    }

    // GET /api/drug-interactions/drug/{drugId}
    @GetMapping("/drug/{drugId}")
    @Operation(summary = "Get interactions by drug", description = "Retrieves all interactions involving a specific drug")
    @ApiResponse(responseCode = "200", description = "Interactions retrieved successfully")
    public ResponseEntity<List<DrugInteractionResponse>> getByDrug(
            @PathVariable Long drugId) {
        return ResponseEntity.ok(drugInteractionService.getByDrug(drugId));
    }

    // GET /api/drug-interactions/severity/{severity}
    @GetMapping("/severity/{severity}")
    @Operation(summary = "Get interactions by severity", description = "Retrieves all interactions of a specific severity level")
    @ApiResponse(responseCode = "200", description = "Interactions retrieved successfully")
    public ResponseEntity<List<DrugInteractionResponse>> getBySeverity(
            @PathVariable DrugSeverity severity) {
        return ResponseEntity.ok(drugInteractionService.getBySeverity(severity));
    }

    // POST /api/drug-interactions/check
    @PostMapping("/check")
    @Operation(summary = "Check drug interactions", description = "Checks for interactions between a list of drug IDs")
    @ApiResponse(responseCode = "200", description = "Interactions checked successfully")
    public ResponseEntity<List<DrugInteractionResponse>> checkInteractions(
            @RequestBody List<Long> drugIds) {
        return ResponseEntity.ok(drugInteractionService.checkInteractions(drugIds));
    }

    // PUT /api/drug-interactions/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update drug interaction", description = "Updates an existing drug interaction rule")
    @ApiResponse(responseCode = "200", description = "Drug interaction updated successfully")
    @ApiResponse(responseCode = "404", description = "Drug interaction not found")
    public ResponseEntity<DrugInteractionResponse> update(
            @PathVariable Long id,
            @RequestBody DrugInteractionRequest request) {
        return ResponseEntity.ok(drugInteractionService.update(id, request));
    }

    // DELETE /api/drug-interactions/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete drug interaction", description = "Deletes a drug interaction rule")
    @ApiResponse(responseCode = "204", description = "Drug interaction deleted successfully")
    @ApiResponse(responseCode = "404", description = "Drug interaction not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        drugInteractionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
