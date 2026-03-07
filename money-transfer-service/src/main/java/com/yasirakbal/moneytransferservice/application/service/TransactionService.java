package com.yasirakbal.moneytransferservice.application.service;

import com.yasirakbal.moneytransferservice.domain.aggregate.Transaction;
import com.yasirakbal.moneytransferservice.domain.infrastructure.transaction.TransactionRepository;
import com.yasirakbal.moneytransferservice.domain.valueobject.Money;
import common.command.DebitCommand;
import common.constant.GeneralConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final FraudService fraudService;
    private final OutboxService outboxService;

    @Transactional
    public Transaction makeTransaction(
            UUID sourceAccountId, UUID sourceCustomerId,
            UUID targetAccountId, UUID targetCustomerId,
            BigDecimal amount, String currency
    ) {

        if (fraudService.isFraudulent(sourceAccountId, amount)) {
            throw new IllegalArgumentException("Transaction flagged as fraudulent");
        }

        String corrId = MDC.get(GeneralConstants.corrId);

        var existing = transactionRepository.findByCorrelationId(corrId);
        if(existing.isPresent()) {
            log.info("Transaction already processed for corrId: {}", corrId);
            throw new IllegalArgumentException("The request is already processed");
        }

        Transaction transaction = Transaction.create(
                sourceAccountId, sourceCustomerId,
                targetAccountId, targetCustomerId,
                Money.of(amount, currency),
                corrId
        );
        transaction.markDebitSent();
        transactionRepository.save(transaction);


        var debitCommand = new DebitCommand(
                transaction.getId(),
                sourceAccountId,
                targetAccountId,
                targetCustomerId,
                amount,
                currency,
                corrId
        );
        outboxService.saveOutbox(
                "transfer-commands",
                sourceAccountId.toString(),
                "DebitCommand",
                debitCommand
        );


        return transaction;
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Transaction not found: " + transactionId));
    }
}
