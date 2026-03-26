package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
