package common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AccountDebitFailedIntegrationEvent extends BaseIntegrationEvent {
    private final UUID transactionId;
    private final UUID accountId;
    private final String errorCode;
    private final String errorMessage;

    public AccountDebitFailedIntegrationEvent(String correlationId, UUID transactionId,
                                              UUID accountId, String errorCode, String errorMessage) {
        super(correlationId);
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @JsonCreator
    public AccountDebitFailedIntegrationEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("occurredOn") LocalDateTime occurredOn,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("transactionId") UUID transactionId,
            @JsonProperty("accountId") UUID accountId,
            @JsonProperty("errorCode") String errorCode,
            @JsonProperty("errorMessage") String errorMessage) {
        super(id, occurredOn, correlationId, eventType);
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}