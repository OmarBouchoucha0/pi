package com.pi.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
