package com.pi.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
