package tn.esprit.pi.service.drug;

import java.util.List;

import tn.esprit.pi.dto.drug.DrugInteractionRequest;
import tn.esprit.pi.dto.drug.DrugInteractionResponse;
import tn.esprit.pi.enums.drug.DrugSeverity;

public interface DrugInteractionService {
    DrugInteractionResponse create(DrugInteractionRequest request);
    DrugInteractionResponse getById(Long id);
    List<DrugInteractionResponse> getAll();
    List<DrugInteractionResponse> getByDrug(Long drugId);
    List<DrugInteractionResponse> getBySeverity(DrugSeverity severity);
    List<DrugInteractionResponse> checkInteractions(List<Long> drugIds);
    DrugInteractionResponse update(Long id, DrugInteractionRequest request);
    void delete(Long id);
}
