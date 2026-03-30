package com.pi.backend.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Admin;
import com.pi.backend.model.user.enums.AdminPrivilege;

/**
 * Repository for managing Admin entities.
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUserId(Long userId);

    Optional<Admin> findByUserIdAndDeletedAtIsNull(Long userId);

    List<Admin> findByPrivilegeLevel(AdminPrivilege privilegeLevel);

    List<Admin> findByPrivilegeLevelAndDeletedAtIsNull(AdminPrivilege privilegeLevel);

    Optional<Admin> findByIdAndDeletedAtIsNull(Long id);
}
