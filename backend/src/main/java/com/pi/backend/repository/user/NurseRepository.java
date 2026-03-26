package com.pi.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Nurse;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
}
