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

/**
 * Service for managing nurse profiles. Handles creation, retrieval, updates,
 * and deletion of nurse records, including shift and department assignments.
 */
@Service
@RequiredArgsConstructor
public class NurseService {

    private final NurseRepository nurseRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    /**
     * Creates a nurse profile linked to an existing user and department.
     *
     * @param userId       the ID of the user to link the nurse to
     * @param departmentId the ID of the department the nurse belongs to
     * @param shift        the nurse's assigned shift
     * @return the created Nurse entity
     * @throws ResourceNotFoundException if the user or department does not exist
     */
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

    /**
     * Retrieves a nurse by their unique ID.
     *
     * @param nurseId the ID of the nurse to retrieve
     * @return the Nurse entity
     * @throws ResourceNotFoundException if no nurse with the given ID exists
     */
    public Nurse getNurseById(Long nurseId) {
        return nurseRepository.findByIdAndDeletedAtIsNull(nurseId)
            .orElseThrow(() -> new ResourceNotFoundException("Nurse", nurseId));
    }

    /**
     * Retrieves a nurse by their linked user ID.
     *
     * @param userId the ID of the user linked to the nurse
     * @return the Nurse entity
     * @throws ResourceNotFoundException if no nurse linked to the user ID exists
     */
    public Nurse getNurseByUserId(Long userId) {
        return nurseRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Nurse", "userId", userId));
    }

    /**
     * Retrieves all nurses assigned to a specific department.
     *
     * @param departmentId the ID of the department
     * @return a list of nurses in the department
     */
    public List<Nurse> getNursesByDepartment(Long departmentId) {
        return nurseRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    /**
     * Retrieves all nurses assigned to a specific shift.
     *
     * @param shift the shift to filter by
     * @return a list of nurses working the specified shift
     */
    public List<Nurse> getNursesByShift(NurseShift shift) {
        return nurseRepository.findByShiftAndDeletedAtIsNull(shift);
    }

    /**
     * Retrieves all nurses in a specific department and shift.
     *
     * @param departmentId the ID of the department
     * @param shift        the shift to filter by
     * @return a list of nurses matching the department and shift
     */
    public List<Nurse> getNursesByDepartmentAndShift(Long departmentId, NurseShift shift) {
        return nurseRepository.findByDepartmentIdAndShiftAndDeletedAtIsNull(departmentId, shift);
    }

    /**
     * Updates the shift assignment of a nurse.
     *
     * @param nurseId the ID of the nurse to update
     * @param shift   the new shift assignment
     * @return the updated Nurse entity
     * @throws ResourceNotFoundException if no nurse with the given ID exists
     */
    @Transactional
    public Nurse updateNurseShift(Long nurseId, NurseShift shift) {
        Nurse nurse = getNurseById(nurseId);
        nurse.setShift(shift);
        return nurseRepository.save(nurse);
    }

    /**
     * Soft-deletes a nurse record by setting the deletedAt timestamp.
     *
     * @param nurseId the ID of the nurse to soft-delete
     * @throws ResourceNotFoundException if no active nurse with the given ID exists
     */
    @Transactional
    public void deleteNurse(Long nurseId) {
        Nurse nurse = getNurseById(nurseId);
        nurseRepository.delete(nurse);
    }
}
