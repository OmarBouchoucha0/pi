package tn.esprit.pi.service.vital;

import java.time.LocalDateTime;
import java.util.List;

import tn.esprit.pi.dto.vital.VitalParameterRequest;
import tn.esprit.pi.dto.vital.VitalParameterResponse;
import tn.esprit.pi.dto.vital.VitalStatsResponse;
import tn.esprit.pi.dto.vital.VitalSummaryResponse;
import tn.esprit.pi.enums.vital.VitalType;

public interface IVitalParameterService {

    // ── CRUD ──
    VitalParameterResponse addVital(VitalParameterRequest request);
    List<VitalParameterResponse> getAll();
    VitalParameterResponse getById(Long id);
    List<VitalParameterResponse> getByPatient(Long patientId, Long tenantId);
    List<VitalParameterResponse> getByPatientAndType(Long patientId, Long tenantId, VitalType type);
    VitalParameterResponse update(Long id, VitalParameterRequest request);
    void delete(Long id);

    // ── Advanced existing ──
    List<VitalParameterResponse> getLatestPerType(Long patientId, Long tenantId);
    String classifyValue(Double value, Long tenantId, VitalType type);

    // ── NEW easy APIs ──
    VitalStatsResponse getStats(Long patientId, Long tenantId, VitalType type,
                                LocalDateTime from, LocalDateTime to);
    List<VitalSummaryResponse> getWardSummary(Long tenantId);
    List<VitalSummaryResponse> getCriticalPatients(Long tenantId);
}
