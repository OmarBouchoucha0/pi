package tn.esprit.pi.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.user.TenantUpdateRequest;
import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.enums.user.TenantStatus;
import tn.esprit.pi.exception.DuplicateResourceException;
import tn.esprit.pi.exception.ResourceNotFoundException;
import tn.esprit.pi.repository.user.TenantRepository;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public List<Tenant> findAll() {
        return tenantRepository.findAllByDeletedAtIsNull();
    }

    public Optional<Tenant> findById(Long id) {
        return tenantRepository.findById(id);
    }

    public Optional<Tenant> findByName(String name) {
        return tenantRepository.findByNameAndDeletedAtIsNull(name);
    }

    public List<Tenant> findByStatus(TenantStatus status) {
        return tenantRepository.findByStatusAndDeletedAtIsNull(status);
    }

    public boolean existsByName(String name) {
        return tenantRepository.existsByNameAndDeletedAtIsNull(name);
    }

    public Tenant save(Tenant tenant) {
        if (tenant.getId() == null) {
            tenantRepository.findByNameAndDeletedAtIsNull(tenant.getName())
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException("Tenant with name already exists: " + tenant.getName());
                    });
        } else {
            tenantRepository.findById(tenant.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenant.getId()));

            tenantRepository.findByNameAndDeletedAtIsNull(tenant.getName())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(tenant.getId())) {
                            throw new DuplicateResourceException("Tenant with name already exists: " + tenant.getName());
                        }
                    });
        }

        return tenantRepository.save(tenant);
    }

    public void softDelete(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        tenant.setDeletedAt(LocalDateTime.now());
        tenantRepository.save(tenant);
    }

    public Tenant update(Long id, TenantUpdateRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));

        if (request.getName() != null) tenant.setName(request.getName());
        if (request.getStatus() != null) tenant.setStatus(request.getStatus());

        return tenantRepository.save(tenant);
    }
}
