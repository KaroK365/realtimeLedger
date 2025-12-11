package com.example.realtimeLedger.controller;

import com.example.realtimeLedger.entity.ReconciliationReport;
import com.example.realtimeLedger.repository.ReconciliationReportRepository;
import com.example.realtimeLedger.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReconciliationReportRepository repo;
    private final ReconciliationService reconciliationService;

    @GetMapping("/reconciliation/run")
    public Map<String, Object> runNow() {
        return Map.of("report", reconciliationService.runReconciliation());
    }


    @GetMapping("/reconciliation/latest")
    public Map<String, Object> latest() {
        ReconciliationReport report = repo.findAll()
                .stream()
                .max(Comparator.comparing(ReconciliationReport::getRunAt))
                .orElse(null);

        return Map.of(
                "latestReport", report
        );
    }
}
