package com.yasirakbal.accountservice.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record DebitRequest(
        @NotNull UUID targetAccountId,
        @NotNull UUID targetCustomerId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currency
) {}