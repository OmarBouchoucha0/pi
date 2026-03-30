package com.pi.backend.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Secretary;

public interface SecretaryRepository extends JpaRepository<Secretary, Long> {

    Optional<Secretary> findByUserId(Long userId);

    Optional<Secretary> findByUserIdAndDeletedAtIsNull(Long userId);

    List<Secretary> findByDepartmentId(Long departmentId);

    List<Secretary> findByDepartmentIdAndDeletedAtIsNull(Long departmentId);

    Optional<Secretary> findByIdAndDeletedAtIsNull(Long id);
}
