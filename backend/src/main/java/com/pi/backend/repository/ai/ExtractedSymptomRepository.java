package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.ExtractedSymptom;

public interface ExtractedSymptomRepository extends JpaRepository<ExtractedSymptom, Long> {
}
