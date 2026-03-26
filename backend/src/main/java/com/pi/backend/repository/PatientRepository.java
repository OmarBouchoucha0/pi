package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
