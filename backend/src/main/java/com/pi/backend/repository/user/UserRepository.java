package com.pi.backend.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.model.user.enums.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByTenantIdAndEmail(Long tenantId, String email);

    boolean existsByTenantIdAndEmail(Long tenantId, String email);

    List<User> findByTenantId(Long tenantId);

    List<User> findByTenantIdAndDeletedAtIsNull(Long tenantId);

    List<User> findByTenantIdAndRole(Long tenantId, UserRole role);

    List<User> findByTenantIdAndStatus(Long tenantId, UserStatus status);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    long countByTenantIdAndStatus(Long tenantId, UserStatus status);
}
