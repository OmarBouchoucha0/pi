package com.pi.backend.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.user.Admin;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.AdminPrivilege;
import com.pi.backend.repository.user.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;

    @Transactional
    public Admin createAdmin(Long userId, AdminPrivilege privilegeLevel) {
        User user = userService.getUserById(userId);

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setPrivilegeLevel(privilegeLevel);

        return adminRepository.save(admin);
    }

    public Admin getAdminById(Long adminId) {
        return adminRepository.findByIdAndDeletedAtIsNull(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));
    }

    public Admin getAdminByUserId(Long userId) {
        return adminRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin", "userId", userId));
    }

    public List<Admin> getAdminsByPrivilegeLevel(AdminPrivilege privilegeLevel) {
        return adminRepository.findByPrivilegeLevelAndDeletedAtIsNull(privilegeLevel);
    }

    @Transactional
    public Admin updateAdminPrivilege(Long adminId, AdminPrivilege privilegeLevel) {
        Admin admin = getAdminById(adminId);
        admin.setPrivilegeLevel(privilegeLevel);
        return adminRepository.save(admin);
    }

    @Transactional
    public void deleteAdmin(Long adminId) {
        Admin admin = getAdminById(adminId);
        adminRepository.delete(admin);
    }
}
