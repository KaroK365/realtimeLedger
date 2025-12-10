package com.example.realtimeLedger.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {

    private String accountId;
    private String userId;
    private long balance;
    private String currency;
    private String createdAt;
}
