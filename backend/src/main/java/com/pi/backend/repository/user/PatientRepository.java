package com.pi.backend.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUserId(Long userId);

    Optional<Patient> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<Patient> findByMedicalRecordNumber(String medicalRecordNumber);

    boolean existsByMedicalRecordNumber(String medicalRecordNumber);

    List<Patient> findByPrimaryDepartmentId(Long departmentId);

    List<Patient> findByPrimaryDepartmentIdAndDeletedAtIsNull(Long departmentId);

    Optional<Patient> findByIdAndDeletedAtIsNull(Long id);
}
