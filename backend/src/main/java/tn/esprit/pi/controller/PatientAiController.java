package tn.esprit.pi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.response.AiExplanationResponse;
import tn.esprit.pi.dto.response.AiPredictionResponse;
import tn.esprit.pi.service.ai.IPatientAiService;

@RestController
@RequestMapping("/patient-states")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientAiController {

    private final IPatientAiService aiService;

    // POST /pi/patient-states/patient/{id}/ai-explain?tenantId=1
    @PostMapping("/patient/{id}/ai-explain")
    public ResponseEntity<AiExplanationResponse> explain(
            @PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseEntity.ok(aiService.explainRisk(id, tenantId));
    }

    // POST /pi/patient-states/patient/{id}/ai-predict?tenantId=1
    @PostMapping("/patient/{id}/ai-predict")
    public ResponseEntity<AiPredictionResponse> predict(
            @PathVariable Long id, @RequestParam Long tenantId) {
        return ResponseEntity.ok(aiService.predictEvolution(id, tenantId));
    }
}
