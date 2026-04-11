package tn.esprit.pi.service.vital;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.vital.ThresholdCoverageResponse;
import tn.esprit.pi.dto.vital.ThresholdRequest;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.vital.ParameterThreshold;
import tn.esprit.pi.enums.vital.VitalType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.vital.ParameterThresholdRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ParameterThresholdServiceImpl implements IParameterThresholdService {

    private final ParameterThresholdRepository thresholdRepo;
    private final TenantRepository tenantRepo;

    // ══════════════════════════════════════
    // CRUD
    // ══════════════════════════════════════

    @Override
    public ParameterThreshold save(ThresholdRequest req) {
        Tenant tenant = tenantRepo.findById(req.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + req.getTenantId()));

        ParameterThreshold threshold = thresholdRepo
                .findByTenantIdAndType(req.getTenantId(), req.getType())
                .orElse(new ParameterThreshold());

        threshold.setTenant(tenant);
        threshold.setType(req.getType());
        threshold.setMinValue(req.getMinValue());
        threshold.setMaxValue(req.getMaxValue());
        threshold.setCriticalMin(req.getCriticalMin());
        threshold.setCriticalMax(req.getCriticalMax());

        return thresholdRepo.save(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParameterThreshold> getAll() {
        return thresholdRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ParameterThreshold getById(Long id) {
        return thresholdRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Threshold not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParameterThreshold> getByTenant(Long tenantId) {
        return thresholdRepo.findByTenantId(tenantId);
    }

    @Override
    public ParameterThreshold update(Long id, ThresholdRequest req) {
        ParameterThreshold t = getById(id);
        t.setMinValue(req.getMinValue());
        t.setMaxValue(req.getMaxValue());
        t.setCriticalMin(req.getCriticalMin());
        t.setCriticalMax(req.getCriticalMax());
        return thresholdRepo.save(t);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        thresholdRepo.deleteById(id);
    }

    // ══════════════════════════════════════
    // NEW EASY APIs
    // ══════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<VitalType> getMissingTypes(Long tenantId) {
        Set<VitalType> configured = thresholdRepo.findByTenantId(tenantId)
                .stream().map(ParameterThreshold::getType).collect(Collectors.toSet());

        return Arrays.stream(VitalType.values())
                .filter(type -> !configured.contains(type))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ThresholdCoverageResponse getCoverage(Long tenantId) {
        List<ParameterThreshold> configured = thresholdRepo.findByTenantId(tenantId);
        int total   = VitalType.values().length;
        int covered = configured.size();
        int percent = (int) Math.round((covered * 100.0) / total);

        Set<VitalType> coveredTypes = configured.stream()
                .map(ParameterThreshold::getType).collect(Collectors.toSet());

        List<VitalType> missing = Arrays.stream(VitalType.values())
                .filter(t -> !coveredTypes.contains(t))
                .collect(Collectors.toList());

        return ThresholdCoverageResponse.builder()
                .tenantId(tenantId)
                .totalTypes(total)
                .coveredTypes(covered)
                .coveragePercent(percent)
                .covered(List.copyOf(coveredTypes))
                .missing(missing)
                .build();
    }
}
