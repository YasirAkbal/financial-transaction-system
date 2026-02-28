package common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MoneyTransferCompletedIntegrationEvent extends BaseIntegrationEvent {
    private final UUID transactionId;
    private final UUID sourceAccountId;
    private final UUID targetAccountId;
    private final BigDecimal amount;
    private final String currency;

    public MoneyTransferCompletedIntegrationEvent(String correlationId, UUID transactionId,
                                                  UUID sourceAccountId, UUID targetAccountId,
                                                  BigDecimal amount, String currency) {
        super(correlationId);
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.currency = currency;
    }

    @JsonCreator
    public MoneyTransferCompletedIntegrationEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("occurredOn") LocalDateTime occurredOn,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("transactionId") UUID transactionId,
            @JsonProperty("sourceAccountId") UUID sourceAccountId,
            @JsonProperty("targetAccountId") UUID targetAccountId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") String currency) {
        super(id, occurredOn, correlationId, eventType);
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.currency = currency;
    }
}