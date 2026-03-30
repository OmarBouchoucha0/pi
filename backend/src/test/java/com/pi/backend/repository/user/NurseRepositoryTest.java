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
import com.pi.backend.model.user.Nurse;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.NurseShift;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.TenantRepository;

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

    @Test
    void saveAndRetrieveNurse() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "nurse@test.com");
        Department dept = createDepartment(tenant, "ICU");

        Nurse nurse = createNurse(user, dept, NurseShift.DAY);
        Nurse saved = nurseRepository.save(nurse);

        assertNotNull(saved.getId());
        assertEquals(NurseShift.DAY, saved.getShift());
        assertEquals(user.getId(), saved.getUser().getId());
        assertEquals(dept.getId(), saved.getDepartment().getId());
    }

    @Test
    void findByUserId() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "nurse@test.com");
        Department dept = createDepartment(tenant, "ICU");

        nurseRepository.save(createNurse(user, dept, NurseShift.DAY));

        Nurse found = nurseRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals(NurseShift.DAY, found.getShift());
    }

    @Test
    void findByDepartmentId() {
        Tenant tenant = createTenant();
        Department icu = createDepartment(tenant, "ICU");
        Department er = createDepartment(tenant, "ER");

        User u1 = createUser(tenant, "nurse1@test.com");
        User u2 = createUser(tenant, "nurse2@test.com");

        nurseRepository.save(createNurse(u1, icu, NurseShift.DAY));
        nurseRepository.save(createNurse(u2, er, NurseShift.NIGHT));

        List<Nurse> icuNurses = nurseRepository.findByDepartmentId(icu.getId());
        assertEquals(1, icuNurses.size());
    }

    @Test
    void findByShift() {
        Tenant tenant = createTenant();
        Department dept = createDepartment(tenant, "ICU");

        User u1 = createUser(tenant, "nurse1@test.com");
        User u2 = createUser(tenant, "nurse2@test.com");

        nurseRepository.save(createNurse(u1, dept, NurseShift.DAY));
        nurseRepository.save(createNurse(u2, dept, NurseShift.NIGHT));

        List<Nurse> dayNurses = nurseRepository.findByShift(NurseShift.DAY);
        assertEquals(1, dayNurses.size());
    }

    @Test
    void findByDepartmentIdAndShift() {
        Tenant tenant = createTenant();
        Department icu = createDepartment(tenant, "ICU");
        Department er = createDepartment(tenant, "ER");

        User u1 = createUser(tenant, "nurse1@test.com");
        User u2 = createUser(tenant, "nurse2@test.com");
        User u3 = createUser(tenant, "nurse3@test.com");

        nurseRepository.save(createNurse(u1, icu, NurseShift.DAY));
        nurseRepository.save(createNurse(u2, icu, NurseShift.NIGHT));
        nurseRepository.save(createNurse(u3, er, NurseShift.DAY));

        List<Nurse> icuDay = nurseRepository.findByDepartmentIdAndShift(icu.getId(), NurseShift.DAY);
        assertEquals(1, icuDay.size());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "nurse@test.com");
        Department dept = createDepartment(tenant, "ICU");

        Nurse saved = nurseRepository.save(createNurse(user, dept, NurseShift.DAY));
        nurseRepository.deleteById(saved.getId());

        List<Nurse> all = nurseRepository.findAll();
        assertTrue(all.isEmpty());
    }

    private Tenant createTenant() {
        Tenant tenant = new Tenant();
        tenant.setName("Nurse Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }

    private User createUser(Tenant tenant, String email) {
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setFirstName("Nurse");
        user.setLastName("Test");
        user.setRole(UserRole.NURSE);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Department createDepartment(Tenant tenant, String name) {
        Department dept = new Department();
        dept.setTenant(tenant);
        dept.setName(name);
        return departmentRepository.save(dept);
    }

    private Nurse createNurse(User user, Department dept, NurseShift shift) {
        Nurse nurse = new Nurse();
        nurse.setUser(user);
        nurse.setDepartment(dept);
        nurse.setShift(shift);
        return nurse;
    }
}
