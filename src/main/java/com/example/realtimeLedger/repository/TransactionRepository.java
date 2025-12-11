package com.example.realtimeLedger.repository;

import com.example.realtimeLedger.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(UUID transactionId);
    Optional<Transaction> findByIdempotencyKey(String key);
    List<Transaction> findByFromAccountOrToAccount(UUID from, UUID to);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :acc OR t.toAccount = :acc")
    Page<Transaction> findByAccount(@Param("acc") UUID accountId, Pageable pageable);

}
