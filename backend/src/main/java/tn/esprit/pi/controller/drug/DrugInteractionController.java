package tn.esprit.pi.controller.drug;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.drug.DrugInteractionRequest;
import tn.esprit.pi.dto.drug.DrugInteractionResponse;
import tn.esprit.pi.enums.drug.DrugSeverity;
import tn.esprit.pi.service.drug.DrugInteractionService;

@RestController
@RequestMapping("/drug-interactions")
@RequiredArgsConstructor
public class DrugInteractionController {

    private final DrugInteractionService drugInteractionService;

    // POST /api/drug-interactions
    @PostMapping
    public ResponseEntity<DrugInteractionResponse> create(
            @RequestBody DrugInteractionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(drugInteractionService.create(request));
    }

    // GET /api/drug-interactions
    @GetMapping
    public ResponseEntity<List<DrugInteractionResponse>> getAll() {
        return ResponseEntity.ok(drugInteractionService.getAll());
    }

    // GET /api/drug-interactions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DrugInteractionResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(drugInteractionService.getById(id));
    }

    // GET /api/drug-interactions/drug/{drugId}
    @GetMapping("/drug/{drugId}")
    public ResponseEntity<List<DrugInteractionResponse>> getByDrug(
            @PathVariable Long drugId) {
        return ResponseEntity.ok(drugInteractionService.getByDrug(drugId));
    }

    // GET /api/drug-interactions/severity/{severity}
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<DrugInteractionResponse>> getBySeverity(
            @PathVariable DrugSeverity severity) {
        return ResponseEntity.ok(drugInteractionService.getBySeverity(severity));
    }

    // POST /api/drug-interactions/check
    // Body: [1, 2, 3]  → vérifie toutes les interactions entre ces médicaments
    @PostMapping("/check")
    public ResponseEntity<List<DrugInteractionResponse>> checkInteractions(
            @RequestBody List<Long> drugIds) {
        return ResponseEntity.ok(drugInteractionService.checkInteractions(drugIds));
    }

    // PUT /api/drug-interactions/{id}
    @PutMapping("/{id}")
    public ResponseEntity<DrugInteractionResponse> update(
            @PathVariable Long id,
            @RequestBody DrugInteractionRequest request) {
        return ResponseEntity.ok(drugInteractionService.update(id, request));
    }

    // DELETE /api/drug-interactions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        drugInteractionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
