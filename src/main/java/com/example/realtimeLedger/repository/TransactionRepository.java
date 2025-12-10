package com.example.realtimeLedger.repository;

import com.example.realtimeLedger.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(UUID transactionId);
    Optional<Transaction> findByIdempotencyKey(String key);
}
