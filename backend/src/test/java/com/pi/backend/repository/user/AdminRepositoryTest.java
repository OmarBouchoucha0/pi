package com.pi.backend.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.user.Admin;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.AdminPrivilege;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;

@SpringBootTest
@Transactional
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void saveAndRetrieveAdmin() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "admin@test.com");

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        Admin saved = adminRepository.save(admin);

        assertNotNull(saved.getId());
        assertEquals(AdminPrivilege.SUPER_ADMIN, saved.getPrivilegeLevel());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    @Test
    void findByUserId() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "admin@test.com");

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setPrivilegeLevel(AdminPrivilege.TENANT_ADMIN);
        adminRepository.save(admin);

        Admin found = adminRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals(AdminPrivilege.TENANT_ADMIN, found.getPrivilegeLevel());
    }

    @Test
    void findByPrivilegeLevel() {
        Tenant tenant = createTenant();
        User u1 = createUser(tenant, "super@test.com");
        User u2 = createUser(tenant, "tenant@test.com");

        Admin a1 = new Admin();
        a1.setUser(u1);
        a1.setPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        adminRepository.save(a1);

        Admin a2 = new Admin();
        a2.setUser(u2);
        a2.setPrivilegeLevel(AdminPrivilege.TENANT_ADMIN);
        adminRepository.save(a2);

        List<Admin> superAdmins = adminRepository.findByPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        assertEquals(1, superAdmins.size());
        assertEquals(u1.getId(), superAdmins.get(0).getUser().getId());
    }

    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "admin@test.com");

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        Admin saved = adminRepository.save(admin);

        adminRepository.deleteById(saved.getId());

        List<Admin> all = adminRepository.findAll();
        assertTrue(all.isEmpty());
    }

    private Tenant createTenant() {
        Tenant tenant = new Tenant();
        tenant.setName("Admin Hospital");
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }

    private User createUser(Tenant tenant, String email) {
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setFirstName("Admin");
        user.setLastName("Test");
        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }
}
