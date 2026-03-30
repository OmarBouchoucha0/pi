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
import com.pi.backend.model.user.Nurse;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.NurseShift;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.TenantRepository;

/**
 * Integration tests for {@link NurseRepository}. Verifies database operations
 * including CRUD, lookup by department/shift, and soft delete filtering.
 */
@SpringBootTest
@Transactional
class NurseRepositoryTest {

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * Verifies that a nurse can be saved and retrieved from the database.
     */
    @Test
    void saveAndRetrieveNurse() {
        Tenant tenant = createTenant(tenantRepository, "Nurse Hospital");
        User user = createUser(userRepository, tenant, "nurse@test.com", UserRole.NURSE);
        Department dept = createDepartment(departmentRepository, tenant, "ICU");

        Nurse nurse = new Nurse();
        nurse.setUser(user);
        nurse.setDepartment(dept);
        nurse.setShift(NurseShift.DAY);
        Nurse saved = nurseRepository.save(nurse);

        assertNotNull(saved.getId());
        assertEquals(NurseShift.DAY, saved.getShift());
        assertEquals(user.getId(), saved.getUser().getId());
        assertEquals(dept.getId(), saved.getDepartment().getId());
    }

    /**
     * Verifies that a nurse can be looked up by their associated user ID.
     */
    @Test
    void findByUserId() {
        Tenant tenant = createTenant(tenantRepository, "Nurse Hospital");
        User user = createUser(userRepository, tenant, "nurse@test.com", UserRole.NURSE);
        Department dept = createDepartment(departmentRepository, tenant, "ICU");

        Nurse nurse = new Nurse();
        nurse.setUser(user);
        nurse.setDepartment(dept);
        nurse.setShift(NurseShift.DAY);
        nurseRepository.save(nurse);

        Nurse found = nurseRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals(NurseShift.DAY, found.getShift());
    }

    /**
     * Verifies that nurses can be retrieved filtered by their department ID.
     */
    @Test
    void findByDepartmentId() {
        Tenant tenant = createTenant(tenantRepository, "Nurse Hospital");
        Department icu = createDepartment(departmentRepository, tenant, "ICU");
        Department er = createDepartment(departmentRepository, tenant, "ER");

        User u1 = createUser(userRepository, tenant, "nurse1@test.com", UserRole.NURSE);
        User u2 = createUser(userRepository, tenant, "nurse2@test.com", UserRole.NURSE);

        Nurse n1 = new Nurse();
        n1.setUser(u1);
        n1.setDepartment(icu);
        n1.setShift(NurseShift.DAY);
        nurseRepository.save(n1);

        Nurse n2 = new Nurse();
        n2.setUser(u2);
        n2.setDepartment(er);
        n2.setShift(NurseShift.NIGHT);
        nurseRepository.save(n2);

        List<Nurse> icuNurses = nurseRepository.findByDepartmentId(icu.getId());
        assertEquals(1, icuNurses.size());
    }

    /**
     * Verifies that nurses can be retrieved filtered by their shift type.
     */
    @Test
    void findByShift() {
        Tenant tenant = createTenant(tenantRepository, "Nurse Hospital");
        Department dept = createDepartment(departmentRepository, tenant, "ICU");

        User u1 = createUser(userRepository, tenant, "nurse1@test.com", UserRole.NURSE);
        User u2 = createUser(userRepository, tenant, "nurse2@test.com", UserRole.NURSE);

        Nurse n1 = new Nurse();
        n1.setUser(u1);
        n1.setDepartment(dept);
        n1.setShift(NurseShift.DAY);
        nurseRepository.save(n1);

        Nurse n2 = new Nurse();
        n2.setUser(u2);
        n2.setDepartment(dept);
        n2.setShift(NurseShift.NIGHT);
        nurseRepository.save(n2);

        List<Nurse> dayNurses = nurseRepository.findByShift(NurseShift.DAY);
        assertEquals(1, dayNurses.size());
    }

    /**
     * Verifies that nurses can be retrieved filtered by both department ID and shift type.
     */
    @Test
    void findByDepartmentIdAndShift() {
        Tenant tenant = createTenant(tenantRepository, "Nurse Hospital");
        Department icu = createDepartment(departmentRepository, tenant, "ICU");
        Department er = createDepartment(departmentRepository, tenant, "ER");

        User u1 = createUser(userRepository, tenant, "nurse1@test.com", UserRole.NURSE);
        User u2 = createUser(userRepository, tenant, "nurse2@test.com", UserRole.NURSE);
        User u3 = createUser(userRepository, tenant, "nurse3@test.com", UserRole.NURSE);

        Nurse n1 = new Nurse();
        n1.setUser(u1);
        n1.setDepartment(icu);
        n1.setShift(NurseShift.DAY);
        nurseRepository.save(n1);

        Nurse n2 = new Nurse();
        n2.setUser(u2);
        n2.setDepartment(icu);
        n2.setShift(NurseShift.NIGHT);
        nurseRepository.save(n2);

        Nurse n3 = new Nurse();
        n3.setUser(u3);
        n3.setDepartment(er);
        n3.setShift(NurseShift.DAY);
        nurseRepository.save(n3);

        List<Nurse> icuDay = nurseRepository.findByDepartmentIdAndShift(icu.getId(), NurseShift.DAY);
        assertEquals(1, icuDay.size());
    }

    /**
     * Verifies that soft-deleted nurses are excluded from findAll results.
     */
    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant(tenantRepository, "Nurse Hospital");
        User user = createUser(userRepository, tenant, "nurse@test.com", UserRole.NURSE);
        Department dept = createDepartment(departmentRepository, tenant, "ICU");

        Nurse nurse = new Nurse();
        nurse.setUser(user);
        nurse.setDepartment(dept);
        nurse.setShift(NurseShift.DAY);
        Nurse saved = nurseRepository.save(nurse);

        nurseRepository.deleteById(saved.getId());

        List<Nurse> all = nurseRepository.findAll();
        assertTrue(all.isEmpty());
    }
}
