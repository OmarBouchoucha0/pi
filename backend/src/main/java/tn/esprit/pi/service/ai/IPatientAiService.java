package tn.esprit.pi.service.ai;

import tn.esprit.pi.dto.response.AiExplanationResponse;
import tn.esprit.pi.dto.response.AiPredictionResponse;

public interface IPatientAiService {
    AiExplanationResponse explainRisk(Long patientId, Long tenantId);
    AiPredictionResponse predictEvolution(Long patientId, Long tenantId);
}
