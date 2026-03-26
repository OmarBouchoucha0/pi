package com.pi.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TenantRepositoryTest {

    @Autowired
    private TenantRepository tenantRepository;

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
