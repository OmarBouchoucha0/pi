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
import com.pi.backend.model.user.Secretary;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.SecretaryRepository;

/**
 * Unit tests for {@link SecretaryService}. Uses Mockito to mock repositories
 * and verify service logic for secretary CRUD operations and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class SecretaryServiceTest {

    @Mock
    private SecretaryRepository secretaryRepository;

    @Mock
    private UserService userService;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private SecretaryService secretaryService;

    /**
     * Verifies that a secretary can be created successfully with valid user and department.
     */
    @Test
    void createSecretary_success() {
        User user = new User();
        Department dept = new Department();
        Secretary secretary = new Secretary();
        secretary.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(secretaryRepository.save(any(Secretary.class))).thenReturn(secretary);

        Secretary result = secretaryService.createSecretary(1L, 1L);

        assertNotNull(result);
        verify(secretaryRepository).save(any(Secretary.class));
    }

    /**
     * Verifies that creating a secretary for a non-existent user throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createSecretary_userNotFound() {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            secretaryService.createSecretary(999L, 1L);
        });
    }

    /**
     * Verifies that creating a secretary with a non-existent department throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createSecretary_departmentNotFound() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            secretaryService.createSecretary(1L, 999L);
        });
    }

    /**
     * Verifies that a secretary can be retrieved by their ID.
     */
    @Test
    void getSecretaryById_success() {
        Secretary secretary = new Secretary();
        secretary.setId(1L);
        when(secretaryRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(secretary));

        Secretary result = secretaryService.getSecretaryById(1L);
        assertEquals(1L, result.getId());
    }

    /**
     * Verifies that retrieving a non-existent secretary by ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getSecretaryById_notFound() {
        when(secretaryRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            secretaryService.getSecretaryById(999L);
        });
    }

    /**
     * Verifies that a secretary can be retrieved by their associated user ID.
     */
    @Test
    void getSecretaryByUserId_success() {
        Secretary secretary = new Secretary();
        when(secretaryRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(secretary));

        Secretary result = secretaryService.getSecretaryByUserId(1L);
        assertNotNull(result);
    }

    /**
     * Verifies that retrieving a secretary by a non-existent user ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getSecretaryByUserId_notFound() {
        when(secretaryRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            secretaryService.getSecretaryByUserId(999L);
        });
    }

    /**
     * Verifies that all secretaries assigned to a department can be retrieved.
     */
    @Test
    void getSecretariesByDepartment() {
        when(secretaryRepository.findByDepartmentIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new Secretary(), new Secretary()));

        List<Secretary> result = secretaryService.getSecretariesByDepartment(1L);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that a secretary's department assignment can be updated successfully.
     */
    @Test
    void updateSecretaryDepartment_success() {
        Secretary secretary = new Secretary();
        secretary.setId(1L);
        Department oldDept = new Department();
        Department newDept = new Department();

        when(secretaryRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(secretary));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(newDept));
        when(secretaryRepository.save(any(Secretary.class))).thenAnswer(i -> i.getArgument(0));

        Secretary result = secretaryService.updateSecretaryDepartment(1L, 2L);

        assertNotNull(result);
        verify(secretaryRepository).save(any(Secretary.class));
    }

    /**
     * Verifies that updating a secretary with a non-existent department throws a {@link ResourceNotFoundException}.
     */
    @Test
    void updateSecretaryDepartment_departmentNotFound() {
        Secretary secretary = new Secretary();
        secretary.setId(1L);
        when(secretaryRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(secretary));
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            secretaryService.updateSecretaryDepartment(1L, 999L);
        });
    }

    /**
     * Verifies that deleting a secretary removes the record from the database.
     */
    @Test
    void deleteSecretary_success() {
        Secretary secretary = new Secretary();
        secretary.setId(1L);
        when(secretaryRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(secretary));

        secretaryService.deleteSecretary(1L);

        verify(secretaryRepository).delete(secretary);
    }

    /**
     * Verifies that deleting a non-existent secretary throws a {@link ResourceNotFoundException}.
     */
    @Test
    void deleteSecretary_notFound() {
        when(secretaryRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            secretaryService.deleteSecretary(999L);
        });
    }
}
