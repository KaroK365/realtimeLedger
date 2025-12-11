package com.example.realtimeLedger.repository;

import com.example.realtimeLedger.entity.ReconciliationReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationReportRepository extends JpaRepository<ReconciliationReport, Long> {
    ReconciliationReport findTopByOrderByRunAtDesc();
}
