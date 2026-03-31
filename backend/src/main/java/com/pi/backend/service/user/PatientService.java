package com.pi.backend.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.dto.patient.PatientResponse;
import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.Patient;
import com.pi.backend.model.user.User;
import com.pi.backend.model.user.enums.UserRole;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.PatientRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing patient records. Handles creation, retrieval, updates,
 * and deletion of patient profiles, including medical record data and emergency contacts.
 */
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    /**
     * Creates a patient profile linked to an existing user.
     *
     * @param userId                 the ID of the user to link the patient to
     * @param medicalRecordNumber    the patient's medical record number (must be unique)
     * @param bloodType              the patient's blood type
     * @param allergies              the patient's known allergies
     * @param chronicConditions      the patient's chronic conditions
     * @param emergencyContactName   the emergency contact's name
     * @param emergencyContactPhone  the emergency contact's phone number
     * @param primaryDepartmentId    the ID of the primary department (nullable)
     * @return the created PatientResponse
     * @throws DuplicateResourceException if the medical record number already exists
     * @throws ResourceNotFoundException  if the user or department does not exist
     */
    @Transactional
    public PatientResponse createPatient(Long userId, String medicalRecordNumber,
                                         String bloodType, String allergies,
                                         String chronicConditions,
                                         String emergencyContactName,
                                         String emergencyContactPhone,
                                         Long primaryDepartmentId) {
        if (medicalRecordNumber != null && patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull(medicalRecordNumber)) {
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

        return toResponse(patientRepository.save(patient));
    }

    /**
     * Creates a new user account and patient profile together in a single transaction.
     *
     * @param tenantId               the ID of the tenant the user belongs to
     * @param firstName              the patient's first name
     * @param lastName               the patient's last name
     * @param email                  the patient's email address
     * @param passwordHash           the hashed password
     * @param medicalRecordNumber    the patient's medical record number (must be unique)
     * @param bloodType              the patient's blood type
     * @param allergies              the patient's known allergies
     * @param chronicConditions      the patient's chronic conditions
     * @param emergencyContactName   the emergency contact's name
     * @param emergencyContactPhone  the emergency contact's phone number
     * @param primaryDepartmentId    the ID of the primary department (nullable)
     * @return the created PatientResponse
     * @throws DuplicateResourceException if the email or medical record number already exists
     * @throws ResourceNotFoundException  if the tenant or department does not exist
     */
    @Transactional
    public PatientResponse createPatientWithUser(Long tenantId, String firstName, String lastName,
                                                 String email, String passwordHash,
                                                 String medicalRecordNumber, String bloodType,
                                                 String allergies, String chronicConditions,
                                                 String emergencyContactName,
                                                 String emergencyContactPhone,
                                                 Long primaryDepartmentId) {
        User user = userService.createUser(tenantId, email, passwordHash,
            firstName, lastName, UserRole.PATIENT);

        if (medicalRecordNumber != null && patientRepository.existsByMedicalRecordNumberAndDeletedAtIsNull(medicalRecordNumber)) {
            throw new DuplicateResourceException("Patient", "medicalRecordNumber", medicalRecordNumber);
        }

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

        return toResponse(patientRepository.save(patient));
    }

    /**
     * Creates a minimal patient profile with only a user account and no medical data.
     *
     * @param tenantId     the ID of the tenant the user belongs to
     * @param firstName    the patient's first name
     * @param lastName     the patient's last name
     * @param email        the patient's email address
     * @param passwordHash the hashed password
     * @return the created PatientResponse with no medical information populated
     * @throws DuplicateResourceException if the email already exists
     * @throws ResourceNotFoundException  if the tenant does not exist
     */
    @Transactional
    public PatientResponse createEmptyPatient(Long tenantId, String firstName, String lastName,
                                              String email, String passwordHash) {
        User user = userService.createUser(tenantId, email, passwordHash,
            firstName, lastName, UserRole.PATIENT);

        Patient patient = new Patient();
        patient.setUser(user);

        return toResponse(patientRepository.save(patient));
    }

    /**
     * Retrieves all non-deleted patients with their associations loaded.
     *
     * @return list of all patients as PatientResponse DTOs
     */
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAllWithAssociations().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * Retrieves a patient by their unique ID.
     *
     * @param patientId the ID of the patient to retrieve
     * @return the PatientResponse DTO
     * @throws ResourceNotFoundException if no patient with the given ID exists
     */
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long patientId) {
        Patient patient = patientRepository.findByIdAndDeletedAtIsNull(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        return toResponse(patient);
    }

    /**
     * Retrieves a patient by their linked user ID.
     *
     * @param userId the ID of the user linked to the patient
     * @return the PatientResponse DTO
     * @throws ResourceNotFoundException if no patient linked to the user ID exists
     */
    @Transactional(readOnly = true)
    public PatientResponse getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "userId", userId));
        return toResponse(patient);
    }

    /**
     * Retrieves a patient by their medical record number.
     *
     * @param mrn the medical record number to search for
     * @return the PatientResponse DTO
     * @throws ResourceNotFoundException if no patient with the MRN exists
     */
    @Transactional(readOnly = true)
    public PatientResponse getPatientByMedicalRecordNumber(String mrn) {
        Patient patient = patientRepository.findByMedicalRecordNumberAndDeletedAtIsNull(mrn)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "medicalRecordNumber", mrn));
        return toResponse(patient);
    }

    /**
     * Retrieves all patients assigned to a specific department.
     *
     * @param departmentId the ID of the department
     * @return a list of PatientResponse DTOs in the department
     */
    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsByDepartment(Long departmentId) {
        return patientRepository.findByPrimaryDepartmentIdAndDeletedAtIsNull(departmentId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * Updates the medical information of a patient.
     *
     * @param patientId              the ID of the patient to update
     * @param bloodType              the updated blood type
     * @param allergies              the updated allergies
     * @param chronicConditions      the updated chronic conditions
     * @param emergencyContactName   the updated emergency contact name
     * @param emergencyContactPhone  the updated emergency contact phone
     * @return the updated PatientResponse
     * @throws ResourceNotFoundException if no patient with the given ID exists
     */
    @Transactional
    public PatientResponse updatePatient(Long patientId, String bloodType,
                                         String allergies, String chronicConditions,
                                         String emergencyContactName,
                                         String emergencyContactPhone) {
        Patient patient = patientRepository.findByIdAndDeletedAtIsNull(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        patient.setBloodType(bloodType);
        patient.setAllergies(allergies);
        patient.setChronicConditions(chronicConditions);
        patient.setEmergencyContactName(emergencyContactName);
        patient.setEmergencyContactPhone(emergencyContactPhone);
        return toResponse(patientRepository.save(patient));
    }

    /**
     * Deletes a patient record.
     *
     * @param patientId the ID of the patient to delete
     * @throws ResourceNotFoundException if no patient with the given ID exists
     */
    @Transactional
    public void deletePatient(Long patientId) {
        Patient patient = patientRepository.findByIdAndDeletedAtIsNull(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        patientRepository.delete(patient);
    }

    /**
     * Converts a Patient entity to a PatientResponse DTO.
     * This method should only be called within a transactional context
     * to ensure lazy-loaded associations are accessible.
     */
    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
            patient.getId(),
            patient.getUser().getId(),
            patient.getUser().getFirstName(),
            patient.getUser().getLastName(),
            patient.getUser().getEmail(),
            patient.getMedicalRecordNumber(),
            patient.getBloodType(),
            patient.getAllergies(),
            patient.getChronicConditions(),
            patient.getEmergencyContactName(),
            patient.getEmergencyContactPhone(),
            patient.getPrimaryDepartment() != null ? patient.getPrimaryDepartment().getId() : null,
            patient.getCreatedAt()
        );
    }
}
