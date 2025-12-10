package com.example.realtimeLedger.controller;

import com.example.realtimeLedger.dto.AccountResponse;
import com.example.realtimeLedger.dto.CreateAccountRequest;
import com.example.realtimeLedger.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    //post
    @PostMapping
    public AccountResponse createAccount(@RequestBody @Valid CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable String accountId) {
        return accountService.getAccount(UUID.fromString(accountId));
    }
}
