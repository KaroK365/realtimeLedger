package com.example.realtimeLedger.controller;

import com.example.realtimeLedger.dto.DepositRequest;
import com.example.realtimeLedger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepositController {

    private final LedgerService ledgerService;

    @PostMapping("/deposit")
    public Object deposit(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody DepositRequest req
    ) {
        UUID txId = ledgerService.deposit(req.getAccountId(), req.getAmount(), idempotencyKey);
        return Map.of("status", "SUCCESS", "transactionId", txId);
    }
}
