package com.yasirakbal.accountservice.application.publisher;

import com.yasirakbal.accountservice.application.event.BalanceCreditedIntegrationEvent;
import com.yasirakbal.accountservice.domain.event.BalanceCreditedEvent;
import com.yasirakbal.accountservice.shared.constant.GeneralConstants;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AccountEventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafka;
    private static final String TOPIC = "account-events";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishToKafka(BalanceCreditedEvent domainEvent) {

        var integrationEvent = new BalanceCreditedIntegrationEvent(
                domainEvent.getCreditedAccountId(),
                domainEvent.getDebitedAccountId(),
                domainEvent.getCreditedCustomerId(),
                domainEvent.getDebitedCustomerId(),
                domainEvent.getAmount().amount(),
                domainEvent.getAmount().currency(),
                domainEvent.getCorrelationId()
        );

        kafka.send("TOPIC", domainEvent.getCreditedAccountId().toString(), integrationEvent);
    }
}
