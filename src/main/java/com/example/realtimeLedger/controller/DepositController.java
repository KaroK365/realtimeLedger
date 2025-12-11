package com.example.realtimeLedger.controller;

import com.example.realtimeLedger.dto.DepositRequest;
import com.example.realtimeLedger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepositController {

    private final LedgerService ledgerService;

    @PostMapping("/deposit")
    public Object deposit(@RequestBody DepositRequest req) {
        UUID tx = ledgerService.deposit(req.getAccountId(), req.getAmount());
        return new Object() {
            public final String status = "SUCCESS";
            public final UUID transactionId = tx;
        };
    }
}
