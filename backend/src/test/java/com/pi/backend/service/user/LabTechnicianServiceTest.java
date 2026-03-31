package com.pi.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.LabTechnician;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.LabTechnicianRepository;

/**
 * Unit tests for {@link LabTechnicianService}. Uses Mockito to mock repositories
 * and verify service logic for lab technician CRUD operations and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class LabTechnicianServiceTest {

    @Mock
    private LabTechnicianRepository labTechnicianRepository;

    @Mock
    private UserService userService;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private LabTechnicianService labTechnicianService;

    /**
     * Verifies that a lab technician can be created successfully with valid user, department, and certification.
     */
    @Test
    void createLabTechnician_success() {
        User user = new User();
        Department dept = new Department();
        LabTechnician tech = new LabTechnician();
        tech.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(labTechnicianRepository.save(any(LabTechnician.class))).thenReturn(tech);

        LabTechnician result = labTechnicianService.createLabTechnician(1L, 1L, "MLT Certified");

        assertNotNull(result);
        verify(labTechnicianRepository).save(any(LabTechnician.class));
    }

    /**
     * Verifies that creating a lab technician for a non-existent user throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createLabTechnician_userNotFound() {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.createLabTechnician(999L, 1L, "MLT");
        });
    }

    /**
     * Verifies that creating a lab technician with a non-existent department throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createLabTechnician_departmentNotFound() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.createLabTechnician(1L, 999L, "MLT");
        });
    }

    /**
     * Verifies that a lab technician can be retrieved by their ID.
     */
    @Test
    void getLabTechnicianById_success() {
        LabTechnician tech = new LabTechnician();
        tech.setId(1L);
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(tech));

        LabTechnician result = labTechnicianService.getLabTechnicianById(1L);
        assertEquals(1L, result.getId());
    }

    /**
     * Verifies that retrieving a non-existent lab technician by ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getLabTechnicianById_notFound() {
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.getLabTechnicianById(999L);
        });
    }

    /**
     * Verifies that a lab technician can be retrieved by their associated user ID.
     */
    @Test
    void getLabTechnicianByUserId_success() {
        LabTechnician tech = new LabTechnician();
        when(labTechnicianRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(tech));

        LabTechnician result = labTechnicianService.getLabTechnicianByUserId(1L);
        assertNotNull(result);
    }

    /**
     * Verifies that retrieving a lab technician by a non-existent user ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getLabTechnicianByUserId_notFound() {
        when(labTechnicianRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.getLabTechnicianByUserId(999L);
        });
    }

    /**
     * Verifies that all lab technicians assigned to a department can be retrieved.
     */
    @Test
    void getLabTechniciansByDepartment() {
        when(labTechnicianRepository.findByDepartmentIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new LabTechnician(), new LabTechnician()));

        List<LabTechnician> result = labTechnicianService.getLabTechniciansByDepartment(1L);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that a lab technician's certification can be updated successfully.
     */
    @Test
    void updateCertification_success() {
        LabTechnician tech = new LabTechnician();
        tech.setId(1L);
        tech.setCertification("MLT");
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(tech));
        when(labTechnicianRepository.save(any(LabTechnician.class))).thenAnswer(i -> i.getArgument(0));

        LabTechnician result = labTechnicianService.updateCertification(1L, "RT Certified");

        assertEquals("RT Certified", result.getCertification());
    }

    /**
     * Verifies that updating a non-existent lab technician's certification throws a {@link ResourceNotFoundException}.
     */
    @Test
    void updateCertification_notFound() {
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.updateCertification(999L, "RT Certified");
        });
    }

    /**
     * Verifies that deleting a lab technician removes the record from the database.
     */
    @Test
    void deleteLabTechnician_success() {
        LabTechnician tech = new LabTechnician();
        tech.setId(1L);
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(tech));

        labTechnicianService.deleteLabTechnician(1L);

        verify(labTechnicianRepository).delete(tech);
    }

    /**
     * Verifies that deleting a non-existent lab technician throws a {@link ResourceNotFoundException}.
     */
    @Test
    void deleteLabTechnician_notFound() {
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.deleteLabTechnician(999L);
        });
    }
}
