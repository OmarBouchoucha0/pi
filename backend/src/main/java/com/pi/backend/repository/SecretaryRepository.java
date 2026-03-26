package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Secretary;

public interface SecretaryRepository extends JpaRepository<Secretary, Long> {
}
