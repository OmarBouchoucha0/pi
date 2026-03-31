package com.pi.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.Gender;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;
import com.pi.backend.repository.user.UserRepository;

/**
 * Unit tests for {@link UserService}. Uses Mockito to mock repositories
 * and verify service logic for CRUD operations and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    /**
     * Verifies that a user can be created successfully when all inputs are valid.
     */
    @Test
    void createUser_success() {
        Tenant tenant = new Tenant();
        tenant.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setRole(UserRole.DOCTOR);

        when(userRepository.existsByTenantIdAndEmail(1L, "test@test.com")).thenReturn(false);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(1L, "test@test.com", "password123", "John", "Doe", UserRole.DOCTOR);

        assertNotNull(result);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    /**
     * Verifies that creating a user with a duplicate email throws a {@link DuplicateResourceException}.
     */
    @Test
    void createUser_duplicateEmail() {
        when(userRepository.existsByTenantIdAndEmail(1L, "test@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(1L, "test@test.com", "password123", "John", "Doe", UserRole.DOCTOR);
        });
    }

    /**
     * Verifies that creating a user for a non-existent tenant throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createUser_tenantNotFound() {
        when(userRepository.existsByTenantIdAndEmail(1L, "test@test.com")).thenReturn(false);
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.createUser(1L, "test@test.com", "password123", "John", "Doe", UserRole.DOCTOR);
        });
    }

    /**
     * Verifies that a user can be retrieved by their ID when they exist.
     */
    @Test
    void getUserById_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);
        assertEquals(1L, result.getId());
    }

    /**
     * Verifies that retrieving a non-existent user by ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getUserById_notFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    /**
     * Verifies that a user can be retrieved by their tenant ID and email.
     */
    @Test
    void getUserByEmail_success() {
        User user = new User();
        user.setEmail("test@test.com");
        when(userRepository.findByTenantIdAndEmailAndDeletedAtIsNull(1L, "test@test.com"))
            .thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(1L, "test@test.com");
        assertEquals("test@test.com", result.getEmail());
    }

    /**
     * Verifies that retrieving a user by a non-existent email throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getUserByEmail_notFound() {
        when(userRepository.findByTenantIdAndEmailAndDeletedAtIsNull(1L, "test@test.com"))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByEmail(1L, "test@test.com");
        });
    }

    /**
     * Verifies that all users belonging to a tenant can be retrieved.
     */
    @Test
    void getUsersByTenant() {
        when(userRepository.findByTenantIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new User(), new User()));

        List<User> result = userService.getUsersByTenant(1L);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that users can be filtered and retrieved by their role within a tenant.
     */
    @Test
    void getUsersByRole() {
        when(userRepository.findByTenantIdAndRoleAndDeletedAtIsNull(1L, UserRole.DOCTOR))
            .thenReturn(List.of(new User()));

        List<User> result = userService.getUsersByRole(1L, UserRole.DOCTOR);
        assertEquals(1, result.size());
    }

    /**
     * Verifies that the existence check returns true when a user with the given email exists.
     */
    @Test
    void existsByEmail() {
        when(userRepository.existsByTenantIdAndEmail(1L, "test@test.com")).thenReturn(true);
        assertTrue(userService.existsByEmail(1L, "test@test.com"));
    }

    /**
     * Verifies that the count of users by status is returned correctly.
     */
    @Test
    void countByStatus() {
        when(userRepository.countByTenantIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(5L);
        assertEquals(5L, userService.countByStatus(1L, UserStatus.ACTIVE));
    }

    /**
     * Verifies that a user's profile fields can be updated successfully.
     */
    @Test
    void updateUserProfile_success() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Old");
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUserProfile(1L, "New", "Name", "123",
            LocalDate.of(1990, 1, 1), Gender.MALE, "123 Main St");

        assertEquals("New", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("123", result.getPhone());
    }

    /**
     * Verifies that updating a non-existent user's profile throws a {@link ResourceNotFoundException}.
     */
    @Test
    void updateUserProfile_notFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUserProfile(999L, "New", "Name", "123", null, null, null);
        });
    }

    /**
     * Verifies that a user's status can be updated successfully.
     */
    @Test
    void updateUserStatus_success() {
        User user = new User();
        user.setId(1L);
        user.setStatus(UserStatus.ACTIVE);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUserStatus(1L, UserStatus.LOCKED);
        assertEquals(UserStatus.LOCKED, result.getStatus());
    }

    /**
     * Verifies that recording a login resets failed attempts and sets the last login timestamp.
     */
    @Test
    void recordLogin_success() {
        User user = new User();
        user.setId(1L);
        user.setFailedAttempts(3);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        userService.recordLogin(1L);

        verify(userRepository).save(argThat(u -> u.getFailedAttempts() == 0 && u.getLastLogin() != null));
    }

    /**
     * Verifies that incrementing failed login attempts increases the count by one.
     */
    @Test
    void incrementFailedAttempts_success() {
        User user = new User();
        user.setId(1L);
        user.setFailedAttempts(2);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        userService.incrementFailedAttempts(1L);

        verify(userRepository).save(argThat(u -> u.getFailedAttempts() == 3));
    }

    /**
     * Verifies that deleting a user performs a soft delete by setting the deletedAt timestamp.
     */
    @Test
    void deleteUser_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).save(argThat(u -> u.getDeletedAt() != null));
    }

    /**
     * Verifies that deleting a non-existent user throws a {@link ResourceNotFoundException}.
     */
    @Test
    void deleteUser_notFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
    }
}
