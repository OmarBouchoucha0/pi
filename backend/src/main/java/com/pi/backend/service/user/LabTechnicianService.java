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

@Service
@RequiredArgsConstructor
public class LabTechnicianService {

    private final LabTechnicianRepository labTechnicianRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

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

    public LabTechnician getLabTechnicianById(Long techId) {
        return labTechnicianRepository.findByIdAndDeletedAtIsNull(techId)
            .orElseThrow(() -> new ResourceNotFoundException("LabTechnician", techId));
    }

    public LabTechnician getLabTechnicianByUserId(Long userId) {
        return labTechnicianRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("LabTechnician", "userId", userId));
    }

    public List<LabTechnician> getLabTechniciansByDepartment(Long departmentId) {
        return labTechnicianRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    @Transactional
    public LabTechnician updateCertification(Long techId, String certification) {
        LabTechnician tech = getLabTechnicianById(techId);
        tech.setCertification(certification);
        return labTechnicianRepository.save(tech);
    }

    @Transactional
    public void deleteLabTechnician(Long techId) {
        LabTechnician tech = getLabTechnicianById(techId);
        labTechnicianRepository.delete(tech);
    }
}
