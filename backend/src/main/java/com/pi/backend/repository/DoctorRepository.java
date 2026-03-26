package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
