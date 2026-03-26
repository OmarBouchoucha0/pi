package com.pi.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
