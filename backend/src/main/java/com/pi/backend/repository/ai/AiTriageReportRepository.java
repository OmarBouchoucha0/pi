package com.pi.backend.repository.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.backend.model.ai.AiTriageReport;

public interface AiTriageReportRepository extends JpaRepository<AiTriageReport, Long> {
}
