package com.pi.backend.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.LabTechnician;

/**
 * Repository for managing LabTechnician entities.
 */
public interface LabTechnicianRepository extends JpaRepository<LabTechnician, Long> {

    Optional<LabTechnician> findByUserId(Long userId);

    Optional<LabTechnician> findByUserIdAndDeletedAtIsNull(Long userId);

    List<LabTechnician> findByDepartmentId(Long departmentId);

    List<LabTechnician> findByDepartmentIdAndDeletedAtIsNull(Long departmentId);

    Optional<LabTechnician> findByIdAndDeletedAtIsNull(Long id);
}
