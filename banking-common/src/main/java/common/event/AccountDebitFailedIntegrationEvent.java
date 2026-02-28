package common.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AccountDebitFailedIntegrationEvent extends BaseIntegrationEvent {
    private final UUID transactionId;
    private final UUID accountId;
    private final String errorCode;
    private final String errorMessage;

    public AccountDebitFailedIntegrationEvent(String correlationId, UUID transactionId, UUID accountId, String errorCode, String errorMessage) {
        super(correlationId);
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}