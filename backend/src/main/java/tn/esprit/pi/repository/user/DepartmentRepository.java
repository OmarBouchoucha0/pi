package tn.esprit.pi.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tn.esprit.pi.entity.user.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.tenant WHERE d.deletedAt IS NULL")
    List<Department> findAllByDeletedAtIsNull();

    List<Department> findByTenantIdAndDeletedAtIsNull(Long tenantId);

    List<Department> findByHospitalIdAndDeletedAtIsNull(Long hospitalId);

    Optional<Department> findByNameAndTenantIdAndDeletedAtIsNull(String name, Long tenantId);

    boolean existsByNameAndTenantIdAndDeletedAtIsNull(String name, Long tenantId);
}
