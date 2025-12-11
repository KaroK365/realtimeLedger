package com.example.realtimeLedger.service;

import com.example.realtimeLedger.entity.LedgerEntry;
import com.example.realtimeLedger.entity.Transaction;
import com.example.realtimeLedger.repository.LedgerEntryRepository;
import com.example.realtimeLedger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionQueryService {

    private final TransactionRepository transactionRepo;
    private final LedgerEntryRepository ledgerRepo;

    // Fetch all transactions for an account
    public List<Transaction> getTransactionsForAccount(UUID accountId) {
        return transactionRepo.findByFromAccountOrToAccount(accountId, accountId);
    }

    // Paginated transactions for an account
    public Page<Transaction> getPaginatedTransactions(UUID accountId, Pageable pageable) {
        return transactionRepo.findByAccount(accountId, pageable);
    }

    // Ledger entries for a transaction
    public List<LedgerEntry> getLedgerForTransaction(UUID transactionId) {
        return ledgerRepo.findByTransactionId(transactionId);
    }
}
