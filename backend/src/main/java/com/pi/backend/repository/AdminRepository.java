package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
