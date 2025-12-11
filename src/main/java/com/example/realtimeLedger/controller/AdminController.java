package com.example.realtimeLedger.controller;

import com.example.realtimeLedger.entity.Anomaly;
import com.example.realtimeLedger.entity.ReconciliationReport;
import com.example.realtimeLedger.repository.AnomalyRepository;
import com.example.realtimeLedger.repository.ReconciliationReportRepository;
import com.example.realtimeLedger.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReconciliationService reconciliationService;
    private final ReconciliationReportRepository reportRepo;
    private final AnomalyRepository anomalyRepo;

    // 1️⃣ Run reconciliation
    @GetMapping("/reconciliation/run")
    public ReconciliationReport runNow() {
        return reconciliationService.runReconciliation();
    }

    // 2️⃣ Get latest reconciliation report
    @GetMapping("/reconciliation/latest")
    public ReconciliationReport latest() {
        return reportRepo.findTopByOrderByRunAtDesc();
    }

    // 3️⃣ List all reconciliation reports
    @GetMapping("/reconciliation/all")
    public List<ReconciliationReport> allReports() {
        return reportRepo.findAll();
    }

    // 4️⃣ List all anomalies
    @GetMapping("/anomalies")
    public List<Anomaly> getAnomalies() {
        return anomalyRepo.findAll();
    }
}
