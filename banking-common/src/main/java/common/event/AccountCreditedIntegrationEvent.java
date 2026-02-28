package common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Getter
public class AccountCreditedIntegrationEvent extends BaseIntegrationEvent {
    private final UUID creditedAccountId;
    private final UUID debitedAccountId;
    private final UUID creditedCustomerId;
    private final UUID debitedCustomerId;
    private final BigDecimal amount;
    private final Currency currency;

    public AccountCreditedIntegrationEvent(UUID creditedAccountId, UUID debitedAccountId,
                                           UUID creditedCustomerId, UUID debitedCustomerId,
                                           BigDecimal amount, Currency currency, String correlationId) {
        super(correlationId);
        this.creditedAccountId = creditedAccountId;
        this.debitedAccountId = debitedAccountId;
        this.creditedCustomerId = creditedCustomerId;
        this.debitedCustomerId = debitedCustomerId;
        this.amount = amount;
        this.currency = currency;
    }

    @JsonCreator
    public AccountCreditedIntegrationEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("occurredOn") LocalDateTime occurredOn,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("creditedAccountId") UUID creditedAccountId,
            @JsonProperty("debitedAccountId") UUID debitedAccountId,
            @JsonProperty("creditedCustomerId") UUID creditedCustomerId,
            @JsonProperty("debitedCustomerId") UUID debitedCustomerId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") Currency currency) {
        super(id, occurredOn, correlationId, eventType);
        this.creditedAccountId = creditedAccountId;
        this.debitedAccountId = debitedAccountId;
        this.creditedCustomerId = creditedCustomerId;
        this.debitedCustomerId = debitedCustomerId;
        this.amount = amount;
        this.currency = currency;
    }
}