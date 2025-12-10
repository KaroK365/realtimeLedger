package com.example.realtimeLedger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank
    private String userId;
    private String currency = "INR";
}
