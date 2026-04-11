package tn.esprit.pi.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tn.esprit.pi.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByIdAndDeletedAtIsNull(Long id);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.tenant WHERE u.id = :id AND u.deletedAt IS NULL")
  Optional<User> findByIdWithRoleAndTenant(@Param("id") Long id);

  List<User> findAllByDeletedAtIsNull();

  Optional<User> findByEmailAndDeletedAtIsNull(String email);

  boolean existsByEmailAndDeletedAtIsNull(String email);

  List<User> findByTenantIdAndDeletedAtIsNull(Long tenantId);

  List<User> findByRoleIdAndDeletedAtIsNull(Long roleId);

  List<User> findByDepartmentIdAndDeletedAtIsNull(Long departmentId);

  Optional<User> findByMedicalRecordNumberAndDeletedAtIsNull(String medicalRecordNumber);

  Optional<User> findByLicenseNumberAndDeletedAtIsNull(String licenseNumber);

  List<User> findByMedicalRecordNumberIsNotNullAndDeletedAtIsNull();

  List<User> findByLicenseNumberIsNotNullAndDeletedAtIsNull();

  List<User> findByPrivilegeLevelIsNotNullAndDeletedAtIsNull();
}
