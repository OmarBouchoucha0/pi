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
import com.pi.backend.model.user.LabTechnician;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.TenantRepository;

@SpringBootTest
@Transactional
class LabTechnicianRepositoryTest {

    @Autowired
    private LabTechnicianRepository labTechnicianRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void saveAndRetrieveLabTechnician() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "lab@test.com");
        Department dept = createDepartment(tenant, "Pathology");

        LabTechnician tech = createLabTechnician(user, dept, "MLT Certified");
        LabTechnician saved = labTechnicianRepository.save(tech);

        assertNotNull(saved.getId());
        assertEquals("MLT Certified", saved.getCertification());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    @Test
    void findByUserId() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "lab@test.com");
        Department dept = createDepartment(tenant, "Pathology");

        labTechnicianRepository.save(createLabTechnician(user, dept, "MLT Certified"));

        LabTechnician found = labTechnicianRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals("MLT Certified", found.getCertification());
    }

    @Test
    void findByDepartmentId() {
        Tenant tenant = createTenant();
        Department pathology = createDepartment(tenant, "Pathology");
        Department radiology = createDepartment(tenant, "Radiology");

        User u1 = createUser(tenant, "lab1@test.com");
        User u2 = createUser(tenant, "lab2@test.com");

        labTechnicianRepository.save(createLabTechnician(u1, pathology, "MLT Certified"));
        labTechnicianRepository.save(createLabTechnician(u2, radiology, "RT Certified"));

        List<LabTechnician> pathTechs = labTechnicianRepository.findByDepartmentId(pathology.getId());
        assertEquals(1, pathTechs.size());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "lab@test.com");
        Department dept = createDepartment(tenant, "Pathology");

        LabTechnician saved = labTechnicianRepository.save(createLabTechnician(user, dept, "MLT Certified"));
        labTechnicianRepository.deleteById(saved.getId());

        List<LabTechnician> all = labTechnicianRepository.findAll();
        assertTrue(all.isEmpty());
    }

    private Tenant createTenant() {
        Tenant tenant = new Tenant();
        tenant.setName("Lab Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }

    private User createUser(Tenant tenant, String email) {
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setFirstName("Lab");
        user.setLastName("Test");
        user.setRole(UserRole.LAB_TECHNICIAN);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Department createDepartment(Tenant tenant, String name) {
        Department dept = new Department();
        dept.setTenant(tenant);
        dept.setName(name);
        return departmentRepository.save(dept);
    }

    private LabTechnician createLabTechnician(User user, Department dept, String certification) {
        LabTechnician tech = new LabTechnician();
        tech.setUser(user);
        tech.setDepartment(dept);
        tech.setCertification(certification);
        return tech;
    }
}
