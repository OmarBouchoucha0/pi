package com.pi.backend.repository.user;

import static com.pi.backend.repository.user.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Department;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.user.Doctor;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
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
        Tenant tenant = createTenant(tenantRepository, "Doctor Hospital");
        User user = createUser(userRepository, tenant, "doctor@test.com", UserRole.DOCTOR);
        Department dept = createDepartment(departmentRepository, tenant, "Cardiology");

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(dept);
        doctor.setLicenseNumber("LIC-001");
        doctor.setSpecialty("Cardiology");
        doctor.setYearsOfExperience(5);
        Doctor saved = doctorRepository.save(doctor);

        assertNotNull(saved.getId());
        assertEquals("LIC-001", saved.getLicenseNumber());
        assertEquals("Cardiology", saved.getSpecialty());
        assertEquals(user.getId(), saved.getUser().getId());
        assertEquals(dept.getId(), saved.getDepartment().getId());
    }

    @Test
    void findByUserId() {
        Tenant tenant = createTenant(tenantRepository, "Doctor Hospital");
        User user = createUser(userRepository, tenant, "doctor@test.com", UserRole.DOCTOR);
        Department dept = createDepartment(departmentRepository, tenant, "Cardiology");

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(dept);
        doctor.setLicenseNumber("LIC-001");
        doctor.setSpecialty("Cardiology");
        doctorRepository.save(doctor);

        Doctor found = doctorRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals("LIC-001", found.getLicenseNumber());
    }

    @Test
    void findByLicenseNumber() {
        Tenant tenant = createTenant(tenantRepository, "Doctor Hospital");
        User user = createUser(userRepository, tenant, "doctor@test.com", UserRole.DOCTOR);
        Department dept = createDepartment(departmentRepository, tenant, "Cardiology");

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(dept);
        doctor.setLicenseNumber("LIC-001");
        doctor.setSpecialty("Cardiology");
        doctorRepository.save(doctor);

        Doctor found = doctorRepository.findByLicenseNumber("LIC-001").orElseThrow();
        assertEquals(user.getId(), found.getUser().getId());
    }

    @Test
    void existsByLicenseNumber() {
        Tenant tenant = createTenant(tenantRepository, "Doctor Hospital");
        User user = createUser(userRepository, tenant, "doctor@test.com", UserRole.DOCTOR);
        Department dept = createDepartment(departmentRepository, tenant, "Cardiology");

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(dept);
        doctor.setLicenseNumber("LIC-001");
        doctor.setSpecialty("Cardiology");
        doctorRepository.save(doctor);

        assertTrue(doctorRepository.existsByLicenseNumber("LIC-001"));
        assertFalse(doctorRepository.existsByLicenseNumber("LIC-999"));
    }

    @Test
    void uniqueLicenseNumber() {
        Tenant tenant = createTenant(tenantRepository, "Doctor Hospital");
        User u1 = createUser(userRepository, tenant, "doctor1@test.com", UserRole.DOCTOR);
        User u2 = createUser(userRepository, tenant, "doctor2@test.com", UserRole.DOCTOR);
        Department dept = createDepartment(departmentRepository, tenant, "Cardiology");

        Doctor d1 = new Doctor();
        d1.setUser(u1);
        d1.setDepartment(dept);
        d1.setLicenseNumber("LIC-001");
        d1.setSpecialty("Cardiology");
        doctorRepository.save(d1);

        Doctor d2 = new Doctor();
        d2.setUser(u2);
        d2.setDepartment(dept);
        d2.setLicenseNumber("LIC-001");
        d2.setSpecialty("Neurology");

        assertThrows(DataIntegrityViolationException.class, () -> {
            doctorRepository.saveAndFlush(d2);
        });
    }

    @Test
    void findByDepartmentId() {
        Tenant tenant = createTenant(tenantRepository, "Doctor Hospital");
        Department cardiology = createDepartment(departmentRepository, tenant, "Cardiology");
        Department neurology = createDepartment(departmentRepository, tenant, "Neurology");

        User u1 = createUser(userRepository, tenant, "doctor1@test.com", UserRole.DOCTOR);
        User u2 = createUser(userRepository, tenant, "doctor2@test.com", UserRole.DOCTOR);

        Doctor d1 = new Doctor();
        d1.setUser(u1);
        d1.setDepartment(cardiology);
        d1.setLicenseNumber("LIC-001");
        d1.setSpecialty("Cardiology");
        doctorRepository.save(d1);

        Doctor d2 = new Doctor();
        d2.setUser(u2);
        d2.setDepartment(neurology);
        d2.setLicenseNumber("LIC-002");
        d2.setSpecialty("Neurology");
        doctorRepository.save(d2);

        List<Doctor> cardiologists = doctorRepository.findByDepartmentId(cardiology.getId());
        assertEquals(1, cardiologists.size());
        assertEquals("LIC-001", cardiologists.get(0).getLicenseNumber());
    }

    @Test
    void findBySpecialty() {
        Tenant tenant = createTenant(tenantRepository, "Doctor Hospital");
        Department dept = createDepartment(departmentRepository, tenant, "Medicine");

        User u1 = createUser(userRepository, tenant, "doctor1@test.com", UserRole.DOCTOR);
        User u2 = createUser(userRepository, tenant, "doctor2@test.com", UserRole.DOCTOR);

        Doctor d1 = new Doctor();
        d1.setUser(u1);
        d1.setDepartment(dept);
        d1.setLicenseNumber("LIC-001");
        d1.setSpecialty("Cardiology");
        doctorRepository.save(d1);

        Doctor d2 = new Doctor();
        d2.setUser(u2);
        d2.setDepartment(dept);
        d2.setLicenseNumber("LIC-002");
        d2.setSpecialty("Neurology");
        doctorRepository.save(d2);

        List<Doctor> cardiologists = doctorRepository.findBySpecialty("Cardiology");
        assertEquals(1, cardiologists.size());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant(tenantRepository, "Doctor Hospital");
        User user = createUser(userRepository, tenant, "doctor@test.com", UserRole.DOCTOR);
        Department dept = createDepartment(departmentRepository, tenant, "Cardiology");

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(dept);
        doctor.setLicenseNumber("LIC-001");
        doctor.setSpecialty("Cardiology");
        Doctor saved = doctorRepository.save(doctor);

        doctorRepository.deleteById(saved.getId());

        List<Doctor> all = doctorRepository.findAll();
        assertTrue(all.isEmpty());
    }
}
