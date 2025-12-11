package com.example.realtimeLedger.scheduler;

import com.example.realtimeLedger.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReconciliationJob {

    private final ReconciliationService reconciliationService;

    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void run() {
        reconciliationService.runReconciliation();
    }
}
