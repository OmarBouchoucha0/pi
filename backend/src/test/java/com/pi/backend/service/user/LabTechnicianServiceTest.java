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

    @Test
    void createLabTechnician_userNotFound() {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.createLabTechnician(999L, 1L, "MLT");
        });
    }

    @Test
    void createLabTechnician_departmentNotFound() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.createLabTechnician(1L, 999L, "MLT");
        });
    }

    @Test
    void getLabTechnicianById_success() {
        LabTechnician tech = new LabTechnician();
        tech.setId(1L);
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(tech));

        LabTechnician result = labTechnicianService.getLabTechnicianById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getLabTechnicianById_notFound() {
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.getLabTechnicianById(999L);
        });
    }

    @Test
    void getLabTechnicianByUserId_success() {
        LabTechnician tech = new LabTechnician();
        when(labTechnicianRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(tech));

        LabTechnician result = labTechnicianService.getLabTechnicianByUserId(1L);
        assertNotNull(result);
    }

    @Test
    void getLabTechnicianByUserId_notFound() {
        when(labTechnicianRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.getLabTechnicianByUserId(999L);
        });
    }

    @Test
    void getLabTechniciansByDepartment() {
        when(labTechnicianRepository.findByDepartmentIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new LabTechnician(), new LabTechnician()));

        List<LabTechnician> result = labTechnicianService.getLabTechniciansByDepartment(1L);
        assertEquals(2, result.size());
    }

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

    @Test
    void deleteLabTechnician_success() {
        LabTechnician tech = new LabTechnician();
        tech.setId(1L);
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(tech));

        labTechnicianService.deleteLabTechnician(1L);

        verify(labTechnicianRepository).delete(tech);
    }

    @Test
    void deleteLabTechnician_notFound() {
        when(labTechnicianRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            labTechnicianService.deleteLabTechnician(999L);
        });
    }
}
