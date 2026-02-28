package com.yasirakbal.moneytransferservice.application.service;

import com.yasirakbal.moneytransferservice.domain.aggregate.Transaction;
import com.yasirakbal.moneytransferservice.domain.infrastructure.repository.TransactionRepository;
import com.yasirakbal.moneytransferservice.domain.valueobject.Money;
import common.constant.GeneralConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction makeTransaction(
            UUID sourceAccountId, UUID sourceCustomerId,
            UUID targetAccountId, UUID targetCustomerId,
            BigDecimal amount, String currency
    ) {

        Transaction transaction = Transaction.create(
                sourceAccountId, sourceCustomerId,
                targetAccountId, targetCustomerId,
                Money.of(amount, currency),
                MDC.get(GeneralConstants.corrId)
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
