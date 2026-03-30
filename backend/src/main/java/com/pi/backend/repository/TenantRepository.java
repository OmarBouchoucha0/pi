package com.pi.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Tenant;
import com.pi.backend.model.TenantStatus;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByName(String name);

    boolean existsByName(String name);

    List<Tenant> findByStatus(TenantStatus status);

    List<Tenant> findByDeletedAtIsNull();

    Optional<Tenant> findByIdAndDeletedAtIsNull(Long id);

    Optional<Tenant> findByNameAndDeletedAtIsNull(String name);
}
