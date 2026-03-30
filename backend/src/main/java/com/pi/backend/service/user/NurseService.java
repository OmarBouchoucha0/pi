package com.pi.backend.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.Nurse;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.NurseShift;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.NurseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NurseService {

    private final NurseRepository nurseRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Nurse createNurse(Long userId, Long departmentId, NurseShift shift) {
        User user = userService.getUserById(userId);
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));

        Nurse nurse = new Nurse();
        nurse.setUser(user);
        nurse.setDepartment(department);
        nurse.setShift(shift);

        return nurseRepository.save(nurse);
    }

    public Nurse getNurseById(Long nurseId) {
        return nurseRepository.findByIdAndDeletedAtIsNull(nurseId)
            .orElseThrow(() -> new ResourceNotFoundException("Nurse", nurseId));
    }

    public Nurse getNurseByUserId(Long userId) {
        return nurseRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Nurse", "userId", userId));
    }

    public List<Nurse> getNursesByDepartment(Long departmentId) {
        return nurseRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    public List<Nurse> getNursesByShift(NurseShift shift) {
        return nurseRepository.findByShiftAndDeletedAtIsNull(shift);
    }

    public List<Nurse> getNursesByDepartmentAndShift(Long departmentId, NurseShift shift) {
        return nurseRepository.findByDepartmentIdAndShiftAndDeletedAtIsNull(departmentId, shift);
    }

    @Transactional
    public Nurse updateNurseShift(Long nurseId, NurseShift shift) {
        Nurse nurse = getNurseById(nurseId);
        nurse.setShift(shift);
        return nurseRepository.save(nurse);
    }

    @Transactional
    public void deleteNurse(Long nurseId) {
        Nurse nurse = getNurseById(nurseId);
        nurseRepository.delete(nurse);
    }
}
