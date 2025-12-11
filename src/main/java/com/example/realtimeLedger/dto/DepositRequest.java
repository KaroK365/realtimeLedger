package com.example.realtimeLedger.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class DepositRequest {
    @NotNull
    private UUID accountId;
    @Positive
    private long amount;
}
