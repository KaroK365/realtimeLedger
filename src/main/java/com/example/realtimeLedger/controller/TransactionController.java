package com.example.realtimeLedger.controller;

import com.example.realtimeLedger.entity.LedgerEntry;
import com.example.realtimeLedger.entity.Transaction;
import com.example.realtimeLedger.service.TransactionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionQueryService queryService;

    // 1️⃣ List all transactions for an account
    @GetMapping("/account/{accountId}")
    public List<Transaction> getTransactionsForAccount(@PathVariable UUID accountId) {
        return queryService.getTransactionsForAccount(accountId);
    }

    // 2️⃣ Paginated transactions
    @GetMapping("/account/{accountId}/paged")
    public Page<Transaction> getPagedTransactions(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return queryService.getPaginatedTransactions(accountId, PageRequest.of(page, size));
    }

    // 3️⃣ Ledger entries for a transaction
    @GetMapping("/{transactionId}/ledger")
    public List<LedgerEntry> getLedger(@PathVariable UUID transactionId) {
        return queryService.getLedgerForTransaction(transactionId);
    }
}
