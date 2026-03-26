package com.pi.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.user.Secretary;

public interface SecretaryRepository extends JpaRepository<Secretary, Long> {
}
