package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.LabTechnician;

public interface LabTechnicianRepository extends JpaRepository<LabTechnician, Long> {
}
