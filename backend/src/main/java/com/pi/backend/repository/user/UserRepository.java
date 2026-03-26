package com.pi.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
