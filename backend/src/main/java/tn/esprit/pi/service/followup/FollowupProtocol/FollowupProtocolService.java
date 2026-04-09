// FollowupProtocolService.java
package tn.esprit.pi.service.followup.FollowupProtocol;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.followup.ProtocolRequest;
import tn.esprit.pi.dto.followup.ProtocolResponse;
import tn.esprit.pi.dto.followup.ProtocolStepRequest;
import tn.esprit.pi.dto.followup.ProtocolStepResponse;
import tn.esprit.pi.entity.followup.FollowupProtocol;
import tn.esprit.pi.entity.followup.ProtocolStep;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.followup.FollowupProtocolRepository;
import tn.esprit.pi.repository.followup.ProtocolStepRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowupProtocolService {

    private final FollowupProtocolRepository protocolRepo;
    private final ProtocolStepRepository stepRepo;

    // Créer un protocole
    public ProtocolResponse createProtocol(ProtocolRequest request) {
        FollowupProtocol protocol = FollowupProtocol.builder()
                .name(request.getName())
                .durationDays(request.getDurationDays())
                .isActive(request.getIsActive())
                .version(1)
                .build();
        return toResponse(protocolRepo.save(protocol));
    }

    // Lister tous les protocoles
    @Transactional(readOnly = true)
    public List<ProtocolResponse> getAllProtocols() {
        return protocolRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Lister protocoles actifs
    @Transactional(readOnly = true)
    public List<ProtocolResponse> getActiveProtocols() {
        return protocolRepo.findByIsActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Obtenir un protocole par ID
    @Transactional(readOnly = true)
    public ProtocolResponse getProtocolById(String id) {
        return toResponse(findProtocolOrThrow(id));
    }

    // Mettre à jour un protocole (crée une nouvelle version)
    public ProtocolResponse updateProtocol(String id, ProtocolRequest request) {
        FollowupProtocol existing = findProtocolOrThrow(id);
        existing.setName(request.getName());
        existing.setDurationDays(request.getDurationDays());
        existing.setVersion(existing.getVersion() + 1);
        return toResponse(protocolRepo.save(existing));
    }

    // Activer / désactiver
    public ProtocolResponse toggleActive(String id) {
        FollowupProtocol protocol = findProtocolOrThrow(id);
        protocol.setIsActive(!protocol.getIsActive());
        return toResponse(protocolRepo.save(protocol));
    }

    // Supprimer
    public void deleteProtocol(String id) {
        findProtocolOrThrow(id);
        protocolRepo.deleteById(id);
    }

    // Ajouter une étape
    public ProtocolStepResponse addStep(String protocolId, ProtocolStepRequest request) {
        FollowupProtocol protocol = findProtocolOrThrow(protocolId);
        ProtocolStep step = ProtocolStep.builder()
                .protocol(protocol)
                .dayNumber(request.getDayNumber())
                .actionType(request.getActionType())
                .mandatory(request.getMandatory())
                .weight(request.getWeight())
                .build();
        return toStepResponse(stepRepo.save(step));
    }

    // Obtenir les étapes d'un protocole
    @Transactional(readOnly = true)
    public List<ProtocolStepResponse> getSteps(String protocolId) {
        findProtocolOrThrow(protocolId);
        return stepRepo.findByProtocolIdOrderByDayNumber(protocolId)
                .stream()
                .map(this::toStepResponse)
                .toList();
    }

    // Supprimer une étape
    public void deleteStep(String stepId) {
        if (!stepRepo.existsById(stepId)) {
            throw new ResourceNotFoundException("Step not found: " + stepId);
        }
        stepRepo.deleteById(stepId);
    }

    // ---- Helpers ----

    private FollowupProtocol findProtocolOrThrow(String id) {
        return protocolRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocol not found: " + id));
    }

    private ProtocolResponse toResponse(FollowupProtocol p) {
        return ProtocolResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .durationDays(p.getDurationDays())
                .version(p.getVersion())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .steps(p.getSteps().stream().map(this::toStepResponse).toList())
                .build();
    }


    private ProtocolStepResponse toStepResponse(ProtocolStep s) {
        return ProtocolStepResponse.builder()
                .id(s.getId())
                .dayNumber(s.getDayNumber())
                .actionType(s.getActionType())
                .mandatory(s.getMandatory())
                .weight(s.getWeight())
                .build();
    }
}
