package com.yasirakbal.accountservice.application.publisher;


import com.yasirakbal.accountservice.domain.event.AccountCreatedEvent;
import com.yasirakbal.accountservice.domain.event.AccountCreditedEvent;
import com.yasirakbal.accountservice.domain.event.AccountDebitCompensatedEvent;
import com.yasirakbal.accountservice.domain.event.AccountDebitedEvent;
import common.event.AccountCreatedIntegrationEvent;
import common.event.AccountCreditedIntegrationEvent;
import common.event.AccountDebitCompensatedIntegrationEvent;
import common.event.AccountDebitedIntegrationEvent;
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
    public void publishToKafka(AccountCreatedEvent domainEvent) {

        var integrationEvent = new AccountCreatedIntegrationEvent(
                domainEvent.getCorrelationId(),
                domainEvent.getAccountId(),
                domainEvent.getCustomerId()
        );

        kafka.send(TOPIC, domainEvent.getAccountId().toString(), integrationEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishToKafka(AccountCreditedEvent domainEvent) {

        var integrationEvent = new AccountCreditedIntegrationEvent(
                domainEvent.getCreditedAccountId(),
                domainEvent.getDebitedAccountId(),
                domainEvent.getCreditedCustomerId(),
                domainEvent.getDebitedCustomerId(),
                domainEvent.getAmount().amount(),
                domainEvent.getAmount().currency(),
                domainEvent.getCorrelationId()
        );

        kafka.send(TOPIC, domainEvent.getCreditedAccountId().toString(), integrationEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishToKafka(AccountDebitedEvent domainEvent) {

        var integrationEvent = new AccountDebitedIntegrationEvent(
                domainEvent.getCreditedAccountId(),
                domainEvent.getDebitedAccountId(),
                domainEvent.getCreditedCustomerId(),
                domainEvent.getDebitedCustomerId(),
                domainEvent.getAmount().amount(),
                domainEvent.getAmount().currency(),
                domainEvent.getCorrelationId()
        );

        kafka.send(TOPIC, domainEvent.getDebitedAccountId().toString(), integrationEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishToKafka(AccountDebitCompensatedEvent domainEvent) {
        var integrationEvent = new AccountDebitCompensatedIntegrationEvent(
                domainEvent.getCorrelationId(),
                domainEvent.getCompensatedAccountId(),
                domainEvent.getRelatedAccountId(),
                domainEvent.getAmount().amount(),
                domainEvent.getAmount().currency()
        );
        kafka.send(TOPIC, domainEvent.getCompensatedAccountId().toString(), integrationEvent);
    }
}
