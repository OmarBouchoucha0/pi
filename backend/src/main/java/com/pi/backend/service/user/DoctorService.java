package com.pi.backend.service.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pi.backend.exception.DuplicateResourceException;
import com.pi.backend.exception.ResourceNotFoundException;
import com.pi.backend.model.Department;
import com.pi.backend.model.user.Doctor;
import com.pi.backend.model.user.User;
import com.pi.backend.repository.DepartmentRepository;
import com.pi.backend.repository.user.DoctorRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing doctor profiles. Handles creation, retrieval, updates,
 * and deletion of doctor records, including license and specialty information.
 */
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    /**
     * Creates a doctor profile linked to an existing user and department.
     *
     * @param userId             the ID of the user to link the doctor to
     * @param departmentId       the ID of the department the doctor belongs to
     * @param licenseNumber      the doctor's medical license number (must be unique)
     * @param specialty          the doctor's medical specialty
     * @param yearsOfExperience  the number of years the doctor has practiced
     * @return the created Doctor entity
     * @throws DuplicateResourceException if a doctor with the license number already exists
     * @throws ResourceNotFoundException  if the user or department does not exist
     */
    @Transactional
    public Doctor createDoctor(Long userId, Long departmentId,
                               String licenseNumber, String specialty,
                               Integer yearsOfExperience) {
        if (doctorRepository.existsByLicenseNumber(licenseNumber)) {
            throw new DuplicateResourceException("Doctor", "licenseNumber", licenseNumber);
        }

        User user = userService.getUserById(userId);
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(department);
        doctor.setLicenseNumber(licenseNumber);
        doctor.setSpecialty(specialty);
        doctor.setYearsOfExperience(yearsOfExperience);

        return doctorRepository.save(doctor);
    }

    /**
     * Retrieves a doctor by their unique ID.
     *
     * @param doctorId the ID of the doctor to retrieve
     * @return the Doctor entity
     * @throws ResourceNotFoundException if no doctor with the given ID exists
     */
    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findByIdAndDeletedAtIsNull(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));
    }

    /**
     * Retrieves a doctor by their linked user ID.
     *
     * @param userId the ID of the user linked to the doctor
     * @return the Doctor entity
     * @throws ResourceNotFoundException if no doctor linked to the user ID exists
     */
    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
    }

    /**
     * Retrieves a doctor by their medical license number.
     *
     * @param licenseNumber the license number to search for
     * @return the Doctor entity
     * @throws ResourceNotFoundException if no doctor with the license number exists
     */
    public Doctor getDoctorByLicenseNumber(String licenseNumber) {
        return doctorRepository.findByLicenseNumber(licenseNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "licenseNumber", licenseNumber));
    }

    /**
     * Retrieves all doctors assigned to a specific department.
     *
     * @param departmentId the ID of the department
     * @return a list of doctors in the department
     */
    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    /**
     * Retrieves all doctors with a specific medical specialty.
     *
     * @param specialty the specialty to filter by
     * @return a list of doctors matching the specialty
     */
    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    /**
     * Updates the specialty and years of experience for a doctor.
     *
     * @param doctorId           the ID of the doctor to update
     * @param specialty          the updated specialty
     * @param yearsOfExperience  the updated years of experience
     * @return the updated Doctor entity
     * @throws ResourceNotFoundException if no doctor with the given ID exists
     */
    @Transactional
    public Doctor updateDoctor(Long doctorId, String specialty,
                               Integer yearsOfExperience) {
        Doctor doctor = getDoctorById(doctorId);
        doctor.setSpecialty(specialty);
        doctor.setYearsOfExperience(yearsOfExperience);
        return doctorRepository.save(doctor);
    }

    /**
     * Deletes a doctor record.
     *
     * @param doctorId the ID of the doctor to delete
     * @throws ResourceNotFoundException if no doctor with the given ID exists
     */
    @Transactional
    public void deleteDoctor(Long doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        doctorRepository.delete(doctor);
    }
}
