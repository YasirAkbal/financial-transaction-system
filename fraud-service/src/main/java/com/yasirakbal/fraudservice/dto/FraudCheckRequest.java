package com.yasirakbal.fraudservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FraudCheckRequest(
        UUID accountId,
        BigDecimal amount
) {}












