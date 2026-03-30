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
import com.pi.backend.model.user.Nurse;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.NurseShift;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.NurseRepository;

/**
 * Unit tests for {@link NurseService}. Uses Mockito to mock repositories
 * and verify service logic for nurse CRUD operations and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class NurseServiceTest {

    @Mock
    private NurseRepository nurseRepository;

    @Mock
    private UserService userService;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private NurseService nurseService;

    /**
     * Verifies that a nurse can be created successfully with valid user, department, and shift.
     */
    @Test
    void createNurse_success() {
        User user = new User();
        Department dept = new Department();
        Nurse nurse = new Nurse();
        nurse.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(nurseRepository.save(any(Nurse.class))).thenReturn(nurse);

        Nurse result = nurseService.createNurse(1L, 1L, NurseShift.DAY);

        assertNotNull(result);
        verify(nurseRepository).save(any(Nurse.class));
    }

    /**
     * Verifies that creating a nurse for a non-existent user throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createNurse_userNotFound() {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            nurseService.createNurse(999L, 1L, NurseShift.DAY);
        });
    }

    /**
     * Verifies that creating a nurse with a non-existent department throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createNurse_departmentNotFound() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            nurseService.createNurse(1L, 999L, NurseShift.DAY);
        });
    }

    /**
     * Verifies that a nurse can be retrieved by their ID.
     */
    @Test
    void getNurseById_success() {
        Nurse nurse = new Nurse();
        nurse.setId(1L);
        when(nurseRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(nurse));

        Nurse result = nurseService.getNurseById(1L);
        assertEquals(1L, result.getId());
    }

    /**
     * Verifies that retrieving a non-existent nurse by ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getNurseById_notFound() {
        when(nurseRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            nurseService.getNurseById(999L);
        });
    }

    /**
     * Verifies that a nurse can be retrieved by their associated user ID.
     */
    @Test
    void getNurseByUserId_success() {
        Nurse nurse = new Nurse();
        when(nurseRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(nurse));

        Nurse result = nurseService.getNurseByUserId(1L);
        assertNotNull(result);
    }

    /**
     * Verifies that retrieving a nurse by a non-existent user ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getNurseByUserId_notFound() {
        when(nurseRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            nurseService.getNurseByUserId(999L);
        });
    }

    /**
     * Verifies that all nurses assigned to a department can be retrieved.
     */
    @Test
    void getNursesByDepartment() {
        when(nurseRepository.findByDepartmentIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new Nurse(), new Nurse()));

        List<Nurse> result = nurseService.getNursesByDepartment(1L);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that nurses can be retrieved filtered by their shift.
     */
    @Test
    void getNursesByShift() {
        when(nurseRepository.findByShiftAndDeletedAtIsNull(NurseShift.DAY))
            .thenReturn(List.of(new Nurse()));

        List<Nurse> result = nurseService.getNursesByShift(NurseShift.DAY);
        assertEquals(1, result.size());
    }

    /**
     * Verifies that nurses can be retrieved filtered by both department and shift.
     */
    @Test
    void getNursesByDepartmentAndShift() {
        when(nurseRepository.findByDepartmentIdAndShiftAndDeletedAtIsNull(1L, NurseShift.NIGHT))
            .thenReturn(List.of(new Nurse()));

        List<Nurse> result = nurseService.getNursesByDepartmentAndShift(1L, NurseShift.NIGHT);
        assertEquals(1, result.size());
    }

    /**
     * Verifies that a nurse's shift can be updated successfully.
     */
    @Test
    void updateNurseShift_success() {
        Nurse nurse = new Nurse();
        nurse.setId(1L);
        nurse.setShift(NurseShift.DAY);
        when(nurseRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(nurse));
        when(nurseRepository.save(any(Nurse.class))).thenAnswer(i -> i.getArgument(0));

        Nurse result = nurseService.updateNurseShift(1L, NurseShift.NIGHT);

        assertEquals(NurseShift.NIGHT, result.getShift());
    }

    /**
     * Verifies that deleting a nurse removes the record from the database.
     */
    @Test
    void deleteNurse_success() {
        Nurse nurse = new Nurse();
        nurse.setId(1L);
        when(nurseRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(nurse));

        nurseService.deleteNurse(1L);

        verify(nurseRepository).delete(nurse);
    }

    /**
     * Verifies that deleting a non-existent nurse throws a {@link ResourceNotFoundException}.
     */
    @Test
    void deleteNurse_notFound() {
        when(nurseRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            nurseService.deleteNurse(999L);
        });
    }
}
