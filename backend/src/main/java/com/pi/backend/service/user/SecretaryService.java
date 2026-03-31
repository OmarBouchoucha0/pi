package com.pi.backend.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.Secretary;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.SecretaryRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing secretary profiles. Handles creation, retrieval,
 * updates, and deletion of secretary records and their department assignments.
 */
@Service
@RequiredArgsConstructor
public class SecretaryService {

    private final SecretaryRepository secretaryRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    /**
     * Creates a secretary profile linked to an existing user and department.
     *
     * @param userId       the ID of the user to link the secretary to
     * @param departmentId the ID of the department the secretary belongs to
     * @return the created Secretary entity
     * @throws ResourceNotFoundException if the user or department does not exist
     */
    @Transactional
    public Secretary createSecretary(Long userId, Long departmentId) {
        User user = userService.getUserById(userId);
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));

        Secretary secretary = new Secretary();
        secretary.setUser(user);
        secretary.setDepartment(department);

        return secretaryRepository.save(secretary);
    }

    /**
     * Retrieves a secretary by their unique ID.
     *
     * @param secretaryId the ID of the secretary to retrieve
     * @return the Secretary entity
     * @throws ResourceNotFoundException if no secretary with the given ID exists
     */
    public Secretary getSecretaryById(Long secretaryId) {
        return secretaryRepository.findByIdAndDeletedAtIsNull(secretaryId)
            .orElseThrow(() -> new ResourceNotFoundException("Secretary", secretaryId));
    }

    /**
     * Retrieves a secretary by their linked user ID.
     *
     * @param userId the ID of the user linked to the secretary
     * @return the Secretary entity
     * @throws ResourceNotFoundException if no secretary linked to the user ID exists
     */
    public Secretary getSecretaryByUserId(Long userId) {
        return secretaryRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Secretary", "userId", userId));
    }

    /**
     * Retrieves all secretaries assigned to a specific department.
     *
     * @param departmentId the ID of the department
     * @return a list of secretaries in the department
     */
    public List<Secretary> getSecretariesByDepartment(Long departmentId) {
        return secretaryRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    /**
     * Updates the department assignment of a secretary.
     *
     * @param secretaryId  the ID of the secretary to update
     * @param departmentId the ID of the new department
     * @return the updated Secretary entity
     * @throws ResourceNotFoundException if the secretary or department does not exist
     */
    @Transactional
    public Secretary updateSecretaryDepartment(Long secretaryId, Long departmentId) {
        Secretary secretary = getSecretaryById(secretaryId);
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
        secretary.setDepartment(department);
        return secretaryRepository.save(secretary);
    }

    /**
     * Soft-deletes a secretary record by setting the deletedAt timestamp.
     *
     * @param secretaryId the ID of the secretary to soft-delete
     * @throws ResourceNotFoundException if no active secretary with the given ID exists
     */
    @Transactional
    public void deleteSecretary(Long secretaryId) {
        Secretary secretary = getSecretaryById(secretaryId);
        secretaryRepository.delete(secretary);
    }
}
