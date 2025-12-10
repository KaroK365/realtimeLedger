package com.example.realtimeLedger.controller;

import com.example.realtimeLedger.dto.TransferRequest;
import com.example.realtimeLedger.service.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final LedgerService ledgerService;

    @PostMapping
    public Map<String, Object> transfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid TransferRequest request
    ) {
        UUID txId = ledgerService.transfer(request, idempotencyKey);

        return Map.of(
                "transactionId", txId.toString(),
                "status", "SUCCESS"
        );
    }
}
