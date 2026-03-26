package com.pi.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
