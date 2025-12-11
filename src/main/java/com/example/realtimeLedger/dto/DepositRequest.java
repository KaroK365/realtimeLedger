package com.example.realtimeLedger.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DepositRequest {
    private UUID accountId;
    private long amount;
}
