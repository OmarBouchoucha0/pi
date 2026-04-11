package tn.esprit.pi.service.vital;

import java.util.List;

import tn.esprit.pi.dto.vital.ThresholdCoverageResponse;
import tn.esprit.pi.dto.vital.ThresholdRequest;
import tn.esprit.pi.entity.vital.ParameterThreshold;
import tn.esprit.pi.enums.vital.VitalType;

public interface IParameterThresholdService {

    // ── CRUD ──
    ParameterThreshold save(ThresholdRequest request);
    List<ParameterThreshold> getAll();
    ParameterThreshold getById(Long id);
    List<ParameterThreshold> getByTenant(Long tenantId);
    ParameterThreshold update(Long id, ThresholdRequest request);
    void delete(Long id);

    // ── NEW easy APIs ──
    List<VitalType> getMissingTypes(Long tenantId);
    ThresholdCoverageResponse getCoverage(Long tenantId);
}
