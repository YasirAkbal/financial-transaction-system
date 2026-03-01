package common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MoneyTransferFailedIntegrationEvent extends BaseIntegrationEvent {
    private final UUID transactionId;
    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final String errorCode; //INSUFFICIENT_FUNDS, ACCOUNT_BLOCKED etc.
    private final String errorMessage;
    private final String failedStep; //ACCOUNT_DEBIT, ACCOUNT_CREDIT etc.

    public MoneyTransferFailedIntegrationEvent(String correlationId, UUID transactionId,
                                               UUID sourceAccountId, UUID targetAccountId,
                                               BigDecimal amount, String currency, String errorCode, String errorMessage, String failedStep) {
        super(correlationId);
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.currency = currency;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.failedStep = failedStep;
    }

    @JsonCreator
    public MoneyTransferFailedIntegrationEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("occurredOn") LocalDateTime occurredOn,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("transactionId") UUID transactionId,
            @JsonProperty("sourceAccountId") UUID sourceAccountId,
            @JsonProperty("targetAccountId") UUID targetAccountId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("errorCode") String errorCode,
            @JsonProperty("errorMessage") String errorMessage,
            @JsonProperty("failedStep") String failedStep) {
        super(id, occurredOn, correlationId, eventType);
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.currency = currency;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.failedStep = failedStep;
    }
}