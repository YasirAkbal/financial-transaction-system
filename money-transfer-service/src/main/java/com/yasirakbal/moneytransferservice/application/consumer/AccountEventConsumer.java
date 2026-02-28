package com.yasirakbal.moneytransferservice.application.consumer;

import com.yasirakbal.moneytransferservice.domain.aggregate.Transaction;
import com.yasirakbal.moneytransferservice.domain.enums.TransferStep;
import com.yasirakbal.moneytransferservice.domain.infrastructure.repository.TransactionRepository;
import common.command.CompensateDebitCommand;
import common.command.CreditCommand;
import common.event.AccountCreditFailedIntegrationEvent;
import common.event.AccountCreditedIntegrationEvent;
import common.event.AccountDebitFailedIntegrationEvent;
import common.event.AccountDebitedIntegrationEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "account-events", groupId = "money-transfer-group")
public class AccountEventConsumer {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TRANSFER_COMMANDS = "transfer-commands";

    @KafkaHandler
    @Transactional
    public void onAccountDebited(AccountDebitedIntegrationEvent event) {
        log.info("[Saga] AccountDebited is received. corrId={}", event.getCorrelationId());

        Transaction transaction = findTransaction(event.getCorrelationId());

        if (transaction.getCurrentStep() != TransferStep.DEBIT_SENT) {
            log.warn("[Saga] Unexpected step. current step={}, corrId={}",
                    transaction.getCurrentStep(), event.getCorrelationId());
            return;
        }

        transaction.markDebitCompleted();

        var creditCommand = new CreditCommand(
                transaction.getId(),
                transaction.getTargetAccountId(),
                transaction.getSourceAccountId(),
                transaction.getSourceCustomerId(),
                transaction.getAmount().amount(),
                transaction.getAmount().currency().toString(),
                event.getCorrelationId()
        );

        transactionRepository.save(transaction);

        kafkaTemplate.send(
                TRANSFER_COMMANDS,
                transaction.getTargetAccountId().toString(),
                creditCommand
        );

        log.info("[Saga] CreditCommand sent. transactionId={}, targetAccount={}",
                transaction.getId(), transaction.getTargetAccountId());
    }

    @KafkaHandler
    @Transactional
    public void onAccountCredited(AccountCreditedIntegrationEvent event) {
        log.info("[Saga] AccountCredited is received. corrId={}", event.getCorrelationId());

        Transaction transaction = findTransaction(event.getCorrelationId());

        if (transaction.getCurrentStep() != TransferStep.CREDIT_SENT) {
            log.warn("[Saga] Unexpected step. current={}, corrId={}",
                    transaction.getCurrentStep(), event.getCorrelationId());
            return;
        }

        transaction.complete(event.getCorrelationId());
        transactionRepository.save(transaction);

        log.info("[Saga] Transfer is completed. transactionId={}", transaction.getId());
    }

    @KafkaHandler
    @Transactional
    public void onAccountDebitFailed(AccountDebitFailedIntegrationEvent event) {
        log.warn("[Saga] AccountDebitFailed. errorCode={}, corrId={}",
                event.getErrorCode(), event.getCorrelationId());

        Transaction transaction = findTransaction(event.getCorrelationId());

        transaction.fail(
                event.getErrorCode(),
                event.getErrorMessage(),
                "ACCOUNT_DEBIT",
                event.getCorrelationId()
        );
        transactionRepository.save(transaction);

        log.warn("[Saga] Transfer is unsuccessful (in debit step). transactionId={}", transaction.getId());
    }

    @KafkaHandler
    @Transactional
    public void onAccountCreditFailed(AccountCreditFailedIntegrationEvent event) {
        log.error("[Saga] AccountCreditFailed! Compensation is starting. corrId={}",
                event.getCorrelationId());

        Transaction transaction = findTransaction(event.getCorrelationId());

        var compensateCommand = new CompensateDebitCommand(
                transaction.getId(),
                transaction.getSourceAccountId(),
                transaction.getTargetAccountId(),
                transaction.getTargetCustomerId(),
                transaction.getAmount().amount(),
                transaction.getAmount().currency().toString(),
                event.getCorrelationId()
        );

        transaction.fail(
                event.getErrorCode(),
                event.getErrorMessage(),
                "ACCOUNT_CREDIT",
                event.getCorrelationId()
        );
        transactionRepository.save(transaction);

        kafkaTemplate.send(
                TRANSFER_COMMANDS,
                transaction.getSourceAccountId().toString(),
                compensateCommand
        );

        log.error("[Saga] CompensateDebitCommand sent. transactionId={}", transaction.getId());
    }

    // ─── Yardımcı metot ──────────────────────────────────────────────────────
    private Transaction findTransaction(String correlationId) {
        return transactionRepository
                .findByCorrelationId(correlationId)
                .orElseThrow(() -> {
                    log.error("[Saga] Transaction is not found! corrId={}", correlationId);
                    return new EntityNotFoundException(
                            "Transaction not found for correlationId: " + correlationId);
                });
    }
}