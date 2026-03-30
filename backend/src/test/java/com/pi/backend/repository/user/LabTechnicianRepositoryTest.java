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
import com.pi.backend.model.user.LabTechnician;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
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
        Tenant tenant = createTenant(tenantRepository, "Lab Hospital");
        User user = createUser(userRepository, tenant, "lab@test.com", UserRole.LAB_TECHNICIAN);
        Department dept = createDepartment(departmentRepository, tenant, "Pathology");

        LabTechnician tech = new LabTechnician();
        tech.setUser(user);
        tech.setDepartment(dept);
        tech.setCertification("MLT Certified");
        LabTechnician saved = labTechnicianRepository.save(tech);

        assertNotNull(saved.getId());
        assertEquals("MLT Certified", saved.getCertification());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    @Test
    void findByUserId() {
        Tenant tenant = createTenant(tenantRepository, "Lab Hospital");
        User user = createUser(userRepository, tenant, "lab@test.com", UserRole.LAB_TECHNICIAN);
        Department dept = createDepartment(departmentRepository, tenant, "Pathology");

        LabTechnician tech = new LabTechnician();
        tech.setUser(user);
        tech.setDepartment(dept);
        tech.setCertification("MLT Certified");
        labTechnicianRepository.save(tech);

        LabTechnician found = labTechnicianRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals("MLT Certified", found.getCertification());
    }

    @Test
    void findByDepartmentId() {
        Tenant tenant = createTenant(tenantRepository, "Lab Hospital");
        Department pathology = createDepartment(departmentRepository, tenant, "Pathology");
        Department radiology = createDepartment(departmentRepository, tenant, "Radiology");

        User u1 = createUser(userRepository, tenant, "lab1@test.com", UserRole.LAB_TECHNICIAN);
        User u2 = createUser(userRepository, tenant, "lab2@test.com", UserRole.LAB_TECHNICIAN);

        LabTechnician t1 = new LabTechnician();
        t1.setUser(u1);
        t1.setDepartment(pathology);
        t1.setCertification("MLT Certified");
        labTechnicianRepository.save(t1);

        LabTechnician t2 = new LabTechnician();
        t2.setUser(u2);
        t2.setDepartment(radiology);
        t2.setCertification("RT Certified");
        labTechnicianRepository.save(t2);

        List<LabTechnician> pathTechs = labTechnicianRepository.findByDepartmentId(pathology.getId());
        assertEquals(1, pathTechs.size());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant(tenantRepository, "Lab Hospital");
        User user = createUser(userRepository, tenant, "lab@test.com", UserRole.LAB_TECHNICIAN);
        Department dept = createDepartment(departmentRepository, tenant, "Pathology");

        LabTechnician tech = new LabTechnician();
        tech.setUser(user);
        tech.setDepartment(dept);
        tech.setCertification("MLT Certified");
        LabTechnician saved = labTechnicianRepository.save(tech);

        labTechnicianRepository.deleteById(saved.getId());

        List<LabTechnician> all = labTechnicianRepository.findAll();
        assertTrue(all.isEmpty());
    }
}
