package com.pi.backend.repository.user;

import static com.pi.backend.repository.user.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.user.Admin;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.AdminPrivilege;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.repository.TenantRepository;

/**
 * Integration tests for {@link AdminRepository}. Verifies database operations
 * including CRUD, lookup by privilege level, and soft delete filtering.
 */
@SpringBootTest
@Transactional
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * Verifies that an admin can be saved and retrieved from the database.
     */
    @Test
    void saveAndRetrieveAdmin() {
        Tenant tenant = createTenant(tenantRepository, "Admin Hospital");
        User user = createUser(userRepository, tenant, "admin@test.com", UserRole.ADMIN);

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        Admin saved = adminRepository.save(admin);

        assertNotNull(saved.getId());
        assertEquals(AdminPrivilege.SUPER_ADMIN, saved.getPrivilegeLevel());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    /**
     * Verifies that an admin can be looked up by their associated user ID.
     */
    @Test
    void findByUserId() {
        Tenant tenant = createTenant(tenantRepository, "Admin Hospital");
        User user = createUser(userRepository, tenant, "admin@test.com", UserRole.ADMIN);

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setPrivilegeLevel(AdminPrivilege.TENANT_ADMIN);
        adminRepository.save(admin);

        Admin found = adminRepository.findByUserId(user.getId()).orElseThrow();
        assertEquals(AdminPrivilege.TENANT_ADMIN, found.getPrivilegeLevel());
    }

    /**
     * Verifies that admins can be retrieved filtered by their privilege level.
     */
    @Test
    void findByPrivilegeLevel() {
        Tenant tenant = createTenant(tenantRepository, "Admin Hospital");
        User u1 = createUser(userRepository, tenant, "super@test.com", UserRole.ADMIN);
        User u2 = createUser(userRepository, tenant, "tenant@test.com", UserRole.ADMIN);

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

    /**
     * Verifies that soft-deleted admins are excluded from findAll results.
     */
    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant(tenantRepository, "Admin Hospital");
        User user = createUser(userRepository, tenant, "admin@test.com", UserRole.ADMIN);

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        Admin saved = adminRepository.save(admin);

        adminRepository.deleteById(saved.getId());

        List<Admin> all = adminRepository.findAll();
        assertTrue(all.isEmpty());
    }
}
