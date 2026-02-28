package common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountCreatedIntegrationEvent extends BaseIntegrationEvent {
    private final UUID accountId;
    private final UUID customerId;

    public AccountCreatedIntegrationEvent(String correlationId, UUID accountId, UUID customerId) {
        super(correlationId);
        this.accountId = accountId;
        this.customerId = customerId;
    }

    @JsonCreator
    public AccountCreatedIntegrationEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("occurredOn") LocalDateTime occurredOn,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("accountId") UUID accountId,
            @JsonProperty("customerId") UUID customerId) {
        super(id, occurredOn, correlationId, eventType);
        this.accountId = accountId;
        this.customerId = customerId;
    }
}