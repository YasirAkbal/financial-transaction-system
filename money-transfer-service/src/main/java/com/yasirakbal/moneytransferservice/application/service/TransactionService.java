package com.yasirakbal.moneytransferservice.application.service;

import com.yasirakbal.moneytransferservice.domain.aggregate.Transaction;
import com.yasirakbal.moneytransferservice.domain.infrastructure.repository.TransactionRepository;
import com.yasirakbal.moneytransferservice.domain.valueobject.Money;
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

    @Transactional
    public Transaction makeTransaction(
            UUID sourceAccountId, UUID sourceCustomerId,
            UUID targetAccountId, UUID targetCustomerId,
            BigDecimal amount, String currency
    ) {

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

        return transaction;
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Transaction not found: " + transactionId));
    }
}
