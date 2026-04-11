package tn.esprit.pi.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.user.HospitalUpdateRequest;
import tn.esprit.pi.entity.user.Hospital;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.HospitalRepository;
import tn.esprit.pi.repository.user.TenantRepository;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final TenantRepository tenantRepository;

    public List<Hospital> findAll() {
        return hospitalRepository.findAllByDeletedAtIsNull();
    }

    public Optional<Hospital> findById(Long id) {
        return hospitalRepository.findById(id);
    }

    public List<Hospital> findByTenantId(Long tenantId) {
        return hospitalRepository.findByTenantIdAndDeletedAtIsNull(tenantId);
    }

    public Optional<Hospital> findByNameAndTenantId(String name, Long tenantId) {
        return hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull(name, tenantId);
    }

    public boolean existsByNameAndTenantId(String name, Long tenantId) {
        return hospitalRepository.existsByNameAndTenantIdAndDeletedAtIsNull(name, tenantId);
    }

    public Hospital save(Hospital hospital) {
        if (hospital.getId() == null) {
            hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull(hospital.getName(), hospital.getTenant().getId())
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException("Hospital with name already exists in this tenant: " + hospital.getName());
                    });
        } else {
            hospitalRepository.findById(hospital.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospital.getId()));

            hospitalRepository.findByNameAndTenantIdAndDeletedAtIsNull(hospital.getName(), hospital.getTenant().getId())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(hospital.getId())) {
                            throw new DuplicateResourceException("Hospital with name already exists in this tenant: " + hospital.getName());
                        }
                    });
        }

        return hospitalRepository.save(hospital);
    }

    public void softDelete(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));
        hospital.setDeletedAt(LocalDateTime.now());
        hospitalRepository.save(hospital);
    }

    public Hospital update(Long id, HospitalUpdateRequest request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));

        if (request.getName() != null) hospital.setName(request.getName());
        if (request.getStatus() != null) hospital.setStatus(request.getStatus());
        if (request.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(request.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId()));
            hospital.setTenant(tenant);
        }

        return hospitalRepository.save(hospital);
    }
}
