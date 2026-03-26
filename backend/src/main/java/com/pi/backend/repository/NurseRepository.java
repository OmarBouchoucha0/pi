package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Nurse;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
}
