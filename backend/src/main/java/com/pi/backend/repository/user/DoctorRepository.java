package com.pi.backend.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Doctor;

/**
 * Repository for managing Doctor entities.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUserId(Long userId);

    Optional<Doctor> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Doctor> findByDepartmentId(Long departmentId);

    List<Doctor> findByDepartmentIdAndDeletedAtIsNull(Long departmentId);

    List<Doctor> findBySpecialty(String specialty);

    Optional<Doctor> findByIdAndDeletedAtIsNull(Long id);
}
