package common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Getter
public class AccountDebitCompensatedIntegrationEvent extends BaseIntegrationEvent {
    private final UUID compensatedAccountId;
    private final UUID relatedAccountId;
    private final BigDecimal amount;
    private final Currency currency;

    public AccountDebitCompensatedIntegrationEvent(String correlationId,
                                                   UUID compensatedAccountId,
                                                   UUID relatedAccountId,
                                                   BigDecimal amount,
                                                   Currency currency) {
        super(correlationId);
        this.compensatedAccountId = compensatedAccountId;
        this.relatedAccountId = relatedAccountId;
        this.amount = amount;
        this.currency = currency;
    }

    @JsonCreator
    public AccountDebitCompensatedIntegrationEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("occurredOn") LocalDateTime occurredOn,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("compensatedAccountId") UUID compensatedAccountId,
            @JsonProperty("relatedAccountId") UUID relatedAccountId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") Currency currency) {
        super(id, occurredOn, correlationId, eventType);
        this.compensatedAccountId = compensatedAccountId;
        this.relatedAccountId = relatedAccountId;
        this.amount = amount;
        this.currency = currency;
    }
}