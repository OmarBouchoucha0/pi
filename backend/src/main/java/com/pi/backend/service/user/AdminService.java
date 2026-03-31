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

/**
 * Service for managing administrator profiles. Handles creation, retrieval,
 * updates, and deletion of admin records with privilege level management.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;

    /**
     * Creates an administrator profile linked to an existing user.
     *
     * @param userId          the ID of the user to link the admin to
     * @param privilegeLevel  the privilege level to assign to the admin
     * @return the created Admin entity
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Transactional
    public Admin createAdmin(Long userId, AdminPrivilege privilegeLevel) {
        User user = userService.getUserById(userId);

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setPrivilegeLevel(privilegeLevel);

        return adminRepository.save(admin);
    }

    /**
     * Retrieves an administrator by their unique ID.
     *
     * @param adminId the ID of the admin to retrieve
     * @return the Admin entity
     * @throws ResourceNotFoundException if no admin with the given ID exists
     */
    public Admin getAdminById(Long adminId) {
        return adminRepository.findByIdAndDeletedAtIsNull(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));
    }

    /**
     * Retrieves an administrator by their linked user ID.
     *
     * @param userId the ID of the user linked to the admin
     * @return the Admin entity
     * @throws ResourceNotFoundException if no admin linked to the user ID exists
     */
    public Admin getAdminByUserId(Long userId) {
        return adminRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Admin", "userId", userId));
    }

    /**
     * Retrieves all administrators with a specific privilege level.
     *
     * @param privilegeLevel the privilege level to filter by
     * @return a list of admins matching the privilege level
     */
    public List<Admin> getAdminsByPrivilegeLevel(AdminPrivilege privilegeLevel) {
        return adminRepository.findByPrivilegeLevelAndDeletedAtIsNull(privilegeLevel);
    }

    /**
     * Updates the privilege level of an administrator.
     *
     * @param adminId         the ID of the admin to update
     * @param privilegeLevel  the new privilege level
     * @return the updated Admin entity
     * @throws ResourceNotFoundException if no admin with the given ID exists
     */
    @Transactional
    public Admin updateAdminPrivilege(Long adminId, AdminPrivilege privilegeLevel) {
        Admin admin = getAdminById(adminId);
        admin.setPrivilegeLevel(privilegeLevel);
        return adminRepository.save(admin);
    }

    /**
     * Soft-deletes an administrator record by setting the deletedAt timestamp.
     *
     * @param adminId the ID of the admin to soft-delete
     * @throws ResourceNotFoundException if no active admin with the given ID exists
     */
    @Transactional
    public void deleteAdmin(Long adminId) {
        Admin admin = getAdminById(adminId);
        adminRepository.delete(admin);
    }
}
