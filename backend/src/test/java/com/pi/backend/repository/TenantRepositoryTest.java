package com.pi.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;

/**
 * Integration tests for {@link TenantRepository}. Verifies database operations
 * including CRUD, soft delete filtering, and unique constraints.
 */
@SpringBootTest
@Transactional
class TenantRepositoryTest {

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * Verifies that a tenant can be saved and retrieved from the database.
     */
    @Test
    void saveAndRetrieveTenant() {
        Tenant tenant = new Tenant();
        tenant.setName("Hospital A");
        tenant.setStatus(TenantStatus.ACTIVE);

        Tenant saved = tenantRepository.save(tenant);

        assertNotNull(saved.getId());
        assertEquals("Hospital A", saved.getName());
        assertEquals(TenantStatus.ACTIVE, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    /**
     * Verifies that all tenants including active and inactive are returned.
     */
    @Test
    void findAllReturnsActiveTenants() {
        Tenant t1 = new Tenant();
        t1.setName("Hospital A");
        t1.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(t1);

        Tenant t2 = new Tenant();
        t2.setName("Hospital B");
        t2.setStatus(TenantStatus.INACTIVE);
        tenantRepository.save(t2);

        List<Tenant> all = tenantRepository.findAll();
        assertEquals(2, all.size());
    }

    /**
     * Verifies that soft-deleted tenants are excluded from findAll and findById results.
     */
    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = new Tenant();
        tenant.setName("Hospital A");
        tenant.setStatus(TenantStatus.ACTIVE);
        Tenant saved = tenantRepository.save(tenant);

        tenantRepository.deleteById(saved.getId());

        List<Tenant> all = tenantRepository.findAll();
        assertTrue(all.isEmpty());

        Optional<Tenant> raw = tenantRepository.findById(saved.getId());
        assertTrue(raw.isEmpty());
    }

    /**
     * Verifies that findById returns empty for a soft-deleted tenant.
     */
    @Test
    void findByIdReturnsEmptyForDeleted() {
        Tenant tenant = new Tenant();
        tenant.setName("Hospital A");
        tenant.setStatus(TenantStatus.ACTIVE);
        Tenant saved = tenantRepository.save(tenant);

        tenantRepository.deleteById(saved.getId());

        Optional<Tenant> found = tenantRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }
}
