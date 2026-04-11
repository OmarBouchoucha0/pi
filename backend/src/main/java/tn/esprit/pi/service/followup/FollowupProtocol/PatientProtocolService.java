// PatientProtocolService.java
package tn.esprit.pi.service.followup.FollowupProtocol;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.followup.ExecutionResponse;
import tn.esprit.pi.dto.followup.ExecutionUpdateRequest;
import tn.esprit.pi.dto.followup.PatientProtocolRequest;
import tn.esprit.pi.dto.followup.PatientProtocolResponse;
import tn.esprit.pi.entity.followup.FollowupProtocol;
import tn.esprit.pi.entity.followup.PatientProtocol;
import tn.esprit.pi.entity.followup.ProtocolExecution;
import tn.esprit.pi.entity.followup.ProtocolStep;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.enums.followup.ExecutionStatus;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.followup.FollowupProtocolRepository;
import tn.esprit.pi.repository.followup.PatientProtocolRepository;
import tn.esprit.pi.repository.followup.ProtocolExecutionRepository;
import tn.esprit.pi.repository.followup.ProtocolStepRepository;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientProtocolService {

    private final PatientProtocolRepository patientProtocolRepo;
    private final ProtocolExecutionRepository executionRepo;
    private final FollowupProtocolRepository protocolRepo;
    private final ProtocolStepRepository stepRepo;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

// Assigner un protocole à un patient
    public PatientProtocolResponse assignProtocol(PatientProtocolRequest request) {

        // 1. Récupérer le protocole
        FollowupProtocol protocol = protocolRepo.findById(request.getProtocolId())
                .orElseThrow(() -> new ResourceNotFoundException("Protocol not found"));

        // 2. Récupérer le patient (User)
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // 3. Récupérer le tenant
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        // 4. Calculer les dates
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = startDate.plusDays(protocol.getDurationDays());

        // 5. Créer l'assignation
        PatientProtocol patientProtocol = PatientProtocol.builder()
                .patient(patient)
                .protocol(protocol)
                .tenant(tenant)
                .startDate(startDate)
                .endDate(endDate)
                .complianceScore(0.0)
                .riskFlag(false)
                .build();

        PatientProtocol saved = patientProtocolRepo.save(patientProtocol);

        // 6. Générer automatiquement les exécutions
        List<ProtocolStep> steps = stepRepo
                .findByProtocolIdOrderByDayNumber(protocol.getId());

        List<ProtocolExecution> executions = steps.stream()
                .map(step -> ProtocolExecution.builder()
                        .patientProtocol(saved)
                        .step(step)
                        .status(ExecutionStatus.PENDING)
                        .build())
                .toList();

        executionRepo.saveAll(executions);

        return toResponse(patientProtocolRepo.findById(saved.getId()).orElseThrow());
    }

    // Lister les protocoles d'un patient
    @Transactional(readOnly = true)
    public List<PatientProtocolResponse> getByPatient(String patientId) {
        return patientProtocolRepo.findByPatientId(Long.valueOf(patientId))
                .stream().map(this::toResponse).toList();
    }

    // Obtenir un patient-protocol par ID
    @Transactional(readOnly = true)
    public PatientProtocolResponse getById(String id) {
        return toResponse(findOrThrow(id));
    }

    // Mettre à jour le statut d'une exécution
    public ExecutionResponse updateExecution(String executionId,
                                             ExecutionUpdateRequest request) {
        ProtocolExecution execution = executionRepo.findById(executionId)
                .orElseThrow(() -> new ResourceNotFoundException("Execution not found: " + executionId));

        execution.setStatus(request.getStatus());
        execution.setDelayMinutes(request.getDelayMinutes());

        if (request.getStatus() == ExecutionStatus.DONE
                || request.getStatus() == ExecutionStatus.LATE) {
            execution.setCompletedAt(LocalDateTime.now());
        }

        ProtocolExecution saved = executionRepo.save(execution);

        // Recalculer compliance et risk après update
        recalculateCompliance(execution.getPatientProtocol().getId());

        return toExecutionResponse(saved);
    }

    // Recalculer le score de compliance
    public void recalculateCompliance(String patientProtocolId) {
        PatientProtocol pp = findOrThrow(patientProtocolId);

        long total = executionRepo.countByPatientProtocolId(patientProtocolId);
        long done  = executionRepo.countByPatientProtocolIdAndStatus(
                patientProtocolId, ExecutionStatus.DONE);
        long late  = executionRepo.countByPatientProtocolIdAndStatus(
                patientProtocolId, ExecutionStatus.LATE);
        long missed = executionRepo.countByPatientProtocolIdAndStatus(
                patientProtocolId, ExecutionStatus.MISSED);

        double score = total > 0 ? (double)(done + late) / total * 100 : 0.0;
        boolean risk = score < 50.0 || missed > 2;

        pp.setComplianceScore(Math.round(score * 100.0) / 100.0);
        pp.setRiskFlag(risk);
        patientProtocolRepo.save(pp);
    }

    // Lister les patients à risque
    @Transactional(readOnly = true)
    public List<PatientProtocolResponse> getRiskPatients() {
        return patientProtocolRepo.findByRiskFlagTrue()
                .stream().map(this::toResponse).toList();
    }

    // ---- Helpers ----

    private PatientProtocol findOrThrow(String id) {
        return patientProtocolRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PatientProtocol not found: " + id));
    }

    private PatientProtocolResponse toResponse(PatientProtocol pp) {
        return PatientProtocolResponse.builder()
                .id(pp.getId())
                .patientId(pp.getPatient().getId())
                .patientName(pp.getPatient().getFirstName() + " "
                        + pp.getPatient().getLastName())
                .protocolId(pp.getProtocol().getId())
                .protocolName(pp.getProtocol().getName())
                .tenantId(pp.getTenant().getId())
                .tenantName(pp.getTenant().getName())
                .startDate(pp.getStartDate())
                .endDate(pp.getEndDate())
                .complianceScore(pp.getComplianceScore())
                .riskFlag(pp.getRiskFlag())
                .executions(
                        pp.getExecutions() == null
                                ? List.of()
                                : pp.getExecutions().stream()
                                .map(this::toExecutionResponse)
                                .toList()
                )
                .build();
    }

    private ExecutionResponse toExecutionResponse(ProtocolExecution e) {
        return ExecutionResponse.builder()
                .id(e.getId())
                .stepId(e.getStep().getId())
                .actionType(e.getStep().getActionType())
                .dayNumber(e.getStep().getDayNumber())
                .status(e.getStatus())
                .delayMinutes(e.getDelayMinutes())
                .completedAt(e.getCompletedAt())
                .build();
    }
}
