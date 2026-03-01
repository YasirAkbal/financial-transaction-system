package com.yasirakbal.accountservice.application.controller;

import com.yasirakbal.accountservice.application.dto.AccountResponse;
import com.yasirakbal.accountservice.application.dto.CreateAccountRequest;
import com.yasirakbal.accountservice.application.dto.CreditRequest;
import com.yasirakbal.accountservice.application.dto.DebitRequest;
import com.yasirakbal.accountservice.application.service.AccountService;
import com.yasirakbal.accountservice.domain.aggregate.Account;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/{customerId}")
    public ResponseEntity<List<AccountResponse>> getCustomerAccounts(@Valid @NotNull @PathVariable UUID customerId) {

        List<Account> accountList = accountAppService.getCustomerAccounts(customerId);

        List<AccountResponse> response = accountList.stream()
                .map(AccountResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

}