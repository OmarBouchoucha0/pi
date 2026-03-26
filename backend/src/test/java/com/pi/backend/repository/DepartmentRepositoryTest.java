package com.pi.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Department;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;

@SpringBootTest
@Transactional
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void saveAndRetrieveDepartment() {
        Tenant tenant = createTenant();
        Department dept = new Department();
        dept.setTenant(tenant);
        dept.setName("Cardiology");
        dept.setDescription("Heart and vascular care");

        Department saved = departmentRepository.save(dept);

        assertNotNull(saved.getId());
        assertEquals("Cardiology", saved.getName());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void uniqueConstraintOnTenantAndName() {
        Tenant tenant = createTenant();

        Department d1 = new Department();
        d1.setTenant(tenant);
        d1.setName("Cardiology");
        departmentRepository.save(d1);

        Department d2 = new Department();
        d2.setTenant(tenant);
        d2.setName("Cardiology");

        assertThrows(DataIntegrityViolationException.class, () -> {
            departmentRepository.saveAndFlush(d2);
        });
    }

    @Test
    void sameNameDifferentTenantsAllowed() {
        Tenant t1 = createTenant("Hospital A");
        Tenant t2 = createTenant("Hospital B");

        Department d1 = new Department();
        d1.setTenant(t1);
        d1.setName("Cardiology");
        departmentRepository.save(d1);

        Department d2 = new Department();
        d2.setTenant(t2);
        d2.setName("Cardiology");
        Department saved = departmentRepository.save(d2);

        assertNotNull(saved.getId());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant();
        Department dept = new Department();
        dept.setTenant(tenant);
        dept.setName("Cardiology");
        Department saved = departmentRepository.save(dept);

        departmentRepository.deleteById(saved.getId());

        List<Department> all = departmentRepository.findAll();
        assertTrue(all.isEmpty());
    }

    private Tenant createTenant() {
        return createTenant("Hospital A");
    }

    private Tenant createTenant(String name) {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }
}
