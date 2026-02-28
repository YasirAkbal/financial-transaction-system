package common.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CompensateDebitCommand(
        UUID transactionId,
        UUID accountId,
        UUID targetAccountId,
        UUID targetCustomerId,
        BigDecimal amount,
        String currency,
        String correlationId
) {}