package com.pi.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing tenant records.
 */
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    /**
     * Creates a new tenant.
     *
     * @param name   the tenant name
     * @param status the initial status
     * @return the created Tenant
     * @throws DuplicateResourceException if a tenant with the name already exists
     */
    @Transactional
    public Tenant createTenant(String name, TenantStatus status) {
        Optional<Tenant> existing = tenantRepository.findByNameAndDeletedAtIsNull(name);
        if (existing.isPresent()) {
            throw new DuplicateResourceException("Tenant", "name", name);
        }

        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setStatus(status);

        return tenantRepository.save(tenant);
    }

    /**
     * Retrieves a tenant by ID.
     *
     * @param id the tenant ID
     * @return the Tenant
     * @throws ResourceNotFoundException if no tenant with the given ID exists
     */
    @Transactional(readOnly = true)
    public Tenant getTenantById(Long id) {
        return tenantRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
    }
}
