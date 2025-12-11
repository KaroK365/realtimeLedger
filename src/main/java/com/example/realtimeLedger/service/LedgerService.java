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


    // ============================================================
    //  TRANSFER (DOUBLE ENTRY + IDEMPOTENCY)
    // ============================================================
    @Transactional
    public UUID transfer(TransferRequest request, String idempotencyKey) {

        // 1️⃣ Idempotency check
        String existingTx = redis.opsForValue().get("idemp:" + idempotencyKey);
        if (existingTx != null) {
            return UUID.fromString(existingTx);
        }

        // 2️⃣ Prepare transaction record
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

        // 3️⃣ Validate sender & receiver
        Account from = accountRepository.findByAccountId(UUID.fromString(request.getFromAccount()))
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account to = accountRepository.findByAccountId(UUID.fromString(request.getToAccount()))
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (from.getBalance() < request.getAmount()) {
            tx.setStatus("FAILED");
            transactionRepository.save(tx);
            throw new RuntimeException("Insufficient balance");
        }

        // 4️⃣ Create Ledger entries
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

        // 5️⃣ Update Balances
        from.setBalance(from.getBalance() - request.getAmount());
        to.setBalance(to.getBalance() + request.getAmount());

        accountRepository.save(from);
        accountRepository.save(to);

        // 6️⃣ Mark transaction as SUCCESS
        tx.setStatus("SUCCESS");
        transactionRepository.save(tx);

        // 7️⃣ Save idempotency key
        redis.opsForValue().set("idemp:" + idempotencyKey, txId.toString());

        return txId;
    }


    // ============================================================
    //  DEPOSIT (SINGLE ENTRY + BALANCE UPDATE)
    // ============================================================
    @Transactional
    public UUID deposit(UUID accountId, long amount, String idempotencyKey) {

        // 🔒 1. Idempotency check
        String existingTx = redis.opsForValue().get("idemp:" + idempotencyKey);
        if (existingTx != null) {
            return UUID.fromString(existingTx);
        }

        // 🔎 2. Validate amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account acc = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 🆔 3. Create transaction
        UUID txId = UUID.randomUUID();

        // 🧾 4. Ledger entry
        ledgerEntryRepository.save(
                LedgerEntry.builder()
                        .entryId(UUID.randomUUID())
                        .transactionId(txId)
                        .accountId(accountId)
                        .delta(amount)
                        .build()
        );

        // 💰 5. Update balance
        acc.setBalance(acc.getBalance() + amount);
        accountRepository.save(acc);

        // 📝 6. Save Transaction record
        Transaction tx = Transaction.builder()
                .transactionId(txId)
                .fromAccount(accountId)  // TEMP: schema requires non-null
                .toAccount(accountId)
                .amount(amount)
                .currency("INR")
                .status("SUCCESS")
                .idempotencyKey(idempotencyKey)
                .build();
        transactionRepository.save(tx);
        // 💾 7. Save idempotency
        redis.opsForValue().set("idemp:" + idempotencyKey, txId.toString());
        return txId;
    }
}
