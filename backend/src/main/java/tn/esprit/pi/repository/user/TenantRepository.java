package tn.esprit.pi.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.pi.entity.user.Tenant;
import tn.esprit.pi.enums.user.TenantStatus;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByIdAndDeletedAtIsNull(Long id);

    List<Tenant> findAllByDeletedAtIsNull();

    Optional<Tenant> findByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndDeletedAtIsNull(String name);

    List<Tenant> findByStatusAndDeletedAtIsNull(TenantStatus status);
}
