package tn.esprit.pi.service.intake;

import java.util.List;
import java.util.Map;

import tn.esprit.pi.dto.intake.IntakeLogRequest;
import tn.esprit.pi.dto.intake.IntakeLogResponse;
import tn.esprit.pi.enums.intake.IntakeStatus;

public interface IntakeLogService {
    IntakeLogResponse create(IntakeLogRequest request);
    IntakeLogResponse getById(Long id);
    List<IntakeLogResponse> getAll();
    List<IntakeLogResponse> getByPrescription(Long prescriptionId);
    List<IntakeLogResponse> getByStatus(IntakeStatus status);
    Map<String, Object> getAdherenceStats(Long prescriptionId);
    void delete(Long id);
    IntakeLogResponse update(Long id, IntakeLogRequest request);
}
