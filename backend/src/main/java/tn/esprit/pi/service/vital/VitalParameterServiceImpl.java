package tn.esprit.pi.service.vital;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.vital.VitalParameterRequest;
import tn.esprit.pi.dto.vital.VitalParameterResponse;
import tn.esprit.pi.dto.vital.VitalStatsResponse;
import tn.esprit.pi.dto.vital.VitalSummaryResponse;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.entity.user.User;
import tn.esprit.pi.entity.vital.ParameterThreshold;
import tn.esprit.pi.entity.vital.VitalParameter;
import tn.esprit.pi.enums.vital.VitalStatus;
import tn.esprit.pi.enums.vital.VitalType;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.TenantRepository;
import tn.esprit.pi.repository.user.UserRepository;
import tn.esprit.pi.repository.vital.ParameterThresholdRepository;
import tn.esprit.pi.repository.vital.VitalParameterRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class VitalParameterServiceImpl implements IVitalParameterService {

    private final VitalParameterRepository vitalRepo;
    private final ParameterThresholdRepository thresholdRepo;
    private final UserRepository userRepo;
    private final TenantRepository tenantRepo;

    // ══════════════════════════════════════
    // CRUD
    // ══════════════════════════════════════

    @Override
    public VitalParameterResponse addVital(VitalParameterRequest req) {
        User patient = userRepo.findById(req.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + req.getPatientId()));
        Tenant tenant = tenantRepo.findById(req.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + req.getTenantId()));

        VitalStatus status = classify(req.getValue(), req.getTenantId(), req.getType());
        Double normalized = normalize(req.getValue(), req.getTenantId(), req.getType());

        VitalParameter vp = VitalParameter.builder()
                .patient(patient).tenant(tenant)
                .type(req.getType()).value(req.getValue())
                .unit(req.getUnit()).normalizedValue(normalized).status(status)
                .build();

        return toResponse(vitalRepo.save(vp));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalParameterResponse> getAll() {
        return vitalRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VitalParameterResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalParameterResponse> getByPatient(Long patientId, Long tenantId) {
        return vitalRepo.findByPatientIdAndTenantIdOrderByRecordedAtDesc(patientId, tenantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalParameterResponse> getByPatientAndType(Long patientId, Long tenantId, VitalType type) {
        return vitalRepo.findByPatientIdAndTenantIdAndTypeOrderByRecordedAtDesc(patientId, tenantId, type)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public VitalParameterResponse update(Long id, VitalParameterRequest req) {
        VitalParameter vp = findOrThrow(id);
        vp.setValue(req.getValue());
        vp.setType(req.getType());
        vp.setUnit(req.getUnit());
        vp.setStatus(classify(req.getValue(), vp.getTenant().getId(), req.getType()));
        vp.setNormalizedValue(normalize(req.getValue(), vp.getTenant().getId(), req.getType()));
        return toResponse(vitalRepo.save(vp));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        vitalRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalParameterResponse> getLatestPerType(Long patientId, Long tenantId) {
        return vitalRepo.findLatestPerTypeByPatientAndTenant(patientId, tenantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public String classifyValue(Double value, Long tenantId, VitalType type) {
        return classify(value, tenantId, type).name();
    }

    // ══════════════════════════════════════
    // NEW EASY APIs
    // ══════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public VitalStatsResponse getStats(Long patientId, Long tenantId, VitalType type,
                                       LocalDateTime from, LocalDateTime to) {
        List<Object[]> rows = vitalRepo.findStatsForPatient(patientId, tenantId, type, from, to);

        if (rows.isEmpty() || rows.get(0)[0] == null) {
            return VitalStatsResponse.builder()
                    .patientId(patientId).type(type)
                    .min(null).max(null).avg(null).count(0L)
                    .from(from).to(to).build();
        }

        Object[] row = rows.get(0);
        return VitalStatsResponse.builder()
                .patientId(patientId)
                .type(type)
                .min(((Number) row[0]).doubleValue())
                .max(((Number) row[1]).doubleValue())
                .avg(Math.round(((Number) row[2]).doubleValue() * 100.0) / 100.0)
                .count(((Number) row[3]).longValue())
                .from(from)
                .to(to)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalSummaryResponse> getWardSummary(Long tenantId) {
        List<Long> patientIds = vitalRepo.findDistinctPatientIdsByTenantId(tenantId);
        return patientIds.stream()
                .map(pid -> buildSummary(pid, tenantId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalSummaryResponse> getCriticalPatients(Long tenantId) {
        List<Long> criticalIds = vitalRepo.findDistinctPatientIdsByTenantIdAndStatus(tenantId, VitalStatus.CRITICAL);
        return criticalIds.stream()
                .map(pid -> buildSummary(pid, tenantId))
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════

    private VitalSummaryResponse buildSummary(Long patientId, Long tenantId) {
        User patient = userRepo.findById(patientId).orElse(null);
        long total    = vitalRepo.countByPatientIdAndTenantId(patientId, tenantId);
        long normal   = vitalRepo.countByPatientIdAndTenantIdAndStatus(patientId, tenantId, VitalStatus.NORMAL);
        long warning  = vitalRepo.countByPatientIdAndTenantIdAndStatus(patientId, tenantId, VitalStatus.WARNING);
        long critical = vitalRepo.countByPatientIdAndTenantIdAndStatus(patientId, tenantId, VitalStatus.CRITICAL);

        VitalStatus worst = critical > 0 ? VitalStatus.CRITICAL
                          : warning  > 0 ? VitalStatus.WARNING
                          : VitalStatus.NORMAL;

        return VitalSummaryResponse.builder()
                .patientId(patientId)
                .patientFirstName(patient != null ? patient.getFirstName() : "Unknown")
                .patientLastName(patient != null ? patient.getLastName() : "")
                .totalVitals(total)
                .normalCount(normal)
                .warningCount(warning)
                .criticalCount(critical)
                .worstStatus(worst)
                .build();
    }

    private VitalStatus classify(Double value, Long tenantId, VitalType type) {
        Optional<ParameterThreshold> opt = thresholdRepo.findByTenantIdAndType(tenantId, type);
        if (opt.isEmpty()) return VitalStatus.NORMAL;
        ParameterThreshold t = opt.get();
        if (t.getCriticalMin() != null && value <= t.getCriticalMin()) return VitalStatus.CRITICAL;
        if (t.getCriticalMax() != null && value >= t.getCriticalMax()) return VitalStatus.CRITICAL;
        if (t.getMinValue()    != null && value <= t.getMinValue())    return VitalStatus.WARNING;
        if (t.getMaxValue()    != null && value >= t.getMaxValue())    return VitalStatus.WARNING;
        return VitalStatus.NORMAL;
    }

    private Double normalize(Double value, Long tenantId, VitalType type) {
        Optional<ParameterThreshold> opt = thresholdRepo.findByTenantIdAndType(tenantId, type);
        if (opt.isEmpty()) return null;
        ParameterThreshold t = opt.get();
        if (t.getCriticalMin() == null || t.getCriticalMax() == null) return null;
        double range = t.getCriticalMax() - t.getCriticalMin();
        if (range == 0) return 50.0;
        return ((value - t.getCriticalMin()) / range) * 100.0;
    }

    private VitalParameter findOrThrow(Long id) {
        return vitalRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VitalParameter not found: " + id));
    }

    private VitalParameterResponse toResponse(VitalParameter v) {
        return VitalParameterResponse.builder()
                .id(v.getId())
                .patientId(v.getPatient().getId())
                .tenantId(v.getTenant().getId())
                .type(v.getType()).value(v.getValue())
                .unit(v.getUnit()).normalizedValue(v.getNormalizedValue())
                .status(v.getStatus()).recordedAt(v.getRecordedAt())
                .build();
    }
}
