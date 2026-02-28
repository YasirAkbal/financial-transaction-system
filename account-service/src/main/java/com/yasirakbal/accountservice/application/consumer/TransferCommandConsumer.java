package com.yasirakbal.accountservice.application.consumer;

import com.yasirakbal.accountservice.application.service.AccountService;
import common.command.CompensateDebitCommand;
import common.command.CreditCommand;
import common.command.DebitCommand;
import common.event.AccountDebitFailedIntegrationEvent;
import common.event.AccountCreditFailedIntegrationEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "transfer-commands", groupId = "account-service-group")
public class TransferCommandConsumer {

    private final AccountService accountService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ACCOUNT_EVENTS = "account-events";

    @KafkaHandler
    @Transactional
    public void onDebitCommand(DebitCommand command) {
        log.info("[Command] DebitCommand is received. transactionId={}, accountId={}",
                command.transactionId(), command.accountId());
        try {
            accountService.debit(
                    command.transactionId(),
                    command.accountId(),
                    command.targetAccountId(),
                    command.targetCustomerId(),
                    command.amount(),
                    command.currency()
            );
            log.info("[Command] Debit is successful. transactionId={}", command.transactionId());

        } catch (IllegalArgumentException e) {
            log.warn("[Command] Debit is unsuccessful (business): {}, transactionId={}",
                    e.getMessage(), command.transactionId());
            publishDebitFailed(command, "INSUFFICIENT_FUNDS", e.getMessage());

        } catch (EntityNotFoundException e) {
            log.warn("[Command] Account is not found. accountId={}", command.accountId());
            publishDebitFailed(command, "ACCOUNT_NOT_FOUND", e.getMessage());

        } catch (Exception e) {
            log.error("[Command] Debit unknown technical error. transactionId={}", command.transactionId(), e);
            publishDebitFailed(command, "TECHNICAL_ERROR", "Unexpected error: " + e.getMessage());
        }
    }

    @KafkaHandler
    @Transactional
    public void onCreditCommand(CreditCommand command) {
        log.info("[Command] CreditCommand is received. transactionId={}, accountId={}",
                command.transactionId(), command.accountId());
        try {
            accountService.credit(
                    command.transactionId(),
                    command.accountId(),
                    command.sourceAccountId(),
                    command.sourceCustomerId(),
                    command.amount(),
                    command.currency()
            );
            log.info("[Command] Credit is successful. transactionId={}", command.transactionId());

        } catch (EntityNotFoundException e) {
            log.warn("[Command] Account is not found. accountId={}", command.accountId());
            publishCreditFailed(command, "ACCOUNT_NOT_FOUND", e.getMessage());

        } catch (Exception e) {
            log.error("[Command] Credit, unknown technical error. transactionId={}", command.transactionId(), e);
            publishCreditFailed(command, "TECHNICAL_ERROR", "Unexpected error: " + e.getMessage());
        }
    }

    @KafkaHandler
    @Transactional
    public void onCompensateDebitCommand(CompensateDebitCommand command) {
        log.info("[Command] CompensateDebitCommand is received. transactionId={}, accountId={}",
                command.transactionId(), command.accountId());
        try {
            UUID compensateTransactionId = UUID.nameUUIDFromBytes(
                    (command.transactionId().toString() + "_COMPENSATE").getBytes()
            );

            accountService.credit(
                    compensateTransactionId,
                    command.accountId(),
                    command.targetAccountId(),
                    command.targetCustomerId(),
                    command.amount(),
                    command.currency()
            );

            log.info("[Command] Compensation is successful. transactionId={}",
                    command.transactionId());

        } catch (Exception e) {
            log.error("[CRITICAL] Compensation Failed! Manuel operation is needed. " +
                            "transactionId={}, accountId={}, amount={}",
                    command.transactionId(), command.accountId(), command.amount(), e);
            kafkaTemplate.send(
                    "transfer-commands.DLQ",
                    command.transactionId().toString(),
                    command
            );
        }
    }

    private void publishDebitFailed(DebitCommand command, String errorCode, String message) {
        var failedEvent = new AccountDebitFailedIntegrationEvent(
                command.correlationId(),
                command.transactionId(),
                command.accountId(),
                errorCode,
                message
        );
        kafkaTemplate.send(ACCOUNT_EVENTS, command.accountId().toString(), failedEvent);
    }

    private void publishCreditFailed(CreditCommand command, String errorCode, String message) {
        var failedEvent = new AccountCreditFailedIntegrationEvent(
                command.correlationId(),
                command.transactionId(),
                command.accountId(),
                errorCode,
                message
        );
        kafkaTemplate.send(ACCOUNT_EVENTS, command.accountId().toString(), failedEvent);
    }
}