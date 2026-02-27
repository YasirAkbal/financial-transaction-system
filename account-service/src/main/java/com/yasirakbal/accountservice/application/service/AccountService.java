package com.yasirakbal.accountservice.application.service;

import com.yasirakbal.accountservice.application.dto.CreateAccountRequest;
import com.yasirakbal.accountservice.domain.aggregate.Account;
import com.yasirakbal.accountservice.domain.infrastructure.repository.AccountRepository;
import com.yasirakbal.accountservice.domain.valueobject.Money;
import com.yasirakbal.accountservice.shared.constant.GeneralConstants;
import com.yasirakbal.accountservice.shared.util.AccountNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        Long accountNumber = accountNumberGenerator.generate();

        Account createdAccount = Account.create(
                request.customerId(),
                accountNumber,
                Currency.getInstance(request.currency()),
                MDC.get(GeneralConstants.corrId)
        );

        accountRepository.save(createdAccount);
        return createdAccount;
    }

    @Transactional
    public void debit(UUID accountId, UUID targetId, UUID targetCustId, BigDecimal amount, String currency) {
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.debitAccount(targetId, targetCustId, Money.of(amount, currency), MDC.get(GeneralConstants.corrId));
        accountRepository.save(account);
    }

    @Transactional
    public void credit(UUID accountId, UUID sourceId, UUID sourceCustId, BigDecimal amount, String currency) {
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        account.creditAccount(sourceId, sourceCustId, Money.of(amount, currency), MDC.get(GeneralConstants.corrId));
        accountRepository.save(account);
    }

}
