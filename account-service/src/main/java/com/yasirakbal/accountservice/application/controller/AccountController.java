package com.yasirakbal.accountservice.application.controller;

import com.yasirakbal.accountservice.application.dto.AccountResponse;
import com.yasirakbal.accountservice.application.dto.CreateAccountRequest;
import com.yasirakbal.accountservice.application.dto.CreditRequest;
import com.yasirakbal.accountservice.application.dto.DebitRequest;
import com.yasirakbal.accountservice.application.service.AccountService;
import com.yasirakbal.accountservice.domain.aggregate.Account;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountAppService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {

        Account createdAccount = accountAppService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AccountResponse.fromEntity(createdAccount));
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<Void> debit(@PathVariable UUID id, @Valid @RequestBody DebitRequest request) {

        accountAppService.debit(
                id,
                request.targetAccountId(),
                request.targetCustomerId(),
                request.amount(),
                request.currency()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<Void> credit(@PathVariable UUID id, @Valid @RequestBody CreditRequest request) {

        accountAppService.credit(
                id,
                request.sourceAccountId(),
                request.sourceCustomerId(),
                request.amount(),
                request.currency()
        );

        return ResponseEntity.ok().build();
    }
}