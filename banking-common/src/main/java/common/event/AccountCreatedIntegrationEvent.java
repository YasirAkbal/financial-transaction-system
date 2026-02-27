package common.event;
import lombok.Getter;

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
}
