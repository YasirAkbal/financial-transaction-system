package com.yasirakbal.accountservice.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditRequest(
        @NotNull UUID sourceAccountId,
        @NotNull UUID sourceCustomerId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currency
) {}