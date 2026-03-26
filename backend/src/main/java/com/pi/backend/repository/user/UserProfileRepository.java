package com.pi.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
