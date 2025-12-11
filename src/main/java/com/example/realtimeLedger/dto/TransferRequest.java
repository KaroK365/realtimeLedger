package com.example.realtimeLedger.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferRequest {

    @NotNull
    private String fromAccount;

    @NotNull
    private String toAccount;

    @Positive
    private long amount; // paise eg: 50000 = ₹500
}
