package com.pi.backend.repository.user;

import static com.pi.backend.repository.user.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Department;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.user.Secretary;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
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
        Tenant tenant = createTenant(tenantRepository, "Secretary Hospital");
        User user = createUser(userRepository, tenant, "secretary@test.com", UserRole.SECRETARY);
        Department dept = createDepartment(departmentRepository, tenant, "Front Desk");

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
        Tenant tenant = createTenant(tenantRepository, "Secretary Hospital");
        User user = createUser(userRepository, tenant, "secretary@test.com", UserRole.SECRETARY);
        Department dept = createDepartment(departmentRepository, tenant, "Front Desk");

        Secretary secretary = new Secretary();
        secretary.setUser(user);
        secretary.setDepartment(dept);
        secretaryRepository.save(secretary);

        Secretary found = secretaryRepository.findByUserId(user.getId()).orElseThrow();
        assertNotNull(found.getId());
    }

    @Test
    void findByDepartmentId() {
        Tenant tenant = createTenant(tenantRepository, "Secretary Hospital");
        Department frontDesk = createDepartment(departmentRepository, tenant, "Front Desk");
        Department billing = createDepartment(departmentRepository, tenant, "Billing");

        User u1 = createUser(userRepository, tenant, "sec1@test.com", UserRole.SECRETARY);
        User u2 = createUser(userRepository, tenant, "sec2@test.com", UserRole.SECRETARY);

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
        Tenant tenant = createTenant(tenantRepository, "Secretary Hospital");
        User user = createUser(userRepository, tenant, "secretary@test.com", UserRole.SECRETARY);
        Department dept = createDepartment(departmentRepository, tenant, "Front Desk");

        Secretary secretary = new Secretary();
        secretary.setUser(user);
        secretary.setDepartment(dept);
        Secretary saved = secretaryRepository.save(secretary);

        secretaryRepository.deleteById(saved.getId());

        List<Secretary> all = secretaryRepository.findAll();
        assertTrue(all.isEmpty());
    }
}
