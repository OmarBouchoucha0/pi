package tn.esprit.pi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.DrugInteractionRequest;
import tn.esprit.pi.dto.DrugInteractionResponse;
import tn.esprit.pi.entity.DrugInteraction;
import tn.esprit.pi.entity.MedicationCatalog;
import tn.esprit.pi.enums.DrugSeverity;
import tn.esprit.pi.repository.DrugInteractionRepository;
import tn.esprit.pi.repository.MedicationCatalogRepository;

@Service
@RequiredArgsConstructor
public class DrugInteractionServiceImpl implements DrugInteractionService {
    private final DrugInteractionRepository drugInteractionRepository;
    private final MedicationCatalogRepository medicationCatalogRepository;

    // ─── Mapping ──────────────────────────────────────────────────

    private DrugInteractionResponse toResponse(DrugInteraction d) {
        return DrugInteractionResponse.builder()
                .id(d.getId())
                .drugAId(d.getDrugA().getId())
                .drugAName(d.getDrugA().getName())
                .drugBId(d.getDrugB().getId())
                .drugBName(d.getDrugB().getName())
                .severity(d.getSeverity())
                .interactionType(d.getInteractionType())
                .description(d.getDescription())
                .recommendation(d.getRecommendation())
                .build();
    }

    // ─── Create ───────────────────────────────────────────────────
    // Empêche les doublons (A↔B = B↔A)

    public DrugInteractionResponse create(DrugInteractionRequest request) {
        MedicationCatalog drugA = medicationCatalogRepository.findById(request.getDrugAId())
                .orElseThrow(() -> new RuntimeException("Drug A non trouvé : " + request.getDrugAId()));
        MedicationCatalog drugB = medicationCatalogRepository.findById(request.getDrugBId())
                .orElseThrow(() -> new RuntimeException("Drug B non trouvé : " + request.getDrugBId()));

        if (drugA.getId().equals(drugB.getId())) {
            throw new RuntimeException("Un médicament ne peut pas interagir avec lui-même");
        }

        // Vérifie doublon dans les deux sens
        drugInteractionRepository.findInteractionBetween(drugA.getId(), drugB.getId())
                .ifPresent(existing -> {
                    throw new RuntimeException("Interaction déjà définie entre "
                            + drugA.getName() + " et " + drugB.getName());
                });

        DrugInteraction interaction = DrugInteraction.builder()
                .drugA(drugA)
                .drugB(drugB)
                .severity(request.getSeverity())
                .interactionType(request.getInteractionType())
                .description(request.getDescription())
                .recommendation(request.getRecommendation())
                .build();

        return toResponse(drugInteractionRepository.save(interaction));
    }

    // ─── Read ─────────────────────────────────────────────────────

    public DrugInteractionResponse getById(Long id) {
        return toResponse(drugInteractionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interaction non trouvée : " + id)));
    }

    public List<DrugInteractionResponse> getAll() {
        return drugInteractionRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<DrugInteractionResponse> getByDrug(Long drugId) {
        return drugInteractionRepository.findByDrugAIdOrDrugBId(drugId, drugId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<DrugInteractionResponse> getBySeverity(DrugSeverity severity) {
        return drugInteractionRepository.findBySeverity(severity)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Vérifie les interactions entre une liste de médicaments (check ordonnance)
    public List<DrugInteractionResponse> checkInteractions(List<Long> drugIds) {
        return drugInteractionRepository.findInteractionsAmong(drugIds)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Update ───────────────────────────────────────────────────

    public DrugInteractionResponse update(Long id, DrugInteractionRequest request) {
        DrugInteraction interaction = drugInteractionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interaction non trouvée : " + id));
        interaction.setSeverity(request.getSeverity());
        interaction.setInteractionType(request.getInteractionType());
        interaction.setDescription(request.getDescription());
        interaction.setRecommendation(request.getRecommendation());
        return toResponse(drugInteractionRepository.save(interaction));
    }

    // ─── Delete ───────────────────────────────────────────────────

    public void delete(Long id) {
        if (!drugInteractionRepository.existsById(id)) {
            throw new RuntimeException("Interaction non trouvée : " + id);
        }
        drugInteractionRepository.deleteById(id);
    }
}
