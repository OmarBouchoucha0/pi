package tn.esprit.pi.repository.vital;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.vital.ParameterThreshold;
import tn.esprit.pi.enums.vital.VitalType;

@Repository
public interface ParameterThresholdRepository extends JpaRepository<ParameterThreshold, Long> {

    Optional<ParameterThreshold> findByTenantIdAndType(Long tenantId, VitalType type);

    List<ParameterThreshold> findByTenantId(Long tenantId);

    boolean existsByTenantIdAndType(Long tenantId, VitalType type);
}
