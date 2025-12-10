package com.example.realtimeLedger.service;

import com.example.realtimeLedger.dto.AccountResponse;
import com.example.realtimeLedger.dto.CreateAccountRequest;
import com.example.realtimeLedger.entity.Account;
import com.example.realtimeLedger.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {

        Account account = Account.builder()
                .accountId(UUID.randomUUID())
                .userId(UUID.fromString(request.getUserId()))
                .balance(0L)
                .currency(request.getCurrency())
                .build();

        accountRepository.save(account);

        return AccountResponse.builder()
                .accountId(account.getAccountId().toString())
                .userId(account.getUserId().toString())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt() != null ? account.getCreatedAt().toString() : null)
                .build();
    }

    public AccountResponse getAccount(UUID accountId) {

        Account acc = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return AccountResponse.builder()
                .accountId(acc.getAccountId().toString())
                .userId(acc.getUserId().toString())
                .balance(acc.getBalance())
                .currency(acc.getCurrency())
                .createdAt(acc.getCreatedAt() != null ? acc.getCreatedAt().toString() : null)
                .build();
    }
}
