package com.pi.backend.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pi.backend.dto.patient.PatientResponse;
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
        User user = createMockUser();
        Patient patient = createMockPatient(user, null);

        when(patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull("MRN-001")).thenReturn(false);
        when(userService.getUserById(1L)).thenReturn(user);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientResponse result = patientService.createPatient(1L, "MRN-001", "O+", "Penicillin",
            "Diabetes", "Jane Doe", "1234567890", null);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John", result.firstName());
        verify(patientRepository).save(any(Patient.class));
    }

    /**
     * Verifies that a patient can be created with an associated department.
     */
    @Test
    void createPatient_withDepartment() {
        User user = createMockUser();
        Department dept = createMockDepartment();
        Patient patient = createMockPatient(user, dept);

        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientResponse result = patientService.createPatient(1L, null, "O+", null, null, null, null, 1L);

        assertNotNull(result);
        assertEquals(1L, result.primaryDepartmentId());
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
        User user = createMockUser();
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
        User user = createMockUser();
        Patient patient = createMockPatient(user, null);

        when(userService.createUser(1L, "patient@test.com", "password123", "John", "Doe", UserRole.PATIENT))
            .thenReturn(user);
        when(patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull("MRN-001")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientResponse result = patientService.createPatientWithUser(1L, "John", "Doe",
            "patient@test.com", "password123", "MRN-001", "O+", "Peanuts",
            "Diabetes", "Jane Doe", "1234567890", null);

        assertNotNull(result);
        assertEquals("John", result.firstName());
        verify(userService).createUser(1L, "patient@test.com", "password123", "John", "Doe", UserRole.PATIENT);
        verify(patientRepository).save(any(Patient.class));
    }

    /**
     * Verifies that creating a patient with a duplicate email throws a {@link DuplicateResourceException}.
     */
    @Test
    void createPatientWithUser_duplicateEmail() {
        when(userService.createUser(1L, "existing@test.com", "password123", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new DuplicateResourceException("User", "email", "existing@test.com"));

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createPatientWithUser(1L, "John", "Doe",
                "existing@test.com", "password123", null, null, null, null, null, null, null);
        });
    }

    /**
     * Verifies that creating a patient with a duplicate MRN throws a {@link DuplicateResourceException}.
     */
    @Test
    void createPatientWithUser_duplicateMRN() {
        User user = createMockUser();

        when(userService.createUser(1L, "patient@test.com", "password123", "John", "Doe", UserRole.PATIENT))
            .thenReturn(user);
        when(patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull("MRN-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createPatientWithUser(1L, "John", "Doe",
                "patient@test.com", "password123", "MRN-001", null, null, null, null, null, null);
        });
    }

    /**
     * Verifies that creating a patient with a non-existent tenant throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createPatientWithUser_tenantNotFound() {
        when(userService.createUser(999L, "patient@test.com", "password123", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new ResourceNotFoundException("Tenant", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createPatientWithUser(999L, "John", "Doe",
                "patient@test.com", "password123", null, null, null, null, null, null, null);
        });
    }

    /**
     * Verifies that an empty patient record can be created with a new user account.
     */
    @Test
    void createEmptyPatient_success() {
        User user = createMockUser();
        Patient patient = createMockPatient(user, null);

        when(userService.createUser(1L, "patient@test.com", "password123", "John", "Doe", UserRole.PATIENT))
            .thenReturn(user);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientResponse result = patientService.createEmptyPatient(1L, "John", "Doe",
            "patient@test.com", "password123");

        assertNotNull(result);
        assertEquals("John", result.firstName());
        verify(userService).createUser(1L, "patient@test.com", "password123", "John", "Doe", UserRole.PATIENT);
        verify(patientRepository).save(any(Patient.class));
    }

    /**
     * Verifies that creating an empty patient with a duplicate email throws a {@link DuplicateResourceException}.
     */
    @Test
    void createEmptyPatient_duplicateEmail() {
        when(userService.createUser(1L, "existing@test.com", "password123", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new DuplicateResourceException("User", "email", "existing@test.com"));

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createEmptyPatient(1L, "John", "Doe", "existing@test.com", "password123");
        });
    }

    /**
     * Verifies that creating an empty patient with a non-existent tenant throws a {@link ResourceNotFoundException}.
     */
    @Test
    void createEmptyPatient_tenantNotFound() {
        when(userService.createUser(999L, "patient@test.com", "password123", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new ResourceNotFoundException("Tenant", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createEmptyPatient(999L, "John", "Doe", "patient@test.com", "password123");
        });
    }

    /**
     * Verifies that a patient can be retrieved by their ID.
     */
    @Test
    void getPatientById_success() {
        User user = createMockUser();
        Patient patient = createMockPatient(user, null);
        when(patientRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));

        PatientResponse result = patientService.getPatientById(1L);
        assertEquals(1L, result.id());
        assertEquals("John", result.firstName());
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
        User user = createMockUser();
        Patient patient = createMockPatient(user, null);
        when(patientRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));

        PatientResponse result = patientService.getPatientByUserId(1L);
        assertNotNull(result);
        assertEquals("John", result.firstName());
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
        User user = createMockUser();
        Patient patient = createMockPatient(user, null);
        when(patientRepository.findByMedicalRecordNumberAndDeletedAtIsNull("MRN-001"))
            .thenReturn(Optional.of(patient));

        PatientResponse result = patientService.getPatientByMedicalRecordNumber("MRN-001");
        assertNotNull(result);
        assertEquals("MRN-001", result.medicalRecordNumber());
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
        User user = createMockUser();
        Patient p1 = createMockPatient(user, null);
        Patient p2 = createMockPatient(user, null);
        when(patientRepository.findByPrimaryDepartmentIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(p1, p2));

        List<PatientResponse> result = patientService.getPatientsByDepartment(1L);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that a patient's medical details can be updated successfully.
     */
    @Test
    void updatePatient_success() {
        User user = createMockUser();
        Patient patient = createMockPatient(user, null);
        when(patientRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));

        PatientResponse result = patientService.updatePatient(1L, "A+", "Peanuts", "Asthma", "John Doe", "9876543210");

        assertEquals("A+", result.bloodType());
        assertEquals("Peanuts", result.allergies());
    }

    /**
     * Verifies that deleting a patient removes the record from the database.
     */
    @Test
    void deletePatient_success() {
        Patient patient = createMockPatient(createMockUser(), null);
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

    private User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }

    private Department createMockDepartment() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Cardiology");
        return dept;
    }

    private Patient createMockPatient(User user, Department department) {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUser(user);
        patient.setMedicalRecordNumber("MRN-001");
        patient.setBloodType("O+");
        patient.setAllergies("Peanuts");
        patient.setPrimaryDepartment(department);
        patient.setCreatedAt(LocalDateTime.now());
        return patient;
    }
}
