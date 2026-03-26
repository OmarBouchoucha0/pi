package com.pi.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.LabTechnician;

public interface LabTechnicianRepository extends JpaRepository<LabTechnician, Long> {
}
