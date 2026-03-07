package com.yasirakbal.accountservice.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yasirakbal.accountservice.application.dto.CreateAccountRequest;
import com.yasirakbal.accountservice.domain.aggregate.Account;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.IdempotencyService;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.OperationType;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.AccountRepository;
import com.yasirakbal.accountservice.domain.infrastructure.outbox.OutboxMessage;
import com.yasirakbal.accountservice.domain.infrastructure.outbox.OutboxMessageRepository;
import com.yasirakbal.accountservice.domain.valueobject.Money;
import com.yasirakbal.accountservice.shared.util.AccountNumberGenerator;
import common.constant.GeneralConstants;
import common.event.AccountCreditedIntegrationEvent;
import common.event.AccountDebitCompensatedIntegrationEvent;
import common.event.AccountDebitedIntegrationEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final IdempotencyService idempotencyService;
    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;

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


        var integrationEvent = new AccountDebitedIntegrationEvent(
                account.getId(), targetId,
                account.getCustomerId(), targetCustId,
                amount, account.getBalance().currency(),
                corrId
        );
        saveOutbox(accountId.toString(), "AccountDebitedIntegrationEvent", integrationEvent);
    }

    @Transactional
    public void credit(UUID transactionId, UUID accountId, UUID sourceId, UUID sourceCustId, BigDecimal amount, String currency, String corrId) {
        try {
            idempotencyService.record(corrId, OperationType.CREDIT, "account-service");
        } catch (DataIntegrityViolationException e) {
            log.info("Credit already processed for {}", transactionId);
            return;
        }

        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        account.creditAccount(sourceId, sourceCustId, Money.of(amount, currency), corrId);
        accountRepository.save(account);

        var integrationEvent = new AccountCreditedIntegrationEvent(
                account.getId(), sourceId,
                account.getCustomerId(), sourceCustId,
                amount, account.getBalance().currency(),
                corrId
        );
        saveOutbox(accountId.toString(), "AccountCreditedIntegrationEvent", integrationEvent);
    }

    @Transactional
    public void compensateDebit(UUID transactionId, UUID accountId, UUID sourceId, UUID sourceCustId, BigDecimal amount, String currency, String corrId) {
        try {
            idempotencyService.record(corrId, OperationType.COMPENSATE, "account-service");
        } catch (DataIntegrityViolationException e) {
            log.info("Credit already compensated for {}", transactionId);
            return;
        }

        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        account.compensateDebit(sourceId, sourceCustId, Money.of(amount, currency), corrId);
        accountRepository.save(account);

        var integrationEvent = new AccountDebitCompensatedIntegrationEvent(
                corrId,
                sourceId,
                account.getId(),
                amount,
                account.getBalance().currency()
        );
        saveOutbox(accountId.toString(), "AccountDebitCompensatedIntegrationEvent", integrationEvent);
    }

    public List<Account> getCustomerAccounts(UUID customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    private void saveOutbox(String aggregateId, String eventType, Object event) {
        try {
            outboxMessageRepository.save(OutboxMessage.builder()
                    .topic("account-events")
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(event))
                    .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox event", e);
        }
    }
}
