package com.pi.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByTenantId(Long tenantId);

    List<Department> findByTenantIdAndDeletedAtIsNull(Long tenantId);

    Optional<Department> findByTenantIdAndName(Long tenantId, String name);

    boolean existsByTenantIdAndName(Long tenantId, String name);

    Optional<Department> findByIdAndDeletedAtIsNull(Long id);
}
