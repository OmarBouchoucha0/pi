package tn.esprit.pi.controller.medication;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.medication.MedicationCatalogRequest;
import tn.esprit.pi.dto.medication.MedicationCatalogResponse;
import tn.esprit.pi.service.medication.MedicationCatalogService;

@RestController
@RequestMapping("/medications")
@RequiredArgsConstructor
public class MedicationCatalogController {

    private final MedicationCatalogService medicationCatalogService;

    // POST /api/medications
    @PostMapping
    public ResponseEntity<MedicationCatalogResponse> create(
            @RequestBody MedicationCatalogRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(medicationCatalogService.create(request));
    }

    // GET /api/medications
    @GetMapping
    public ResponseEntity<List<MedicationCatalogResponse>> getAll() {
        return ResponseEntity.ok(medicationCatalogService.getAll());
    }

    // GET /api/medications/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MedicationCatalogResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(medicationCatalogService.getById(id));
    }

    // GET /api/medications/search?name=para
    @GetMapping("/search")
    public ResponseEntity<List<MedicationCatalogResponse>> searchByName(
            @RequestParam String name) {
        return ResponseEntity.ok(medicationCatalogService.searchByName(name));
    }

    // GET /api/medications/category/{category}
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MedicationCatalogResponse>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(medicationCatalogService.getByCategory(category));
    }

    // PUT /api/medications/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MedicationCatalogResponse> update(
            @PathVariable Long id,
            @RequestBody MedicationCatalogRequest request) {
        return ResponseEntity.ok(medicationCatalogService.update(id, request));
    }

    // DELETE /api/medications/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicationCatalogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
