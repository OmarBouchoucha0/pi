package com.pi.backend.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.Patient;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Patient createPatient(Long userId, String medicalRecordNumber,
                                 String bloodType, String allergies,
                                 String chronicConditions,
                                 String emergencyContactName,
                                 String emergencyContactPhone,
                                 Long primaryDepartmentId) {
        if (medicalRecordNumber != null && patientRepository.existsByMedicalRecordNumberAndDeletedIsNull(medicalRecordNumber)) {
            throw new DuplicateResourceException("Patient", "medicalRecordNumber", medicalRecordNumber);
        }

        User user = userService.getUserById(userId);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setMedicalRecordNumber(medicalRecordNumber);
        patient.setBloodType(bloodType);
        patient.setAllergies(allergies);
        patient.setChronicConditions(chronicConditions);
        patient.setEmergencyContactName(emergencyContactName);
        patient.setEmergencyContactPhone(emergencyContactPhone);

        if (primaryDepartmentId != null) {
            Department dept = departmentRepository.findById(primaryDepartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", primaryDepartmentId));
            patient.setPrimaryDepartment(dept);
        }

        return patientRepository.save(patient);
    }

    public Patient getPatientById(Long patientId) {
        return patientRepository.findByIdAndDeletedAtIsNull(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
    }

    public Patient getPatientByUserId(Long userId) {
        return patientRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "userId", userId));
    }

    public Patient getPatientByMedicalRecordNumber(String mrn) {
        return patientRepository.findByMedicalRecordNumberAndDeletedIsNull(mrn)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "medicalRecordNumber", mrn));
    }

    public List<Patient> getPatientsByDepartment(Long departmentId) {
        return patientRepository.findByPrimaryDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    @Transactional
    public Patient updatePatient(Long patientId, String bloodType,
                                String allergies, String chronicConditions,
                                String emergencyContactName,
                                String emergencyContactPhone) {
        Patient patient = getPatientById(patientId);
        patient.setBloodType(bloodType);
        patient.setAllergies(allergies);
        patient.setChronicConditions(chronicConditions);
        patient.setEmergencyContactName(emergencyContactName);
        patient.setEmergencyContactPhone(emergencyContactPhone);
        return patientRepository.save(patient);
    }

    @Transactional
    public void deletePatient(Long patientId) {
        Patient patient = getPatientById(patientId);
        patientRepository.delete(patient);
    }
}
