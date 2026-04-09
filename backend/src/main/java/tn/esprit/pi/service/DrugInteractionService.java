package tn.esprit.pi.service;

import java.util.List;

import tn.esprit.pi.dto.DrugInteractionRequest;
import tn.esprit.pi.dto.DrugInteractionResponse;
import tn.esprit.pi.enums.DrugSeverity;

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
