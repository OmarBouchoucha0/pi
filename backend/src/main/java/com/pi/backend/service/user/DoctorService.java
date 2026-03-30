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

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

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

    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findByIdAndDeletedAtIsNull(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));
    }

    public Doctor getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
    }

    public Doctor getDoctorByLicenseNumber(String licenseNumber) {
        return doctorRepository.findByLicenseNumber(licenseNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "licenseNumber", licenseNumber));
    }

    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId);
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    @Transactional
    public Doctor updateDoctor(Long doctorId, String specialty,
                               Integer yearsOfExperience) {
        Doctor doctor = getDoctorById(doctorId);
        doctor.setSpecialty(specialty);
        doctor.setYearsOfExperience(yearsOfExperience);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        doctorRepository.delete(doctor);
    }
}
