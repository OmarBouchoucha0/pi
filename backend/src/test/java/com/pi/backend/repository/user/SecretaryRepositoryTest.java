package com.pi.backend.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Department;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.user.Secretary;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.TenantRepository;

@SpringBootTest
@Transactional
class SecretaryRepositoryTest {

    @Autowired
    private SecretaryRepository secretaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void saveAndRetrieveSecretary() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "secretary@test.com");
        Department dept = createDepartment(tenant, "Front Desk");

        Secretary secretary = new Secretary();
        secretary.setUser(user);
        secretary.setDepartment(dept);
        Secretary saved = secretaryRepository.save(secretary);

        assertNotNull(saved.getId());
        assertEquals(user.getId(), saved.getUser().getId());
        assertEquals(dept.getId(), saved.getDepartment().getId());
    }

    @Test
    void findByUserId() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "secretary@test.com");
        Department dept = createDepartment(tenant, "Front Desk");

        Secretary secretary = new Secretary();
        secretary.setUser(user);
        secretary.setDepartment(dept);
        secretaryRepository.save(secretary);

        Secretary found = secretaryRepository.findByUserId(user.getId()).orElseThrow();
        assertNotNull(found.getId());
    }

    @Test
    void findByDepartmentId() {
        Tenant tenant = createTenant();
        Department frontDesk = createDepartment(tenant, "Front Desk");
        Department billing = createDepartment(tenant, "Billing");

        User u1 = createUser(tenant, "sec1@test.com");
        User u2 = createUser(tenant, "sec2@test.com");

        Secretary s1 = new Secretary();
        s1.setUser(u1);
        s1.setDepartment(frontDesk);
        secretaryRepository.save(s1);

        Secretary s2 = new Secretary();
        s2.setUser(u2);
        s2.setDepartment(billing);
        secretaryRepository.save(s2);

        List<Secretary> frontDeskSecs = secretaryRepository.findByDepartmentId(frontDesk.getId());
        assertEquals(1, frontDeskSecs.size());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "secretary@test.com");
        Department dept = createDepartment(tenant, "Front Desk");

        Secretary secretary = new Secretary();
        secretary.setUser(user);
        secretary.setDepartment(dept);
        Secretary saved = secretaryRepository.save(secretary);

        secretaryRepository.deleteById(saved.getId());

        List<Secretary> all = secretaryRepository.findAll();
        assertTrue(all.isEmpty());
    }

    private Tenant createTenant() {
        Tenant tenant = new Tenant();
        tenant.setName("Secretary Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }

    private User createUser(Tenant tenant, String email) {
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setFirstName("Secretary");
        user.setLastName("Test");
        user.setRole(UserRole.SECRETARY);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Department createDepartment(Tenant tenant, String name) {
        Department dept = new Department();
        dept.setTenant(tenant);
        dept.setName(name);
        return departmentRepository.save(dept);
    }
}
