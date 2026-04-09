package tn.esprit.pi.service;

import java.util.List;
import java.util.Map;

import tn.esprit.pi.dto.IntakeLogRequest;
import tn.esprit.pi.dto.IntakeLogResponse;
import tn.esprit.pi.enums.IntakeStatus;

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
