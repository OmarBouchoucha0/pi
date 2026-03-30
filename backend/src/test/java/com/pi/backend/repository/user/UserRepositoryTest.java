package com.pi.backend.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;

/**
 * Integration tests for {@link UserRepository}. Verifies database operations
 * including CRUD, unique constraints on tenant+email, enum persistence, and soft delete filtering.
 */
@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * Verifies that a user can be saved and retrieved from the database.
     */
    @Test
    void saveAndRetrieveUser() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "test@test.com", UserRole.DOCTOR);

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("test@test.com", saved.getEmail());
        assertEquals(UserRole.DOCTOR, saved.getRole());
        assertEquals(UserStatus.ACTIVE, saved.getStatus());
        assertEquals(0, saved.getFailedAttempts());
        assertNotNull(saved.getCreatedAt());
    }

    /**
     * Verifies that a unique constraint on tenant and email prevents duplicate emails within a tenant.
     */
    @Test
    void uniqueConstraintOnTenantAndEmail() {
        Tenant tenant = createTenant();

        User u1 = createUser(tenant, "test@test.com", UserRole.DOCTOR);
        userRepository.save(u1);

        User u2 = createUser(tenant, "test@test.com", UserRole.PATIENT);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(u2);
        });
    }

    /**
     * Verifies that the same email is allowed across different tenants.
     */
    @Test
    void sameEmailDifferentTenantsAllowed() {
        Tenant t1 = createTenant("Hospital A");
        Tenant t2 = createTenant("Hospital B");

        User u1 = createUser(t1, "test@test.com", UserRole.DOCTOR);
        userRepository.save(u1);

        User u2 = createUser(t2, "test@test.com", UserRole.DOCTOR);
        User saved = userRepository.save(u2);

        assertNotNull(saved.getId());
    }

    /**
     * Verifies that UserRole and UserStatus enums are persisted and retrieved correctly.
     */
    @Test
    void enumPersistence() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "test@test.com", UserRole.LAB_TECHNICIAN);
        userRepository.save(user);

        User found = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(UserRole.LAB_TECHNICIAN, found.getRole());
        assertEquals(UserStatus.ACTIVE, found.getStatus());
    }

    /**
     * Verifies that soft-deleted users are excluded from findAll results.
     */
    @Test
    void softDeleteFiltersFromFindAll() {
        Tenant tenant = createTenant();
        User user = createUser(tenant, "test@test.com", UserRole.PATIENT);
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        List<User> all = userRepository.findAll();
        assertTrue(all.isEmpty());
    }

    private Tenant createTenant() {
        return createTenant("Hospital A");
    }

    private Tenant createTenant(String name) {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setStatus(TenantStatus.ACTIVE);
        return tenantRepository.save(tenant);
    }

    private User createUser(Tenant tenant, String email, UserRole role) {
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPasswordHash("hashed_password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        return user;
    }
}
