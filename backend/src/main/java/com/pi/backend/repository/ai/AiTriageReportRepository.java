package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.AiTriageReport;

/**
 * Repository for managing AiTriageReport entities.
 */
public interface AiTriageReportRepository extends JpaRepository<AiTriageReport, Long> {
}
