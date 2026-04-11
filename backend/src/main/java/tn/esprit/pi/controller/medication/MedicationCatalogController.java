package tn.esprit.pi.controller.medication;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.medication.MedicationCatalogRequest;
import tn.esprit.pi.dto.medication.MedicationCatalogResponse;
import tn.esprit.pi.service.medication.MedicationCatalogService;

@RestController
@RequestMapping("/medications")
@RequiredArgsConstructor
@Tag(name = "Medication Catalog", description = "APIs for managing medication catalog entries")
public class MedicationCatalogController {

    private final MedicationCatalogService medicationCatalogService;

    // POST /api/medications
    @PostMapping
    @Operation(summary = "Create medication", description = "Creates a new medication catalog entry")
    @ApiResponse(responseCode = "201", description = "Medication created successfully")
    public ResponseEntity<MedicationCatalogResponse> create(
            @RequestBody MedicationCatalogRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(medicationCatalogService.create(request));
    }

    // GET /api/medications
    @GetMapping
    @Operation(summary = "Get all medications", description = "Retrieves all medications from the catalog")
    @ApiResponse(responseCode = "200", description = "Medications retrieved successfully")
    public ResponseEntity<List<MedicationCatalogResponse>> getAll() {
        return ResponseEntity.ok(medicationCatalogService.getAll());
    }

    // GET /api/medications/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get medication by ID", description = "Retrieves a specific medication by its ID")
    @ApiResponse(responseCode = "200", description = "Medication found")
    @ApiResponse(responseCode = "404", description = "Medication not found")
    public ResponseEntity<MedicationCatalogResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(medicationCatalogService.getById(id));
    }

    // GET /api/medications/search?name=para
    @GetMapping("/search")
    @Operation(summary = "Search medications", description = "Searches medications by name")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<MedicationCatalogResponse>> searchByName(
            @RequestParam String name) {
        return ResponseEntity.ok(medicationCatalogService.searchByName(name));
    }

    // GET /api/medications/category/{category}
    @GetMapping("/category/{category}")
    @Operation(summary = "Get medications by category", description = "Retrieves all medications in a specific category")
    @ApiResponse(responseCode = "200", description = "Medications retrieved successfully")
    public ResponseEntity<List<MedicationCatalogResponse>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(medicationCatalogService.getByCategory(category));
    }

    // PUT /api/medications/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update medication", description = "Updates an existing medication catalog entry")
    @ApiResponse(responseCode = "200", description = "Medication updated successfully")
    @ApiResponse(responseCode = "404", description = "Medication not found")
    public ResponseEntity<MedicationCatalogResponse> update(
            @PathVariable Long id,
            @RequestBody MedicationCatalogRequest request) {
        return ResponseEntity.ok(medicationCatalogService.update(id, request));
    }

    // DELETE /api/medications/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medication", description = "Deletes a medication from the catalog")
    @ApiResponse(responseCode = "204", description = "Medication deleted successfully")
    @ApiResponse(responseCode = "404", description = "Medication not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicationCatalogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
