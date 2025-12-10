package com.example.realtimeLedger.service;

import com.example.realtimeLedger.dto.TransferRequest;
import com.example.realtimeLedger.entity.Account;
import com.example.realtimeLedger.entity.LedgerEntry;
import com.example.realtimeLedger.entity.Transaction;
import com.example.realtimeLedger.repository.AccountRepository;
import com.example.realtimeLedger.repository.LedgerEntryRepository;
import com.example.realtimeLedger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    @Qualifier("redisTemplate")
    private final RedisTemplate<String, String> redis;

    @Transactional
    public UUID transfer(TransferRequest request, String idempotencyKey) {

        // 1️⃣ Idempotency check
        String existingTx = redis.opsForValue().get("idemp:" + idempotencyKey);
        if (existingTx != null) {
            return UUID.fromString(existingTx);
        }

        // 2️⃣ Create initial transaction (PENDING)
        UUID txId = UUID.randomUUID();

        Transaction tx = Transaction.builder()
                .transactionId(txId)
                .fromAccount(UUID.fromString(request.getFromAccount()))
                .toAccount(UUID.fromString(request.getToAccount()))
                .amount(request.getAmount())
                .currency("INR")
                .status("PENDING")
                .idempotencyKey(idempotencyKey)
                .build();

        transactionRepository.save(tx);

        // 3️⃣ Lock accounts and validate
        Account from = accountRepository.findByAccountId(UUID.fromString(request.getFromAccount()))
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account to = accountRepository.findByAccountId(UUID.fromString(request.getToAccount()))
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (from.getBalance() < request.getAmount()) {
            tx.setStatus("FAILED");
            transactionRepository.save(tx);
            throw new RuntimeException("Insufficient balance");
        }

        // 4️⃣ Apply ledger entries (double entry)
        ledgerEntryRepository.save(
                LedgerEntry.builder()
                        .entryId(UUID.randomUUID())
                        .transactionId(txId)
                        .accountId(from.getAccountId())
                        .delta(-request.getAmount())
                        .build()
        );

        ledgerEntryRepository.save(
                LedgerEntry.builder()
                        .entryId(UUID.randomUUID())
                        .transactionId(txId)
                        .accountId(to.getAccountId())
                        .delta(request.getAmount())
                        .build()
        );

        // 5️⃣ Update balances
        from.setBalance(from.getBalance() - request.getAmount());
        to.setBalance(to.getBalance() + request.getAmount());

        accountRepository.save(from);
        accountRepository.save(to);

        // 6️⃣ Update transaction status
        tx.setStatus("SUCCESS");
        transactionRepository.save(tx);

        // 7️⃣ Store idempotency result (24h)
        redis.opsForValue().set("idemp:" + idempotencyKey, txId.toString());

        return txId;
    }
}
