package tn.esprit.pi.service.vitals;

import java.util.List;
import java.util.Map;

import tn.esprit.pi.dto.response.PatientStateEvolutionResponse;
import tn.esprit.pi.dto.response.PatientStateResponse;

public interface IPatientStateService {

    // ── CRUD ──
    PatientStateResponse recalculate(Long patientId, Long tenantId);
    List<PatientStateResponse> getAll();
    PatientStateResponse getLatest(Long patientId, Long tenantId);
    PatientStateResponse getById(Long id);
    List<PatientStateResponse> getHistory(Long patientId, Long tenantId);
    void delete(Long id);

    // ── NEW easy APIs ──
    List<PatientStateEvolutionResponse> getEvolution(Long patientId, Long tenantId, int days);
    Map<String, Object> getWorseningStreak(Long patientId, Long tenantId);
    List<PatientStateResponse> getAtRisk(Long tenantId, Double minScore);
}
