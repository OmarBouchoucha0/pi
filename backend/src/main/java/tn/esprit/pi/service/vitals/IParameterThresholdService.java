package tn.esprit.pi.service.vitals;

import java.util.List;

import tn.esprit.pi.dto.request.ThresholdRequest;
import tn.esprit.pi.dto.response.ThresholdCoverageResponse;
import tn.esprit.pi.entity.ParameterThreshold;
import tn.esprit.pi.enums.VitalType;

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
