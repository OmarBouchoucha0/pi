package tn.esprit.pi.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.pi.entity.user.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    List<Hospital> findAllByDeletedAtIsNull();

    List<Hospital> findByTenantIdAndDeletedAtIsNull(Long tenantId);

    Optional<Hospital> findByNameAndTenantIdAndDeletedAtIsNull(String name, Long tenantId);

    boolean existsByNameAndTenantIdAndDeletedAtIsNull(String name, Long tenantId);
}
