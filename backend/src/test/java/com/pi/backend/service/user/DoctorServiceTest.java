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

import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.Doctor;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.DoctorRepository;

/**
 * Unit tests for {@link DoctorService}. Uses Mockito to mock repositories
 * and verify service logic for doctor CRUD operations and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserService userService;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DoctorService doctorService;

    /**
     * Verifies that a doctor can be created successfully with valid license, user, and department.
     */
    @Test
    void createDoctor_success() {
        User user = new User();
        user.setId(1L);
        Department dept = new Department();
        dept.setId(1L);
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(doctorRepository.existsByLicenseNumber("LIC-001")).thenReturn(false);
        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        Doctor result = doctorService.createDoctor(1L, 1L, "LIC-001", "Cardiology", 5);

        assertNotNull(result);
        verify(doctorRepository).save(any(Doctor.class));
    }

    /**
     * Verifies that creating a doctor with a duplicate license number throws a {@link DuplicateResourceException}.
     */
    @Test
    void createDoctor_duplicateLicense() {
        when(doctorRepository.existsByLicenseNumber("LIC-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            doctorService.createDoctor(1L, 1L, "LIC-001", "Cardiology", 5);
        });
    }

    /**
     * Verifies that creating a doctor for a non-existent user throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createDoctor_userNotFound() {
        when(doctorRepository.existsByLicenseNumber("LIC-001")).thenReturn(false);
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.createDoctor(999L, 1L, "LIC-001", "Cardiology", 5);
        });
    }

    /**
     * Verifies that creating a doctor with a non-existent department throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createDoctor_departmentNotFound() {
        User user = new User();
        when(doctorRepository.existsByLicenseNumber("LIC-001")).thenReturn(false);
        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.createDoctor(1L, 999L, "LIC-001", "Cardiology", 5);
        });
    }

    /**
     * Verifies that a doctor can be retrieved by their ID.
     */
    @Test
    void getDoctorById_success() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(doctorRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getDoctorById(1L);
        assertEquals(1L, result.getId());
    }

    /**
     * Verifies that retrieving a non-existent doctor by ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getDoctorById_notFound() {
        when(doctorRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.getDoctorById(999L);
        });
    }

    /**
     * Verifies that a doctor can be retrieved by their associated user ID.
     */
    @Test
    void getDoctorByUserId_success() {
        Doctor doctor = new Doctor();
        when(doctorRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getDoctorByUserId(1L);
        assertNotNull(result);
    }

    /**
     * Verifies that retrieving a doctor by a non-existent user ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getDoctorByUserId_notFound() {
        when(doctorRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.getDoctorByUserId(999L);
        });
    }

    /**
     * Verifies that a doctor can be retrieved by their license number.
     */
    @Test
    void getDoctorByLicenseNumber_success() {
        Doctor doctor = new Doctor();
        when(doctorRepository.findByLicenseNumber("LIC-001")).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getDoctorByLicenseNumber("LIC-001");
        assertNotNull(result);
    }

    /**
     * Verifies that retrieving a doctor by a non-existent license number throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getDoctorByLicenseNumber_notFound() {
        when(doctorRepository.findByLicenseNumber("LIC-999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.getDoctorByLicenseNumber("LIC-999");
        });
    }

    /**
     * Verifies that all doctors assigned to a department can be retrieved.
     */
    @Test
    void getDoctorsByDepartment() {
        when(doctorRepository.findByDepartmentIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new Doctor(), new Doctor()));

        List<Doctor> result = doctorService.getDoctorsByDepartment(1L);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that doctors can be retrieved filtered by their specialty.
     */
    @Test
    void getDoctorsBySpecialty() {
        when(doctorRepository.findBySpecialty("Cardiology"))
            .thenReturn(List.of(new Doctor()));

        List<Doctor> result = doctorService.getDoctorsBySpecialty("Cardiology");
        assertEquals(1, result.size());
    }

    /**
     * Verifies that a doctor's specialty and years of experience can be updated.
     */
    @Test
    void updateDoctor_success() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setSpecialty("Cardiology");
        when(doctorRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));

        Doctor result = doctorService.updateDoctor(1L, "Neurology", 10);

        assertEquals("Neurology", result.getSpecialty());
        assertEquals(10, result.getYearsOfExperience());
    }

    /**
     * Verifies that updating a non-existent doctor throws a {@link ResourceNotFoundException}.
     */
    @Test
    void updateDoctor_notFound() {
        when(doctorRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.updateDoctor(999L, "Neurology", 10);
        });
    }

    /**
     * Verifies that deleting a doctor removes the record from the database.
     */
    @Test
    void deleteDoctor_success() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(doctorRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(doctor));

        doctorService.deleteDoctor(1L);

        verify(doctorRepository).delete(doctor);
    }

    /**
     * Verifies that deleting a non-existent doctor throws a {@link ResourceNotFoundException}.
     */
    @Test
    void deleteDoctor_notFound() {
        when(doctorRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            doctorService.deleteDoctor(999L);
        });
    }
}
