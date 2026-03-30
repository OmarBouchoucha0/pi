package com.pi.backend.repository.user;

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
import com.pi.backend.model.user.Doctor;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.TenantRepository;

@SpringBootTest
@Transactional
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void saveAndRetrieveDoctor() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "doctor@test.com");
        Department dept = createDepartment(tenant, "Cardiology");

        Doctor doctor = createDoctor(user, dept, "LIC-001", "Cardiology");
        Doctor saved = doctorRepository.save(doctor);

        assertNotNull(saved.getId());
        assertEquals("LIC-001", saved.getLicenseNumber());
        assertEquals("Cardiology", saved.getSpecialty());
        assertEquals(user.getId(), saved.getUser().getId());
        assertEquals(dept.getId(), saved.getDepartment().getId());
    }

    @Test
    void findByUserId() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "doctor@test.com");
        Department dept = createDepartment(tenant, "Cardiology");

        Doctor doctor = createDoctor(user, dept, "LIC-001", "Cardiology");
        doctorRepository.save(doctor);

        Doctor found = doctorRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals("LIC-001", found.getLicenseNumber());
    }

    @Test
    void findByLicenseNumber() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "doctor@test.com");
        Department dept = createDepartment(tenant, "Cardiology");

        Doctor doctor = createDoctor(user, dept, "LIC-001", "Cardiology");
        doctorRepository.save(doctor);

        Doctor found = doctorRepository.findByLicenseNumber("LIC-001").orElseThrow();
        assertEquals(user.getId(), found.getUser().getId());
    }

    @Test
    void existsByLicenseNumber() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "doctor@test.com");
        Department dept = createDepartment(tenant, "Cardiology");

        Doctor doctor = createDoctor(user, dept, "LIC-001", "Cardiology");
        doctorRepository.save(doctor);

        assertTrue(doctorRepository.existsByLicenseNumber("LIC-001"));
        assertFalse(doctorRepository.existsByLicenseNumber("LIC-999"));
    }

    @Test
    void uniqueLicenseNumber() {
        Tenant tenant = createTenant();
        User u1 = createUser(tenant, "doctor1@test.com");
        User u2 = createUser(tenant, "doctor2@test.com");
        Department dept = createDepartment(tenant, "Cardiology");

        doctorRepository.save(createDoctor(u1, dept, "LIC-001", "Cardiology"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            doctorRepository.saveAndFlush(createDoctor(u2, dept, "LIC-001", "Neurology"));
        });
    }

    @Test
    void findByDepartmentId() {
        Tenant tenant = createTenant();
        Department cardiology = createDepartment(tenant, "Cardiology");
        Department neurology = createDepartment(tenant, "Neurology");

        User u1 = createUser(tenant, "doctor1@test.com");
        User u2 = createUser(tenant, "doctor2@test.com");

        doctorRepository.save(createDoctor(u1, cardiology, "LIC-001", "Cardiology"));
        doctorRepository.save(createDoctor(u2, neurology, "LIC-002", "Neurology"));

        List<Doctor> cardiologists = doctorRepository.findByDepartmentId(cardiology.getId());
        assertEquals(1, cardiologists.size());
        assertEquals("LIC-001", cardiologists.get(0).getLicenseNumber());
    }

    @Test
    void findBySpecialty() {
        Tenant tenant = createTenant();
        Department dept = createDepartment(tenant, "Medicine");

        User u1 = createUser(tenant, "doctor1@test.com");
        User u2 = createUser(tenant, "doctor2@test.com");

        doctorRepository.save(createDoctor(u1, dept, "LIC-001", "Cardiology"));
        doctorRepository.save(createDoctor(u2, dept, "LIC-002", "Neurology"));

        List<Doctor> cardiologists = doctorRepository.findBySpecialty("Cardiology");
        assertEquals(1, cardiologists.size());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "doctor@test.com");
        Department dept = createDepartment(tenant, "Cardiology");

        Doctor doctor = createDoctor(user, dept, "LIC-001", "Cardiology");
        Doctor saved = doctorRepository.save(doctor);

        doctorRepository.deleteById(saved.getId());

        List<Doctor> all = doctorRepository.findAll();
        assertTrue(all.isEmpty());
    }

    private Tenant createTenant() {
        Tenant tenant = new Tenant();
        tenant.setName("Doctor Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }

    private User createUser(Tenant tenant, String email) {
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setFirstName("Doctor");
        user.setLastName("Test");
        user.setRole(UserRole.DOCTOR);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Department createDepartment(Tenant tenant, String name) {
        Department dept = new Department();
        dept.setTenant(tenant);
        dept.setName(name);
        return departmentRepository.save(dept);
    }

    private Doctor createDoctor(User user, Department dept, String license, String specialty) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(dept);
        doctor.setLicenseNumber(license);
        doctor.setSpecialty(specialty);
        doctor.setYearsOfExperience(5);
        return doctor;
    }
}
