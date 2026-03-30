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

    @Test
    void createPatient_duplicateMRN() {
        when(patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull("MRN-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createPatient(1L, "MRN-001", "O+", null, null, null, null, null);
        });
    }

    @Test
    void createPatient_userNotFound() {
        when(userService.getUserById(999L)).thenThrow(new ResourceNotFoundException("User", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createPatient(999L, "MRN-001", "O+", null, null, null, null, null);
        });
    }

    @Test
    void createPatient_departmentNotFound() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createPatient(1L, null, "O+", null, null, null, null, 999L);
        });
    }

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

    @Test
    void createPatientWithUser_duplicateEmail() {
        when(userService.createUser(1L, "existing@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new DuplicateResourceException("User", "email", "existing@test.com"));

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.createPatientWithUser(1L, "John", "Doe",
                "existing@test.com", "hash", null, null, null, null, null, null, null);
        });
    }

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

    @Test
    void createPatientWithUser_tenantNotFound() {
        when(userService.createUser(999L, "patient@test.com", "hash", "John", "Doe", UserRole.PATIENT))
            .thenThrow(new ResourceNotFoundException("Tenant", 999L));

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.createPatientWithUser(999L, "John", "Doe",
                "patient@test.com", "hash", null, null, null, null, null, null, null);
        });
    }

    @Test
    void getPatientById_success() {
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPatientById_notFound() {
        when(patientRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientById(999L);
        });
    }

    @Test
    void getPatientByUserId_success() {
        Patient patient = new Patient();
        when(patientRepository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientByUserId(1L);
        assertNotNull(result);
    }

    @Test
    void getPatientByUserId_notFound() {
        when(patientRepository.findByUserIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientByUserId(999L);
        });
    }

    @Test
    void getPatientByMedicalRecordNumber_success() {
        Patient patient = new Patient();
        when(patientRepository.findByMedicalRecordNumberAndDeletedAtIsNull("MRN-001"))
            .thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientByMedicalRecordNumber("MRN-001");
        assertNotNull(result);
    }

    @Test
    void getPatientByMedicalRecordNumber_notFound() {
        when(patientRepository.findByMedicalRecordNumberAndDeletedAtIsNull("MRN-999"))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientByMedicalRecordNumber("MRN-999");
        });
    }

    @Test
    void getPatientsByDepartment() {
        when(patientRepository.findByPrimaryDepartmentIdAndDeletedAtIsNull(1L))
            .thenReturn(List.of(new Patient(), new Patient()));

        List<Patient> result = patientService.getPatientsByDepartment(1L);
        assertEquals(2, result.size());
    }

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

    @Test
    void deletePatient_success() {
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(patient));

        patientService.deletePatient(1L);

        verify(patientRepository).delete(patient);
    }

    @Test
    void deletePatient_notFound() {
        when(patientRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deletePatient(999L);
        });
    }
}
