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
import com.pi.backend.model.user.Patient;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.PatientRepository;

/**
 * Unit tests for {@link PatientService}. Uses Mockito to mock repositories
 * and verify service logic for patient CRUD operations and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserService userService;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private PatientService patientService;

    /**
     * Verifies that a patient can be created successfully with a valid medical record number.
     */
    @Test
    void createPatient_success() {
        User user = new User();
        user.setId(1L);
        Patient patient = new Patient();
        patient.setId(1L);

        when(patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull("MRN-001")).thenReturn(false);
        when(userService.getUserById(1L)).thenReturn(user);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(1L, "MRN-001", "O+", "Penicillin",
            "Diabetes", "Jane Doe", "1234567890", null);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }

    /**
     * Verifies that a patient can be created with an associated department.
     */
    @Test
    void createPatient_withDepartment() {
        User user = new User();
        user.setId(1L);
        Department dept = new Department();
        dept.setId(1L);
        Patient patient = new Patient();
        patient.setId(1L);

        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(1L, null, "O+", null, null, null, null, 1L);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }

    /**
     * Verifies that creating a patient with a duplicate MRN throws a {@link DuplicateResourceException}.
     */
    @Test
    void createPatient_duplicateMRN() {
        when(patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull("MRN-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createPatient(1L, "MRN-001", "O+", null, null, null, null, null);
        });
    }

    /**
     * Verifies that creating a patient for a non-existent user throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createPatient_userNotFound() {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createPatient(999L, "MRN-001", "O+", null, null, null, null, null);
        });
    }

    /**
     * Verifies that creating a patient with a non-existent department throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createPatient_departmentNotFound() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createPatient(1L, null, "O+", null, null, null, null, 999L);
        });
    }

    /**
     * Verifies that a patient and user account can be created together successfully.
     */
    @Test
    void createPatientWithUser_success() {
        User user = new User();
        user.setId(1L);
        Patient patient = new Patient();
        patient.setId(1L);

        when(userService.createUser(1L, "patient@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenReturn(user);
        when(patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull("MRN-001")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatientWithUser(1L, "John", "Doe",
            "patient@test.com", "hash", "MRN-001", "O+", "Peanuts",
            "Diabetes", "Jane Doe", "1234567890", null);

        assertNotNull(result);
        verify(userService).createUser(1L, "patient@test.com", "hash", "John", "Doe", UserRole.PATIENT);
        verify(patientRepository).save(any(Patient.class));
    }

    /**
     * Verifies that creating a patient with a duplicate email throws a {@link DuplicateResourceException}.
     */
    @Test
    void createPatientWithUser_duplicateEmail() {
        when(userService.createUser(1L, "existing@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new DuplicateResourceException("User", "email", "existing@test.com"));

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createPatientWithUser(1L, "John", "Doe",
                "existing@test.com", "hash", null, null, null, null, null, null, null);
        });
    }

    /**
     * Verifies that creating a patient with a duplicate MRN throws a {@link DuplicateResourceException}.
     */
    @Test
    void createPatientWithUser_duplicateMRN() {
        User user = new User();
        user.setId(1L);

        when(userService.createUser(1L, "patient@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenReturn(user);
        when(patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull("MRN-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createPatientWithUser(1L, "John", "Doe",
                "patient@test.com", "hash", "MRN-001", null, null, null, null, null, null);
        });
    }

    /**
     * Verifies that creating a patient with a non-existent tenant throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createPatientWithUser_tenantNotFound() {
        when(userService.createUser(999L, "patient@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new ResourceNotFoundException("Tenant", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createPatientWithUser(999L, "John", "Doe",
                "patient@test.com", "hash", null, null, null, null, null, null, null);
        });
    }

    /**
     * Verifies that an empty patient record can be created with a new user account.
     */
    @Test
    void createEmptyPatient_success() {
        User user = new User();
        user.setId(1L);
        Patient patient = new Patient();
        patient.setId(1L);

        when(userService.createUser(1L, "patient@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenReturn(user);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createEmptyPatient(1L, "John", "Doe",
            "patient@test.com", "hash");

        assertNotNull(result);
        verify(userService).createUser(1L, "patient@test.com", "hash", "John", "Doe", UserRole.PATIENT);
        verify(patientRepository).save(any(Patient.class));
    }

    /**
     * Verifies that creating an empty patient with a duplicate email throws a {@link DuplicateResourceException}.
     */
    @Test
    void createEmptyPatient_duplicateEmail() {
        when(userService.createUser(1L, "existing@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new DuplicateResourceException("User", "email", "existing@test.com"));

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createEmptyPatient(1L, "John", "Doe", "existing@test.com", "hash");
        });
    }

    /**
     * Verifies that creating an empty patient with a non-existent tenant throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createEmptyPatient_tenantNotFound() {
        when(userService.createUser(999L, "patient@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new ResourceNotFoundException("Tenant", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createEmptyPatient(999L, "John", "Doe", "patient@test.com", "hash");
        });
    }

    /**
     * Verifies that a patient can be retrieved by their ID.
     */
    @Test
    void getPatientById_success() {
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(1L);
        assertEquals(1L, result.getId());
    }

    /**
     * Verifies that retrieving a non-existent patient by ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getPatientById_notFound() {
        when(patientRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientById(999L);
        });
    }

    /**
     * Verifies that a patient can be retrieved by their associated user ID.
     */
    @Test
    void getPatientByUserId_success() {
        Patient patient = new Patient();
        when(patientRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientByUserId(1L);
        assertNotNull(result);
    }

    /**
     * Verifies that retrieving a patient by a non-existent user ID throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getPatientByUserId_notFound() {
        when(patientRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientByUserId(999L);
        });
    }

    /**
     * Verifies that a patient can be retrieved by their medical record number.
     */
    @Test
    void getPatientByMedicalRecordNumber_success() {
        Patient patient = new Patient();
        when(patientRepository.findByMedicalRecordNumberAndDeletedAtIsNull("MRN-001"))
            .thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientByMedicalRecordNumber("MRN-001");
        assertNotNull(result);
    }

    /**
     * Verifies that retrieving a patient by a non-existent MRN throws a {@link ResourceNotFoundException}.
     */
    @Test
    void getPatientByMedicalRecordNumber_notFound() {
        when(patientRepository.findByMedicalRecordNumberAndDeletedAtIsNull("MRN-999"))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientByMedicalRecordNumber("MRN-999");
        });
    }

    /**
     * Verifies that all patients assigned to a department can be retrieved.
     */
    @Test
    void getPatientsByDepartment() {
        when(patientRepository.findByPrimaryDepartmentIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new Patient(), new Patient()));

        List<Patient> result = patientService.getPatientsByDepartment(1L);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that a patient's medical details can be updated successfully.
     */
    @Test
    void updatePatient_success() {
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));

        Patient result = patientService.updatePatient(1L, "A+", "Peanuts", "Asthma", "John Doe", "9876543210");

        assertEquals("A+", result.getBloodType());
        assertEquals("Peanuts", result.getAllergies());
    }

    /**
     * Verifies that deleting a patient removes the record from the database.
     */
    @Test
    void deletePatient_success() {
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));

        patientService.deletePatient(1L);

        verify(patientRepository).delete(patient);
    }

    /**
     * Verifies that deleting a non-existent patient throws a {@link ResourceNotFoundException}.
     */
    @Test
    void deletePatient_notFound() {
        when(patientRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deletePatient(999L);
        });
    }
}
