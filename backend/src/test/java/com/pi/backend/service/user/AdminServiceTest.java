package com.pi.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.user.Admin;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.AdminPrivilege;
import com.pi.backend.repository.user.AdminRepository;

/**
 * Unit tests for {@link AdminService}. Uses Mockito to mock repositories
 * and verify service logic for admin CRUD operations and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService adminService;

    /**
     * Verifies that an admin can be created successfully with a valid user and privilege level.
     */
    @Test
    void createAdmin_success() {
        User user = new User();
        Admin admin = new Admin();
        admin.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        Admin result = adminService.createAdmin(1L, AdminPrivilege.SUPER_ADMIN);

        assertNotNull(result);
        verify(adminRepository).save(any(Admin.class));
    }

    /**
     * Verifies that creating an admin for a non-existent user throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createAdmin_userNotFound() {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.createAdmin(999L, AdminPrivilege.SUPER_ADMIN);
        });
    }

    /**
     * Verifies that an admin can be retrieved by their ID.
     */
    @Test
    void getAdminById_success() {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(admin));

        Admin result = adminService.getAdminById(1L);
        assertEquals(1L, result.getId());
    }

    /**
     * Verifies that retrieving a non-existent admin by ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getAdminById_notFound() {
        when(adminRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.getAdminById(999L);
        });
    }

    /**
     * Verifies that an admin can be retrieved by their associated user ID.
     */
    @Test
    void getAdminByUserId_success() {
        Admin admin = new Admin();
        when(adminRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(admin));

        Admin result = adminService.getAdminByUserId(1L);
        assertNotNull(result);
    }

    /**
     * Verifies that retrieving an admin by a non-existent user ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getAdminByUserId_notFound() {
        when(adminRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.getAdminByUserId(999L);
        });
    }

    /**
     * Verifies that admins can be retrieved filtered by their privilege level.
     */
    @Test
    void getAdminsByPrivilegeLevel() {
        when(adminRepository.findByPrivilegeLevelAndDeletedAtIsNull(AdminPrivilege.SUPER_ADMIN))
            .thenReturn(List.of(new Admin(), new Admin()));

        List<Admin> result = adminService.getAdminsByPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that an admin's privilege level can be updated successfully.
     */
    @Test
    void updateAdminPrivilege_success() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        when(adminRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenAnswer(i -> i.getArgument(0));

        Admin result = adminService.updateAdminPrivilege(1L, AdminPrivilege.TENANT_ADMIN);

        assertEquals(AdminPrivilege.TENANT_ADMIN, result.getPrivilegeLevel());
    }

    /**
     * Verifies that deleting an admin removes the record from the database.
     */
    @Test
    void deleteAdmin_success() {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(admin));

        adminService.deleteAdmin(1L);

        verify(adminRepository).delete(admin);
    }

    /**
     * Verifies that deleting a non-existent admin throws a {@link ResourceNotFoundException}.
     */
    @Test
    void deleteAdmin_notFound() {
        when(adminRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.deleteAdmin(999L);
        });
    }
}
