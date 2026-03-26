package com.pi.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
