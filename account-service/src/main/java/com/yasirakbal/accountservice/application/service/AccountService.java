package com.yasirakbal.accountservice.application.service;

import com.yasirakbal.accountservice.application.dto.CreateAccountRequest;
import com.yasirakbal.accountservice.domain.aggregate.Account;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.OperationType;
import com.yasirakbal.accountservice.domain.infrastructure.idempotency.ProcessedMessage;
import com.yasirakbal.accountservice.domain.infrastructure.repository.AccountRepository;
import com.yasirakbal.accountservice.domain.infrastructure.repository.ProcessedMessageRepository;
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
    private final ProcessedMessageRepository processedMessageRepository;

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        String corrId = MDC.get(GeneralConstants.corrId);

        try {
            processedMessageRepository.save(ProcessedMessage.builder()
                    .transactionId(corrId)
                    .operationType(OperationType.CREATE)
                    .serviceName("account-service")
                    .processedAt(LocalDateTime.now())
                    .build());
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
            processedMessageRepository.save(ProcessedMessage.builder()
                    .transactionId(transactionId.toString())
                    .operationType(OperationType.DEBIT)
                    .serviceName("account-service")
                    .processedAt(LocalDateTime.now())
                    .build()
            );
        } catch(DataIntegrityViolationException exception) {
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
        try {
            processedMessageRepository.save(ProcessedMessage.builder()
                    .transactionId(transactionId.toString())
                    .operationType(OperationType.CREDIT)
                    .serviceName("account-service")
                    .processedAt(LocalDateTime.now())
                    .build()
            );
        } catch(DataIntegrityViolationException exception) {
            log.info("Credit already processed for {}", transactionId);
            return;
        }

        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.creditAccount(sourceId, sourceCustId, Money.of(amount, currency), corrId);
        accountRepository.save(account);
    }
}
