package com.yasirakbal.accountservice.application.service;

import com.yasirakbal.accountservice.application.dto.CreateAccountRequest;
import com.yasirakbal.accountservice.domain.aggregate.Account;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.IdempotencyService;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.OperationType;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.ProcessedMessage;
import com.yasirakbal.accountservice.domain.infrastructure.repository.AccountRepository;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.ProcessedMessageRepository;
import com.yasirakbal.accountservice.domain.valueobject.Money;
import com.yasirakbal.accountservice.shared.util.AccountNumberGenerator;
import common.constant.GeneralConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final IdempotencyService idempotencyService;
    private static int count = 0;

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        String corrId = MDC.get(GeneralConstants.corrId);

        try {
            idempotencyService.record(corrId, OperationType.CREATE, "account-service");
        } catch (DataIntegrityViolationException e) {
            log.info("Account creation already processed for corrId: {}", corrId);
            throw new IllegalArgumentException("The request is already processed");
        }

        Long accountNumber = accountNumberGenerator.generate();
        Account createdAccount = Account.create(
                request.customerId(),
                accountNumber,
                Currency.getInstance(request.currency()),
                request.initialBalance(),
                corrId
        );

        accountRepository.save(createdAccount);

        return createdAccount;
    }

    @Transactional
    public void debit(UUID transactionId, UUID accountId, UUID targetId, UUID targetCustId, BigDecimal amount, String currency, String corrId) {
        try {
            idempotencyService.record(corrId, OperationType.DEBIT, "account-service");
        } catch (DataIntegrityViolationException e) {
            log.info("Debit already processed for {}", transactionId);
            return;
        }

        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.debitAccount(targetId, targetCustId, Money.of(amount, currency), corrId);
        accountRepository.save(account);
    }

    @Transactional
    public void credit(UUID transactionId, UUID accountId, UUID sourceId, UUID sourceCustId, BigDecimal amount, String currency, String corrId) {
        credit(transactionId, accountId, sourceId, sourceCustId, amount, currency, corrId, OperationType.CREDIT);
    }

    @Transactional
    public void compensateCredit(UUID transactionId, UUID accountId, UUID sourceId, UUID sourceCustId, BigDecimal amount, String currency, String corrId) {
        credit(transactionId, accountId, sourceId, sourceCustId, amount, currency, corrId, OperationType.COMPENSATE);
    }

    private void credit(UUID transactionId, UUID accountId, UUID sourceId, UUID sourceCustId, BigDecimal amount, String currency, String corrId, OperationType operationType) {
        try {
            idempotencyService.record(corrId, operationType, "account-service");
        } catch (DataIntegrityViolationException e) {
            log.info("Credit already processed for {}", transactionId);
            return;
        }

        if(count == 0) {
            count++;
            throw new IllegalArgumentException("Test case için yazdım xd");
        }

        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.creditAccount(sourceId, sourceCustId, Money.of(amount, currency), corrId);
        accountRepository.save(account);
    }
}
