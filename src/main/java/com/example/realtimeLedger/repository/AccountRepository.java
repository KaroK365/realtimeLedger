package com.example.realtimeLedger.repository;

import com.example.realtimeLedger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(UUID accountId);
}
