package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
