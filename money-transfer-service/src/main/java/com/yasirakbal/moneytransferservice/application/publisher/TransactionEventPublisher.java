package com.yasirakbal.moneytransferservice.application.publisher;

import com.yasirakbal.moneytransferservice.domain.events.TransactionCompletedEvent;
import com.yasirakbal.moneytransferservice.domain.events.TransactionFailedEvent;
import com.yasirakbal.moneytransferservice.domain.events.TransactionInitiatedEvent;
import common.command.DebitCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TRANSFER_COMMANDS = "transfer-commands";
    private static final String TRANSFER_EVENTS = "transfer-events";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransactionInitiated(TransactionInitiatedEvent event) {

        var debitCommand = new DebitCommand(
                event.getTransactionId(),
                event.getSourceAccountId(),
                event.getTargetAccountId(),
                event.getTargetCustomerId(),
                event.getAmount(),
                event.getCurrency(),
                event.getCorrelationId()
        );

        kafkaTemplate.send(
                TRANSFER_COMMANDS,
                event.getSourceAccountId().toString(),
                debitCommand
        );

        log.info("[Saga] DebitCommand sent. transactionId={}, sourceAccount={}",
                event.getTransactionId(), event.getSourceAccountId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransactionCompleted(TransactionCompletedEvent event) {
        kafkaTemplate.send(TRANSFER_EVENTS, event.getTransactionId().toString(), event);
        log.info("[Saga] Transfer completed. transactionId={}", event.getTransactionId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransactionFailed(TransactionFailedEvent event) {
        kafkaTemplate.send(TRANSFER_EVENTS, event.getTransactionId().toString(), event);
        log.warn("[Saga] Transfer failed. transactionId={}, reason={}",
                event.getTransactionId(), event.getErrorMessage());
    }
}