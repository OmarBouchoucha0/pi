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

import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Tenant;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.Gender;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;
import com.pi.backend.repository.TenantRepository;
import com.pi.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private UserService userService;

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
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(1L, "test@test.com", "hash", "John", "Doe", UserRole.DOCTOR);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_duplicateEmail() {
        when(userRepository.existsByTenantIdAndEmail(1L, "test@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(1L, "test@test.com", "hash", "John", "Doe", UserRole.DOCTOR);
        });
    }

    @Test
    void createUser_tenantNotFound() {
        when(userRepository.existsByTenantIdAndEmail(1L, "test@test.com")).thenReturn(false);
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.createUser(1L, "test@test.com", "hash", "John", "Doe", UserRole.DOCTOR);
        });
    }

    @Test
    void getUserById_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void getUserByEmail_success() {
        User user = new User();
        user.setEmail("test@test.com");
        when(userRepository.findByTenantIdAndEmailAndDeletedAtIsNull(1L, "test@test.com"))
            .thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(1L, "test@test.com");
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void getUserByEmail_notFound() {
        when(userRepository.findByTenantIdAndEmailAndDeletedAtIsNull(1L, "test@test.com"))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByEmail(1L, "test@test.com");
        });
    }

    @Test
    void getUsersByTenant() {
        when(userRepository.findByTenantIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new User(), new User()));

        List<User> result = userService.getUsersByTenant(1L);
        assertEquals(2, result.size());
    }

    @Test
    void getUsersByRole() {
        when(userRepository.findByTenantIdAndRoleAndDeletedAtIsNull(1L, UserRole.DOCTOR))
            .thenReturn(List.of(new User()));

        List<User> result = userService.getUsersByRole(1L, UserRole.DOCTOR);
        assertEquals(1, result.size());
    }

    @Test
    void existsByEmail() {
        when(userRepository.existsByTenantIdAndEmail(1L, "test@test.com")).thenReturn(true);
        assertTrue(userService.existsByEmail(1L, "test@test.com"));
    }

    @Test
    void countByStatus() {
        when(userRepository.countByTenantIdAndStatus(1L, UserStatus.ACTIVE)).thenReturn(5L);
        assertEquals(5L, userService.countByStatus(1L, UserStatus.ACTIVE));
    }

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

    @Test
    void updateUserProfile_notFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUserProfile(999L, "New", "Name", "123", null, null, null);
        });
    }

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

    @Test
    void recordLogin_success() {
        User user = new User();
        user.setId(1L);
        user.setFailedAttempts(3);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        userService.recordLogin(1L);

        verify(userRepository).save(argThat(u -> u.getFailedAttempts() == 0 && u.getLastLogin() != null));
    }

    @Test
    void incrementFailedAttempts_success() {
        User user = new User();
        user.setId(1L);
        user.setFailedAttempts(2);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        userService.incrementFailedAttempts(1L);

        verify(userRepository).save(argThat(u -> u.getFailedAttempts() == 3));
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).save(argThat(u -> u.getDeletedAt() != null));
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
    }
}
