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

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService adminService;

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

    @Test
    void createAdmin_userNotFound() {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.createAdmin(999L, AdminPrivilege.SUPER_ADMIN);
        });
    }

    @Test
    void getAdminById_success() {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(admin));

        Admin result = adminService.getAdminById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAdminById_notFound() {
        when(adminRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.getAdminById(999L);
        });
    }

    @Test
    void getAdminByUserId_success() {
        Admin admin = new Admin();
        when(adminRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(admin));

        Admin result = adminService.getAdminByUserId(1L);
        assertNotNull(result);
    }

    @Test
    void getAdminByUserId_notFound() {
        when(adminRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.getAdminByUserId(999L);
        });
    }

    @Test
    void getAdminsByPrivilegeLevel() {
        when(adminRepository.findByPrivilegeLevelAndDeletedAtIsNull(AdminPrivilege.SUPER_ADMIN))
            .thenReturn(List.of(new Admin(), new Admin()));

        List<Admin> result = adminService.getAdminsByPrivilegeLevel(AdminPrivilege.SUPER_ADMIN);
        assertEquals(2, result.size());
    }

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

    @Test
    void deleteAdmin_success() {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(admin));

        adminService.deleteAdmin(1L);

        verify(adminRepository).delete(admin);
    }

    @Test
    void deleteAdmin_notFound() {
        when(adminRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adminService.deleteAdmin(999L);
        });
    }
}
