package com.pi.backend.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Nurse;
import com.pi.backend.model.user.enums.NurseShift;

public interface NurseRepository extends JpaRepository<Nurse, Long> {

    Optional<Nurse> findByUserId(Long userId);

    Optional<Nurse> findByUserIdAndDeletedAtIsNull(Long userId);

    List<Nurse> findByDepartmentId(Long departmentId);

    List<Nurse> findByDepartmentIdAndDeletedAtIsNull(Long departmentId);

    List<Nurse> findByShift(NurseShift shift);

    List<Nurse> findByDepartmentIdAndShift(Long departmentId, NurseShift shift);

    Optional<Nurse> findByIdAndDeletedAtIsNull(Long id);
}
