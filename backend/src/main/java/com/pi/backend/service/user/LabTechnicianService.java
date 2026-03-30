package com.pi.backend.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.LabTechnician;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.LabTechnicianRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing lab technician profiles. Handles creation, retrieval,
 * updates, and deletion of lab technician records, including certifications.
 */
@Service
@RequiredArgsConstructor
public class LabTechnicianService {

    private final LabTechnicianRepository labTechnicianRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    /**
     * Creates a lab technician profile linked to an existing user and department.
     *
     * @param userId        the ID of the user to link the technician to
     * @param departmentId  the ID of the department the technician belongs to
     * @param certification the technician's certification details
     * @return the created LabTechnician entity
     * @throws ResourceNotFoundException if the user or department does not exist
     */
    @Transactional
    public LabTechnician createLabTechnician(Long userId, Long departmentId,
                                             String certification) {
        User user = userService.getUserById(userId);
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));

        LabTechnician tech = new LabTechnician();
        tech.setUser(user);
        tech.setDepartment(department);
        tech.setCertification(certification);

        return labTechnicianRepository.save(tech);
    }

    /**
     * Retrieves a lab technician by their unique ID.
     *
     * @param techId the ID of the lab technician to retrieve
     * @return the LabTechnician entity
     * @throws ResourceNotFoundException if no lab technician with the given ID exists
     */
    public LabTechnician getLabTechnicianById(Long techId) {
        return labTechnicianRepository.findByIdAndDeletedAtIsNull(techId)
            .orElseThrow(() -> new ResourceNotFoundException("LabTechnician", techId));
    }

    /**
     * Retrieves a lab technician by their linked user ID.
     *
     * @param userId the ID of the user linked to the technician
     * @return the LabTechnician entity
     * @throws ResourceNotFoundException if no lab technician linked to the user ID exists
     */
    public LabTechnician getLabTechnicianByUserId(Long userId) {
        return labTechnicianRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("LabTechnician", "userId", userId));
    }

    /**
     * Retrieves all lab technicians assigned to a specific department.
     *
     * @param departmentId the ID of the department
     * @return a list of lab technicians in the department
     */
    public List<LabTechnician> getLabTechniciansByDepartment(Long departmentId) {
        return labTechnicianRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    /**
     * Updates the certification of a lab technician.
     *
     * @param techId        the ID of the lab technician to update
     * @param certification the updated certification details
     * @return the updated LabTechnician entity
     * @throws ResourceNotFoundException if no lab technician with the given ID exists
     */
    @Transactional
    public LabTechnician updateCertification(Long techId, String certification) {
        LabTechnician tech = getLabTechnicianById(techId);
        tech.setCertification(certification);
        return labTechnicianRepository.save(tech);
    }

    /**
     * Deletes a lab technician record.
     *
     * @param techId the ID of the lab technician to delete
     * @throws ResourceNotFoundException if no lab technician with the given ID exists
     */
    @Transactional
    public void deleteLabTechnician(Long techId) {
        LabTechnician tech = getLabTechnicianById(techId);
        labTechnicianRepository.delete(tech);
    }
}
