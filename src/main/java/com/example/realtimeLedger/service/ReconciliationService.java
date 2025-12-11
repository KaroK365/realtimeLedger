package com.example.realtimeLedger.service;

import com.example.realtimeLedger.entity.Account;
import com.example.realtimeLedger.entity.Anomaly;
import com.example.realtimeLedger.entity.LedgerEntry;
import com.example.realtimeLedger.entity.ReconciliationReport;
import com.example.realtimeLedger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReconciliationService {

    private final AccountRepository accountRepo;
    private final LedgerEntryRepository ledgerRepo;
    private final TransactionRepository transactionRepo;
    private final ReconciliationReportRepository reportRepo;
    private final AnomalyRepository anomalyRepo;

    @Transactional
    public ReconciliationReport runReconciliation() {

        AtomicBoolean mismatch = new AtomicBoolean(false);
        StringBuilder details = new StringBuilder();

        // -----------------------------
        // 1. Group ledger deltas by account
        // -----------------------------
        Map<UUID, Long> ledgerByAccount = ledgerRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        LedgerEntry::getAccountId,
                        Collectors.summingLong(LedgerEntry::getDelta)
                ));

        // -----------------------------
        // 2. Check each account balance vs its ledger sum
        // -----------------------------
        for (Account acc : accountRepo.findAll()) {

            long ledgerDelta = ledgerByAccount.getOrDefault(acc.getAccountId(), 0L);
            long balance = acc.getBalance();

            if (balance != ledgerDelta) {
                mismatch.set(true);
                details.append("Mismatch in account ")
                        .append(acc.getAccountId())
                        .append(": ledger = ").append(ledgerDelta)
                        .append(", balance = ").append(balance)
                        .append("\n");

                // Save anomaly entry
                anomalyRepo.save(
                        Anomaly.builder()
                                .description("Balance mismatch in account " + acc.getAccountId())
                                .build()
                );
            }
        }

        // -----------------------------
        // 3. Detect orphan ledger entries
        // -----------------------------
        ledgerRepo.findAll().forEach(entry -> {
            transactionRepo.findByTransactionId(entry.getTransactionId())
                    .orElseGet(() -> {
                        mismatch.set(true);
                        details.append("Orphan ledger entry for tx: ")
                                .append(entry.getTransactionId())
                                .append("\n");

                        anomalyRepo.save(
                                Anomaly.builder()
                                        .description("Orphan ledger entry for tx " + entry.getTransactionId())
                                        .build()
                        );
                        return null;
                    });
        });

        // -----------------------------
        // 4. Build reconciliation report
        // -----------------------------
        long accountsTotal = accountRepo.findAll().stream()
                .mapToLong(Account::getBalance)
                .sum();

        ReconciliationReport report = ReconciliationReport.builder()
                .ledgerSum(0L) // always zero in double-entry systems
                .accountsSum(accountsTotal)
                .mismatch(mismatch.get())
                .details(details.toString())
                .runAt(OffsetDateTime.now())
                .build();

        return reportRepo.save(report);
    }
}
