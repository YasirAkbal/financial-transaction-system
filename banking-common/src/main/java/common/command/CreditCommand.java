package common.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditCommand(
        UUID transactionId,
        UUID accountId,
        UUID sourceAccountId,
        UUID sourceCustomerId,
        BigDecimal amount,
        String currency,
        String correlationId
) {}